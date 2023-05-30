FROM maven AS MAVEN_BUILD
WORKDIR /app

COPY . /app
COPY /lib ./lib/
#RUN --mount=type=cache,id=m2-cache,sharing=shared,target=/root/.m2
RUN mvn install:install-file -Dfile=./lib/common-4.jar -DgroupId=th.co.scb.autosale -DartifactId=common -Dversion=4 -Dpackaging=jar

#RUN echo \
#    "<settings xmlns='http://maven.apache.org/SETTINGS/1.0.0\' \
#    xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' \
#    xsi:schemaLocation='http://maven.apache.org/SETTINGS/1.0.0 https://maven.apache.org/xsd/settings-1.0.0.xsd'> \
#        <localRepository>/app/.m2/repository</localRepository> \
#        <interactiveMode>true</interactiveMode> \
#        <usePluginRegistry>false</usePluginRegistry> \
#        <offline>false</offline> \
#    </settings>" \
#    > /usr/share/maven/conf/settings.xml;

RUN mvn clean package -DskipTests


FROM openjdk:17-alpine
WORKDIR /app
COPY --from=MAVEN_BUILD /app/target/*.jar /app.jar
EXPOSE 8080

ENTRYPOINT ["sh" ,"-c"]
CMD ["exec java $JAVA_OPTS -jar /app.jar  $JAVA_CONFIGS"]