# Build a normal image

FROM bellsoft/liberica-runtime-container:jdk-23-musl AS builder
WORKDIR /app
ADD ./ /app/
RUN chmod +x ./mvnw && ./mvnw clean package -P prod

FROM bellsoft/liberica-runtime-container:jre-23-slim-musl
WORKDIR /app
EXPOSE 8080
CMD ["java", "-jar", "/app/wildguide-app.jar"]
COPY --from=builder /app/target/wildguide-app.jar /app/wildguide-app.jar

# ------------------------------------------------------------------
# Build the image with CDS startup optimizations

# FROM bellsoft/liberica-runtime-container:jdk-23-crac-cds-musl AS builder
# WORKDIR /home/app
# ADD ./ /home/app/
# RUN ./mvnw clean package

# FROM bellsoft/liberica-runtime-container:jdk-23-cds-slim-musl AS optimizer
# WORKDIR /app
# COPY --from=builder /home/app/target/wildguide-app.jar wildguide-app.jar
# RUN java -Djarmode=tools -jar wildguide-app.jar extract --layers --launcher

# FROM bellsoft/liberica-runtime-container:jdk-23-cds-slim-musl
# ENTRYPOINT ["java", "-Dspring.aot.enabled=true", "-XX:SharedArchiveFile=application.jsa", "org.springframework.boot.loader.launch.JarLauncher"]
# COPY --from=optimizer /app/wildguide-app/dependencies/ ./
# COPY --from=optimizer /app/wildguide-app/spring-boot-loader/ ./
# COPY --from=optimizer /app/wildguide-app/snapshot-dependencies/ ./
# COPY --from=optimizer /app/wildguide-app/application/ ./
# RUN java -Dspring.aot.enabled=true -XX:ArchiveClassesAtExit=./application.jsa -Dspring.context.exit-on-refresh=true org.springframework.boot.loader.launch.JarLauncher
