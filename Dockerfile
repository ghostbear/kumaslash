FROM gradle:jdk17-alpine
COPY . /build
USER root
RUN mkdir /kumaslash
RUN chown -R gradle /build
RUN chown -R gradle /kumaslash
RUN chmod +x /build/gradlew
USER gradle
WORKDIR /build
RUN ./gradlew installDist
RUN cp -R ./app/build/install/app/. /kumaslash/
WORKDIR /kumaslash
USER root
RUN rm -rf /build
RUN chmod +x /kumaslash/bin/app
USER gradle
CMD ./bin/app $(echo $BOT_TOKEN) $(echo $IGNORE_ROLES)

