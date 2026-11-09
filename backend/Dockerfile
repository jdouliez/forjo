FROM maven:3.6.0-jdk-8-slim as maven-builder

COPY . .
RUN mvn package -DskipTests

FROM rtfpessoa/ubuntu-jdk8

RUN mkdir /var/www && chmod o+w /var/www && mkdir /var/www/uploads

COPY --from=maven-builder target/forjo-backend-latest.jar /var/www
COPY src/main/resources/uploads /var/www/uploads/.

COPY src/main/resources/jwt_tool/requirements.txt /tmp

USER www-data
WORKDIR /var/www

ENTRYPOINT java -jar forjo-backend-latest.jar --spring.profiles.active=prod