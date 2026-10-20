# Ghost Net Fishing

Prototyp zum Melden und Bergen von Geisternetzen. Fallstudie IPWA02-01 an der IU.

Technik: Java 21, Spring Boot, Thymeleaf, Spring Data JPA, MySQL

## Starten

In MySQL eine Datenbank `ghostnet` mit einem Benutzer `ghostnet` anlegen. Dann im Projektordner eine Datei `db-local.properties` anlegen (die ist in der .gitignore):

```
spring.datasource.username=ghostnet
spring.datasource.password=...
```

Danach `./mvnw spring-boot:run` und im Browser http://localhost:8080 öffnen. Die Tabellen werden beim ersten Start automatisch angelegt.
