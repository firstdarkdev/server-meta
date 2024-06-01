FROM openjdk:17-alpine AS builder

# Copy Working FIles
WORKDIR /app
COPY . .

# Build it
RUN chmod +x gradlew
RUN ./gradlew clean build -PisDocker=true

FROM openjdk:17-alpine
WORKDIR /app

RUN apk update && apk add bash

# Copy the compiled jar
COPY --from=builder /app/build/libs/servermeta.jar /app/servermeta.jar
EXPOSE 2500

# Setup the entry point, with doppler
CMD ["java", "-jar", "servermeta.jar"]