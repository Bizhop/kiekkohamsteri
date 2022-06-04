FROM openjdk:11

COPY "target/kiekkohamsteri-backend-0.1.0.jar" "/app.jar"

EXPOSE 8080
CMD [ "-jar", "/app.jar" ]
ENTRYPOINT [ "java" ]