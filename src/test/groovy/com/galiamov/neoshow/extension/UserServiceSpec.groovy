package com.galiamov.neoshow.extension

import com.galiamov.neoshow.NeoSpecification
import groovy.json.JsonSlurper
import org.neo4j.test.server.HTTP

import static com.galiamov.neoshow.extension.UserHelper.randomUser

class UserServiceSpec extends NeoSpecification {

    def usersURI = server.httpURI().resolve("/unmanaged/users").toString()

    private static final jsonSlurper = new JsonSlurper()

    def setup() {
        query("MATCH (n) OPTIONAL MATCH (n)-[r]-() DELETE n,r")
    }

    def "create user"() {
        given:
        def user = getRandomUser()

        when:
        def response = HTTP.POST(usersURI, user)

        then:
        response.result == success
        user == query("MATCH (n:User {email:'" + user.email + "'}) RETURN n").results[0].data[0].row[0]
    }

    def "invalid create user request"() {
        given:
        def user = [email: email, age: age, gender: gender]

        expect:
        HTTP.POST(usersURI, user).reason.getTextValue() == result

        where:
        email      | age  | gender || result
        null       | 18   | 1      || "invalid_email"
        ""         | 18   | 2      || "invalid_email"
        "bedEmail" | 18   | 1      || "invalid_email"
        "a@b.com"  | 0    | 2      || "invalid_age"
        "a@b.com"  | null | 1      || "invalid_age"
        "a@b.com"  | 18   | 0      || "invalid_gender"
        "a@b.com"  | 18   | 3      || "invalid_gender"
    }

    def "do not create user with existing email, just skip"() {
        given:
        def user = getRandomUser()

        when:
        3.times { HTTP.POST(usersURI, user) }

        then:
        1 == query("MATCH (n:User {email:'" + user.email + "'}) RETURN n").results[0].data.size()
    }

    def "get users"() {
        given:
        def users = []
        3.times { users << insertUser() }

        when:
        def response = jsonSlurper.parse(HTTP.GET(usersURI).entity.bytes) as List

        then:
        response == users
    }

    def "get users with pagination"() {
        given:
        def usersURIPageable = server.httpURI().resolve("/unmanaged/users?skip=2&limit=3").toString()
        def users = []
        5.times { users << insertUser() }

        expect:
        jsonSlurper.parse(HTTP.GET(usersURIPageable).entity.bytes) as List == users.takeRight(3)
    }

    def insertUser() {
        def user = getRandomUser()
        query("CREATE (:User {email:'${user.email}', age:toInt(${user.age}), gender:toInt(${user.gender})})")
        user
    }

}
