FROM sdorra/oracle-java-8:latest
VOLUME /tmp
ADD ${appName}.jar ${appName}.jar
RUN bash -c 'touch /${appName}.jar'
ENTRYPOINT ["java","-jar","/${appName}.jar"]