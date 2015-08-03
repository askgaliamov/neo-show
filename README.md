# neo-show
Unmanaged extension for [Neo4j](http://neo4j.com/).

## Build

```
mvn clean compile
```

## Test
For integration tests use embedded Neo4j. By default it use 7474 port, but this and other configuration properties can be changed in the file:

```
src/test/groovy/com/galiamov/neoshow/NeoSpecification.groovy
``` 

Run tests:
```
mvn clean compile
```

For the manual testing `import_db.sh` provided. It's inport Cypher statements from `data.json` and `index.json`.
**IMPORTANT! It'll delete all data form DB before importing after run.**


## Provided API
POST /users

GET /users

GET /{email}/likes

POST /tv_shows

GET /tv_shows

POST /tv_shows/like

GET /tv_shows/aired_on/{date}

GET /tv_shows/recommendation/{email}

GET /tv_shows/top
