package rebalancing.erro.kafka.streams.micronaut

import groovy.transform.CompileStatic
import io.confluent.kafka.streams.serdes.avro.GenericAvroSerde
import org.apache.avro.generic.GenericRecord;
import io.micronaut.configuration.kafka.streams.ConfiguredStreamBuilder
import io.micronaut.configuration.kafka.streams.event.BeforeKafkaStreamStart
import io.micronaut.context.annotation.Factory
import io.micronaut.runtime.event.annotation.EventListener
import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.streams.kstream.KStream

import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Factory
class PortalKStreams {

    @Inject
    AdminClient adminClient


    @EventListener
    void onBeforeKafkaStreamStart(BeforeKafkaStreamStart event) {
        Set<NewTopic> topicosNaoExistentes = [
                new NewTopic(TopicosConstants.PORTAL_CLIENTE, 10, (short) 1),
                new NewTopic(TopicosConstants.PORTAL_GRUPO_CLIENTES, 10, (short) 1),
                new NewTopic(TopicosConstants.PORTAL_GRUPO_OPERADORAS, 10, (short) 1),
                new NewTopic(TopicosConstants.PORTAL_OPERADORA_ANS, 10, (short) 1),
                new NewTopic(TopicosConstants.PORTAL_OPERADORA_MULTITENANT, 10, (short) 1),
        ]
        try {
            adminClient.createTopics(topicosNaoExistentes).all().get()
        } catch (ignore) {
        }
    }

    @Singleton
    @Named("key-serde")
    GenericAvroSerde genericKeyAvroDeserializer(final ConfiguredStreamBuilder builder){
        GenericAvroSerde GenericAvroSerde = new GenericAvroSerde()
        GenericAvroSerde.configure(builder.configuration, true)
        genericAvroSerde
    }

    @Singleton
    @Named("value-serde")
    GenericAvroSerde genericValueAvroDeserializer(final ConfiguredStreamBuilder builder){
        GenericAvroSerde GenericAvroSerde = new GenericAvroSerde()
        GenericAvroSerde.configure(builder.configuration, false)
        genericAvroSerde
    }

    @Inject
    @Singleton
    @Named(TopicosConstants.PORTAL_CLIENTE)
    KStream<GenericRecord, GenericRecord> clientePortalStream(
            @Named("key-serde")  GenericAvroSerde genericKeyAvroDeserializer,
            @Named("value-serde") GenericAvroSerde genericValueAvroDeserializer,
            final ConfiguredStreamBuilder builder
    ) {
        return builder.stream(TopicosConstants.PORTAL_CLIENTE, Consumed.with(genericKeyAvroDeserializer, genericValueAvroDeserializer))
    }

    @Inject
    @Singleton
    @Named(TopicosConstants.PORTAL_GRUPO_CLIENTES)
    KStream<GenericRecord, GenericRecord> grupoClientesStream(
            @Named("key-serde")  GenericAvroSerde genericKeyAvroDeserializer,
            @Named("value-serde") GenericAvroSerde genericValueAvroDeserializer,
            final ConfiguredStreamBuilder builder
    ) {
        return builder.stream(TopicosConstants.PORTAL_GRUPO_CLIENTES, Consumed.with(genericKeyAvroDeserializer, genericValueAvroDeserializer))
    }

    @Inject
    @Singleton
    @Named(TopicosConstants.PORTAL_GRUPO_OPERADORAS)
    KStream<GenericRecord, GenericRecord> grupoOperadorasStream(
            @Named("key-serde")  GenericAvroSerde genericKeyAvroDeserializer,
            @Named("value-serde") GenericAvroSerde genericValueAvroDeserializer,
            final ConfiguredStreamBuilder builder
    ) {
        return builder.stream(TopicosConstants.PORTAL_GRUPO_OPERADORAS, Consumed.with(genericKeyAvroDeserializer, genericValueAvroDeserializer))
    }

    @Inject
    @Singleton
    @Named(TopicosConstants.PORTAL_OPERADORA_ANS)
    KStream<GenericRecord, GenericRecord> operadoraAnsStream(
            @Named("key-serde")  GenericAvroSerde genericKeyAvroDeserializer,
            @Named("value-serde") GenericAvroSerde genericValueAvroDeserializer,
            final ConfiguredStreamBuilder builder
    ) {
        return builder.stream(TopicosConstants.PORTAL_OPERADORA_ANS, Consumed.with(genericKeyAvroDeserializer, genericValueAvroDeserializer))
    }

    @Inject
    @Singleton
    @Named(TopicosConstants.PORTAL_OPERADORA_MULTITENANT)
    KStream<GenericRecord, GenericRecord> operadoraMultitenantStream(
            @Named("key-serde")  GenericAvroSerde genericKeyAvroDeserializer,
            @Named("value-serde") GenericAvroSerde genericValueAvroDeserializer,
            final ConfiguredStreamBuilder builder
    ) {
        return builder.stream(TopicosConstants.PORTAL_OPERADORA_MULTITENANT, Consumed.with(genericKeyAvroDeserializer, genericValueAvroDeserializer))
    }
}
