FROM gradle:jdk17-alpine
COPY . /build
USER root
RUN mkdir /kumaslash
RUN chown -R gradle /build
RUN chown -R gradle /kumaslash
RUN chmod +x /build/gradlew
USER gradle
WORKDIR /build
RUN ./gradlew jar
RUN mv ./build/libs/kumaslash-*.jar /kumaslash/application.jar
WORKDIR /kumaslash
USER root
RUN rm -rf /build
USER gradle
CMD java -jar application.jar $(echo $BOT_TOKEN)

