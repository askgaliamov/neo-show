package com.galiamov.neoshow.repository;

import com.galiamov.neoshow.NeoShowException;
import com.galiamov.neoshow.model.Like;
import com.galiamov.neoshow.model.Pageable;
import com.galiamov.neoshow.model.TVShow;
import com.galiamov.neoshow.model.User;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static com.galiamov.neoshow.NeoShowException.TVSHOW_NOT_FOUND;
import static com.galiamov.neoshow.NeoShowException.USER_NOT_FOUND;
import static com.galiamov.neoshow.repository.Properties.TVShow.END_DATE;
import static com.galiamov.neoshow.repository.Properties.TVShow.RELEASE_DATE;
import static com.galiamov.neoshow.repository.Properties.TVShow.TITLE;
import static com.galiamov.neoshow.repository.Properties.User.EMAIL;
import static com.galiamov.neoshow.repository.Relationships.LIKE;

public class Neo4JRepository {

    private static Neo4JRepository instance;
    private final GraphDatabaseService database;

    private Neo4JRepository(GraphDatabaseService database) {
        this.database = database;
    }

    public static Neo4JRepository getInstance(GraphDatabaseService database) {
        if (instance == null) {
            synchronized (Neo4JRepository.class) {
                if (instance == null) {
                    instance = new Neo4JRepository(database);
                }
            }

        }
        return instance;
    }

    private static final String createUserQuery =
            "merge (n:User {email: {email}, age:{age}, gender:{gender}})";

    public void createUser(User user) {
        try (Transaction tx = database.beginTx()) {
            database.execute(createUserQuery, user.asMap());
            tx.success();
        }
    }

    private static final String getUsersQuery =
            "match (u:User) return u skip {skip} limit {limit}";

    public Collection<User> getUsers(Pageable pageable) {
        List<User> result = new ArrayList<>(pageable.getLimit());
        try (Transaction tx = database.beginTx()) {
            Result executeResult = database.execute(getUsersQuery, pageable.asMap());
            ResourceIterator<Node> nodes = executeResult.columnAs("u");
            nodes.forEachRemaining(node -> {
                User user = new User(
                        (String) node.getProperty(EMAIL),
                        getInt(node.getProperty(Properties.User.AGE)),
                        getInt(node.getProperty(Properties.User.GENDER))
                );
                result.add(user);
            });
            tx.success();
        }
        return result;
    }

    private String findUserLikesQuery(String email, Pageable pageable) {
        return String.format("match (:User{email:\"%s\"}-[:LIKE]-(show:TVShow)) return show skip {%d} limit {%d}",
                email, pageable.getSkip(), pageable.getLimit());
    }

    public Collection<TVShow> findUserLikes(String email, Pageable pageable) {
        List<TVShow> result = new ArrayList<>(pageable.getLimit());
        try (Transaction tx = database.beginTx()) {
            Result executeResult = database.execute(findUserLikesQuery(email, pageable));
            ResourceIterator<Node> tvSHows = executeResult.columnAs("show");
            tvSHows.forEachRemaining(tvSHow -> {
                TVShow tvShow = createTVSHowFrom(tvSHow);
                result.add(tvShow);
            });
            tx.success();
        }
        return result;
    }

    public void likeTVShow(Like like) {
        try (Transaction tx = database.beginTx()) {
            ResourceIterator<Node> users = database.findNodes(Labels.User, EMAIL, like.getUserEmail());
            if (!users.hasNext()) {
                throw new NeoShowException(USER_NOT_FOUND);
            }
            ResourceIterator<Node> tvShows = database.findNodes(Labels.TVShow, TITLE, like.getTvShowTitle());
            if (!tvShows.hasNext()) {
                throw new NeoShowException(TVSHOW_NOT_FOUND);
            }
            while (users.hasNext()) {
                Node user = users.next();
                while (tvShows.hasNext()) {
                    Node node = tvShows.next();
                    user.createRelationshipTo(node, LIKE);
                }
            }
            tx.success();
        }
    }

    private static final String createTVShowQuery = "merge (n:TVShow {title: {title}, release_date:{release_date}})";
    private static final String createTVShowQueryFull = "merge (n:TVShow {title: {title}, " +
            "release_date:{release_date}, " +
            "end_date:{end_date}})";

    public void createTVShow(TVShow tvShow) {
        try (Transaction tx = database.beginTx()) {
            database.execute(tvShow.isEndDatePresent() ? createTVShowQueryFull : createTVShowQuery, tvShow.asMap());
            tx.success();
        }
    }

    private static final String getTVShowsQuery = "match (show:TVShow) return show skip {skip} limit {limit}";

    public List<TVShow> getAllTVShows(Pageable pageable) {
        return executeTVShowQuery(pageable, getTVShowsQuery);
    }

    private static String getAiredOnQuery(long time, Pageable pageable) {
        return String.format("match (show:TVShow) where" +
                        " show.release_date <= %d and (show.end_date is null or show.end_date >= %d)" +
                        " return show order by show.release_date skip %d limit %d",
                time, time, pageable.getSkip(), pageable.getLimit());
    }

    public List<TVShow> getAiredOn(Date airedOn, Pageable pageable) {
        String query = getAiredOnQuery(airedOn.getTime(), pageable);
        return executeTVShowQuery(pageable, query);
    }

    static String getCoLikeRecommendationQuery(String email, Pageable pageable) {
        return String.format(
                "match (me:User{email:\"%s\"})-[:LIKE]->(:TVShow)<-[:LIKE]-" +
                        "(:User)-[:LIKE]->(show:TVShow)<-[:LIKE*0..1]-()" +
                        " return show, count(*) as c order by c desc skip %d limit %d",
                email, pageable.getSkip(), pageable.getLimit());
    }

    public List<TVShow> getCoLikeRecommendation(String email, Pageable pageable) {
        String query = getCoLikeRecommendationQuery(email, pageable);
        return executeTVShowQuery(pageable, query);
    }

    static String getMostLikedShowQuery(Pageable pageable) {
        return String.format("match ()-[:LIKE]->(show:TVShow)" +
                        " return count(show) as c, show order by c desc skip %d limit %d",
                pageable.getSkip(), pageable.getLimit());
    }

    public List<TVShow> getTopLikedShow(Pageable pageable) {
        String query = getMostLikedShowQuery(pageable);
        return executeTVShowQuery(pageable, query);
    }

    private List<TVShow> executeTVShowQuery(Pageable pageable, String getTVShowsQuery) {
        List<TVShow> result = new ArrayList<>(pageable.getLimit());
        try (Transaction tx = database.beginTx()) {
            Result executeResult = database.execute(getTVShowsQuery, pageable.asMap());
            ResourceIterator<Node> nodes = executeResult.columnAs("show");
            while (nodes.hasNext()) {
                Node node = nodes.next();
                TVShow userRequest = createTVSHowFrom(node);
                result.add(userRequest);
            }
            tx.success();
        }
        return result;
    }

    private TVShow createTVSHowFrom(Node node) {
        TVShow tvSHow = TVShow.build(node.getProperty(TITLE), node.getProperty(RELEASE_DATE));
        if (node.hasProperty(END_DATE)) {
            tvSHow.setEndDate(Long.valueOf(node.getProperty(END_DATE).toString()));
        }
        return tvSHow;
    }

    private int getInt(Object value) {
        return Long.valueOf(value.toString()).intValue();
    }

}
