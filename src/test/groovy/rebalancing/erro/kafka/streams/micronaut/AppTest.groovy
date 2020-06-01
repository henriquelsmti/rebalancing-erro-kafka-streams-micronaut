package rebalancing.erro.kafka.streams.micronaut

import io.micronaut.context.env.PropertySource
import io.micronaut.runtime.Micronaut
import org.apache.kafka.streams.KafkaStreams
import org.junit.Test
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.containers.Network
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.lifecycle.Startables

import java.util.stream.Stream

class AppTest {

    private static Network network = Network.newNetwork()


    public static KafkaContainer kafkaContainer = (KafkaContainer) new KafkaContainer("5.4.2")
            .withNetwork(network)
            .withNetworkAliases("kafka")

    public static GenericContainer schemaRegistryContainer = new GenericContainer<>("confluentinc/cp-schema-registry:5.4.0")
            .withNetwork(network)
            .dependsOn(kafkaContainer)
            .withNetworkAliases('schema-registry')
            .withEnv('SCHEMA_REGISTRY_HOST_NAME': 'schema-registry')
            .withEnv('SCHEMA_REGISTRY_MASTER_ELIGIBILITY': 'true')
            .withEnv('SCHEMA_REGISTRY_KAFKASTORE_BOOTSTRAP_SERVERS', "kafka:9092")
            .withExposedPorts(8181, 8081)

    @Test
    void test(){
        schemaRegistryContainer.setWaitStrategy(
                Wait.forLogMessage(".*INFO\\s*Server\\s*started,\\s*listening\\s*for\\s*requests.*", 1))

        Startables.deepStart(
                Stream.of(
                        kafkaContainer, schemaRegistryContainer
                )
        ).join()

       String scReUrl = String.format("http://%s:%s", schemaRegistryContainer.containerInfo.getNetworkSettings().getNetworks().values().stream()
                .collect { it }.find().getIpAddress(), '8081')

        Properties properties = new Properties()
        properties.put("SERVER_PORT", 7070)
        properties.put("KAFKA_BROKERS", kafkaContainer.getBootstrapServers())
        properties.put("SCHEMA_REGISTRY_URL", scReUrl)
        def context = Micronaut.build()
                .mainClass(Application)
                .propertySources(
                        PropertySource.of(properties as Map<String, Object>)
                )
                .start()
        Thread.sleep(7000)
        Properties properties2 = new Properties()
        properties2.put("SERVER_PORT", 7071)
        properties2.put("KAFKA_BROKERS", kafkaContainer.getBootstrapServers())
        properties2.put("SCHEMA_REGISTRY_URL", scReUrl)
        def context2 = Micronaut.build()
                .mainClass(Application)
                .propertySources(
                        PropertySource.of(properties2 as Map<String, Object>)
                )
                .start()

        KafkaStreams kafkaStreams1 = context.getBean(KafkaStreams)
        KafkaStreams kafkaStreams2 = context2.getBean(KafkaStreams)

        while (!(KafkaStreams.State.ERROR in [kafkaStreams1.state(), kafkaStreams2.state()]));
    }
}
