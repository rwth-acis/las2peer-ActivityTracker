FROM gradle:6.7-jdk14 as build

COPY . /home/gradle/src
WORKDIR /home/gradle/src

RUN gradle build -x test

FROM openjdk:14-jdk-alpine

ENV HTTP_PORT=8080
ENV HTTPS_PORT=8443
ENV LAS2PEER_PORT=9011

RUN addgroup -g 1000 -S las2peer && \
    adduser -u 1000 -S las2peer -G las2peer

WORKDIR /src
COPY --chown=las2peer:las2peer --from=build /home/gradle/src/service .
COPY --chown=las2peer:las2peer --from=build /home/gradle/src/lib .
COPY --chown=las2peer:las2peer --from=build /home/gradle/src/etc .
COPY --chown=las2peer:las2peer docker-entrypoint.sh /src/docker-entrypoint.sh

# run the rest as unprivileged user
USER las2peer

EXPOSE $HTTP_PORT
EXPOSE $HTTPS_PORT
EXPOSE $LAS2PEER_PORT
ENTRYPOINT ["/src/docker-entrypoint.sh"]
