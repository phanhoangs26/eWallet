FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/e-Wallet-0.0.1-SNAPSHOT.jar ewallet-v1.0.jar
EXPOSE 9090
ENTRYPOINT ["java", "-jar", "ewallet-v1.0.jar"]