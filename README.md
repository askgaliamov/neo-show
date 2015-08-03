# neo-show
Unmanaged extension for [Neo4j](http://neo4j.com/).

### Build

```
mvn clean compile
```

### Test
For integration testing embedded Neo4j used. By default it use 7474 port, but this and other configuration properties can be changed in here:

```
src/test/groovy/com/galiamov/neoshow/NeoSpecification.groovy
``` 

Run tests:
```
mvn test
```

For the manual testing `import_db.sh` also provided. It'll import Cypher statements from `data.json` and `index.json`.
**IMPORTANT! It'll delete all data form DB before importing after run.**


### Provided API
POST /users

GET /users

GET /{email}/likes

POST /tv_shows

GET /tv_shows

POST /tv_shows/like

GET /tv_shows/aired_on/{date}

GET /tv_shows/recommendation/{email}

GET /tv_shows/top
