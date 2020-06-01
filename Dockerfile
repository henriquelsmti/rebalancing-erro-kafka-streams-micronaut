FROM adoptopenjdk/openjdk11-openj9:jdk-11.0.6_10_openj9-0.18.1-debian
COPY build/libs/rebalancing-erro-kafka-streams-micronaut-*-all.jar rebalancing-erro-kafka-streams-micronaut.jar
EXPOSE 8071
CMD ["java", "-Dcom.sun.management.jmxremote", "-Xmx128m", "-XX:+IdleTuningGcOnIdle", "-Xtune:virtualized", "-jar", "rebalancing-erro-kafka-streams-micronaut.jar"]
