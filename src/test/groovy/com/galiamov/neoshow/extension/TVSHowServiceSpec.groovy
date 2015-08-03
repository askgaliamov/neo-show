package com.galiamov.neoshow.extension

import com.galiamov.neoshow.NeoSpecification
import groovy.json.JsonSlurper
import org.neo4j.test.server.HTTP

import java.text.SimpleDateFormat

import static com.galiamov.neoshow.extension.TVShowHelper.randomTVShow
import static com.galiamov.neoshow.extension.UserHelper.getRandomUser
import static java.lang.System.currentTimeMillis

class TVSHowServiceSpec extends NeoSpecification {

    def tvShowsURI = server.httpURI().resolve("/unmanaged/tv_shows").toString()
    def tvShowsURIPageable = server.httpURI().resolve("/unmanaged/tv_shows?skip=2&limit=3").toString()
    def likeTvShowsURI = server.httpURI().resolve("/unmanaged/tv_shows/like").toString()

    def dateFormat = new SimpleDateFormat("yyyy-MM-dd")

    private static final jsonSlurper = new JsonSlurper()

    def setup() {
        query("MATCH (n) OPTIONAL MATCH (n)-[r]-() DELETE n,r")
    }

    def "create TVShow"() {
        given:
        def tvShow = getRandomTVShow()

        when:
        def response = HTTP.POST(tvShowsURI, tvShow)

        then:
        response.result == success
        tvShow == query("MATCH (n:TVShow {title:'" + tvShow.title + "'}) RETURN n").results[0].data[0].row[0]
    }

    def "invalid create tvShow request"() {
        given:
        def tvShow = [title: title, releaseDate: releaseDate, endDate: endDate]

        expect:
        HTTP.POST(tvShowsURI, tvShow).reason.getTextValue() == result

        where:
        title          | releaseDate         | endDate             || result
        null           | currentTimeMillis() | currentTimeMillis() || "invalid_title"
        ""             | currentTimeMillis() | currentTimeMillis() || "invalid_title"
        "Breaking Bad" | null                | currentTimeMillis() || "invalid_release_date"
        "Breaking Bad" | "2015-01"           | currentTimeMillis() || "invalid_release_date"
        "Breaking Bad" | "2015-01-01"        | "2015-01-"          || "invalid_release_date"
    }

    def "do not create user with existing email, just skip"() {
        given:
        def tvShow = getRandomTVShow()

        when:
        3.times { HTTP.POST(tvShowsURI, tvShow) }

        then:
        1 == query("MATCH (n:TVShow) RETURN n").results[0].data.size()
    }

    def "get tvShows"() {
        given:
        def tvShows = []
        3.times { tvShows << insertTVShow() }

        when:
        def response = jsonSlurper.parse(HTTP.GET(tvShowsURI).entity.bytes) as List

        then:
        response == tvShows
    }

    def "get tvShows with pagination"() {
        given:
        def tvShows = []
        5.times { tvShows << insertTVShow() }

        expect:
        jsonSlurper.parse(HTTP.GET(tvShowsURIPageable).entity.bytes) as List == tvShows.takeRight(3)
    }

    def "like TVShow"() {
        given:
        def tvShow = insertTVShow()
        def user = insertUser()

        and:
        def like = [user_email: user.email, tvshow_title: tvShow.title]

        when:
        HTTP.POST(likeTvShowsURI, like)

        then:
        query("match (u:User {email:'${user.email}'})-[:LIKE]->(s:TVShow {title:'${tvShow.title}'}) " +
                "return count(*) as c").results[0].data.row[0] as List == [1]
    }

    def "return user_not_found if liker doesn't exist while like TVShow"() {
        given:
        def tvShow = insertTVShow()

        and:
        def like = [user_email: 'wrong@mail.com', tvshow_title: tvShow.title]

        expect:
        HTTP.POST(likeTvShowsURI, like).reason.getTextValue() == "user_not_found"
    }

    def "return tvshow_not_found if liker doesn't exist while like TVShow"() {
        given:
        def user = insertUser()

        and:
        def like = [user_email: user.email, tvshow_title: 'fake show']

        expect:
        HTTP.POST(likeTvShowsURI, like).reason.getTextValue() == "tvshow_not_found"
    }

    def "return airedOn TVShows"() {
        given:
        def tvShows = getTVShowForAiredOn()

        def airedOnTvShowsURI = server.httpURI().resolve("/unmanaged/tv_shows/aired_on/2015-07-10").toString()

        when:
        def response = jsonSlurper.parse(HTTP.GET(airedOnTvShowsURI).entity.bytes) as List

        then:
        response == tvShows.takeRight(4)
    }

    def "return airedOn TVShows with pagination"() {
        given:
        def tvShows = getTVShowForAiredOn()

        def airedOnTvShowsURI = server.httpURI().resolve("/unmanaged/tv_shows/aired_on/2015-07-10?skip=1&limit=2").toString()

        when:
        def response = jsonSlurper.parse(HTTP.GET(airedOnTvShowsURI).entity.bytes) as List

        then:
        response == tvShows.takeRight(3).take(2)
    }

    private getTVShowForAiredOn() {
        def tvShows = []
        def releaseDate = toTime("2015-06-01")
        def endDate = toTime("2015-08-01")
        tvShows << insertTVShow([title: 'show0', release_date: toTime("2015-08-01"), end_date: toTime("2015-09-01")])
        tvShows << insertTVShow([title: 'show1', release_date: releaseDate, end_date: toTime("2015-07-01")])
        tvShows << insertTVShow([title: 'show2', release_date: releaseDate])
        tvShows << insertTVShow([title: 'show3', release_date: releaseDate, end_date: endDate])
        tvShows << insertTVShow([title: 'show4', release_date: releaseDate, end_date: endDate])
        tvShows << insertTVShow([title: 'show5', release_date: releaseDate, end_date: endDate])
        tvShows
    }

    def "return co-liked recommendations"() {
        given:
        def users = []
        4.times { users << insertUser() }

        def tvShows = []
        4.times { tvShows << insertTVShow() }

        createRelationshipForRecommendations(users, tvShows)

        def airedOnTvShowsURI = server.httpURI().resolve("/unmanaged/tv_shows/recommendation/${users[0].email}").toString()

        when:
        def response = jsonSlurper.parse(HTTP.GET(airedOnTvShowsURI).entity.bytes) as List

        then:
        response == tvShows.takeRight(2).reverse()
    }

    private void createRelationshipForRecommendations(ArrayList users, ArrayList tvShows) {
        like(users[0], tvShows[0])
        like(users[0], tvShows[1])
        like(users[1], tvShows[1])
        like(users[1], tvShows[2])
        like(users[2], tvShows[1])
        like(users[2], tvShows[3])
        like(users[3], tvShows[3])
    }

    def "return co-liked recommendations with pagination"() {
        given:
        def users = []
        7.times { users << insertUser() }

        def tvShows = []
        6.times { tvShows << insertTVShow() }

        createRelationshipForRecommendations(users, tvShows)

        createRelationshipForRecommendationsWithPagination(users, tvShows)

        def airedOnTvShowsURI = server.httpURI().resolve("/unmanaged/tv_shows/recommendation/${users[0].email}?skip=1&limit=1").toString()

        when:
        def response = jsonSlurper.parse(HTTP.GET(airedOnTvShowsURI).entity.bytes) as List

        then:
        response == [tvShows[3]]
    }

    private void createRelationshipForRecommendationsWithPagination(ArrayList users, ArrayList tvShows) {
        like(users[4], tvShows[1])
        like(users[5], tvShows[1])
        like(users[6], tvShows[1])
        like(users[4], tvShows[4])
        like(users[5], tvShows[4])
        like(users[6], tvShows[4])
    }

    def "return top TVShow by likes with pagination"() {
        given:
        def users = []
        3.times { users << insertUser() }

        def tvShows = []
        4.times { tvShows << insertTVShow() }

        createRelationshipForTopTVShow(users, tvShows)

        def airedOnTvShowsURI = server.httpURI().resolve("/unmanaged/tv_shows/top?skip=1&limit=2").toString()

        when:
        def response = jsonSlurper.parse(HTTP.GET(airedOnTvShowsURI).entity.bytes) as List

        then:
        response == tvShows.take(3).takeRight(2)
    }

    private void createRelationshipForTopTVShow(ArrayList users, ArrayList tvShows) {
        like(users[0], tvShows[0])
        like(users[1], tvShows[0])
        like(users[2], tvShows[0])
        like(users[0], tvShows[1])
        like(users[1], tvShows[1])
        like(users[0], tvShows[2])
    }


    def "return top TVShow by likes"() {
        given:
        def users = []
        3.times { users << insertUser() }

        def tvShows = []
        4.times { tvShows << insertTVShow() }

        createRelationshipForTopTVShow(users, tvShows)

        def airedOnTvShowsURI = server.httpURI().resolve("/unmanaged/tv_shows/top").toString()

        when:
        def response = jsonSlurper.parse(HTTP.GET(airedOnTvShowsURI).entity.bytes) as List

        then:
        response == tvShows.take(3)
    }

    def createUsers(n) {
        def users = []
        n.times { users << insertUser() }
        users
    }

    private long toTime(String date) {
        dateFormat.parse(date).getTime()
    }

    def insertTVShow() {
        def tvShow = getRandomTVShow()
        insertTVShow(tvShow)
        tvShow
    }

    def insertTVShow(tvShow) {
        query("CREATE (:TVShow {title:'${tvShow.title}'," +
                " release_date:${tvShow.release_date}" +
                (tvShow.end_date != null ? ", end_date:${tvShow.end_date}" : "") +
                "})")
        tvShow
    }

    def insertUser() {
        def user = getRandomUser()
        query("CREATE (:User {email:'${user.email}', age:toInt(${user.age}), gender:toInt(${user.gender})})")
        user
    }

    def like(user, tvShow) {
        query("MATCH (u:User {email:'${user.email}'}),(s:TVShow {title:'${tvShow.title}'}) MERGE (u)-[r:LIKE]->(s)")
    }

}
