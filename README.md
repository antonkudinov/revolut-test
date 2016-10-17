# revolut test exercise

Project use:
- Jetty as embedded web server / servlet container
- Jersey as REST framework
- EclipseLink as JPA implementation
- H2 as test in-memory database

Build with Maven. Create all-in-one jar with:
> mvn package

Standalone application on 8081 port:
> java -jar target/revolut-test-1.0-SNAPSHOT.jar
