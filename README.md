# revolut test exercise

Project use:
1. Jetty as embedded web server / servlet container
2. Jersey as REST framework
3. EclipseLink as JPA implementation
4. H2 as test in-memory database

Build with Maven. Create all-in-one jar with:
> mvn package

Standalone application on 8081 port:
> java -jar target/revolut-test-1.0-SNAPSHOT.jar
