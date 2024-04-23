FROM amazoncorretto:21
COPY build/libs/hansecom-codechallenge-*.war /hansecom-codechallenge.war
EXPOSE 8080
CMD java -jar /hansecom-codechallenge.war 2>&1
