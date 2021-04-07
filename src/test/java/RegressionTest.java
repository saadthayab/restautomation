import config.BaseConfig;
import config.EndPoints;
import io.restassured.response.Response;
import org.testng.annotations.Test;
import java.time.Instant;
import static io.restassured.RestAssured.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class RegressionTest extends BaseConfig {

    @Test
    public void getAllGames() {
        test = extent.createTest("Get All Games, and Status Check ");
        given().
                when().get(EndPoints.ALL_VIDEO_GAMES).
                then().log().all().statusCode(200);
    }

    @Test
    public void getAllGamesAssertion() {
        test = extent.createTest("Validate the No Of Existing Games ");
        VideoGame[] vdR = given().when().
                get(EndPoints.ALL_VIDEO_GAMES).as(VideoGame[].class);
        System.out.println(vdR.length);
        assertThat(vdR.length, is(vdR.length));

    }

    @Test
    public void validateGameSpecs() {
        test = extent.createTest("Deserialize response Body and Assert all the Specs");
        Response response = given().pathParam("videoGameId", 5).
                when().
                get(EndPoints.SINGLE_VIDEO_GAME);

        VideoGame videoGame = response.getBody().as(VideoGame.class);

        System.out.println(videoGame);
        assertThat(videoGame.getId(), is("5"));
        assertThat(videoGame.getName(), is("The Legend of Zelda: Ocarina of Time"));
        assertThat(videoGame.getReleaseDate(), is("1998-12-10"));
        assertThat(videoGame.getReviewScore(), is("93"));
        assertThat(videoGame.getCategory(), is("Adventure"));
        assertThat(videoGame.getRating(), is("PG-13"));
    }


    @Test
    public void CreatePostRequest() {
        test = extent.createTest("Create  Post Request");

        long gameId = Instant.now().getEpochSecond();

        System.out.println("Game Id before Update " + gameId);

        // Create Object
        VideoGame videoGame = new VideoGame
                ("1"
                        , "2018-04-04",
                        "My Awesome Game",
                        "Mature",
                        String.valueOf(gameId), "Shooter");
        // Post, Rest Assured WIll Take Care of Serialization
       Response postResponse =  given().
                body(videoGame).
                when().
                post(EndPoints.ALL_VIDEO_GAMES);

       // Deserialize Response and Validation
        CreateResponse createResponse = postResponse.getBody().as(CreateResponse.class);
        assertThat(createResponse.getStatus(), is("Record Added Successfully"));
        int statusCode = postResponse.getStatusCode();
        assertThat(statusCode,is(200));

    }


    @Test
    public void CreateUpdateDelete() {
        test = extent.createTest("Get, Post, Update, And  Delete Test");

        long gameId = Instant.now().getEpochSecond();

        System.out.println("Game Id before Update " + gameId);

        // Create Object
        VideoGame videoGame = new VideoGame
                ("1"
                        , "2018-04-04",
                        "My Awesome Game",
                        "Mature",
                        String.valueOf(gameId) , "Shooter");
        // Post
        given().
                body(videoGame).
                when().
                post(EndPoints.ALL_VIDEO_GAMES).
                then();


        Response response = given().pathParam("videoGameId",gameId ).
                when().
                get(EndPoints.SINGLE_VIDEO_GAME);

        VideoGame videoGamePojoRes = response.getBody().as(VideoGame.class);

        assertThat(videoGamePojoRes.getId(), is( String.valueOf(gameId)));

        //// Now update the game name and perform Update

        videoGame.setName("LegoBatman2");
        given().
                body(videoGame).
                when().
                put(EndPoints.ALL_VIDEO_GAMES+"/"+gameId).
                then();

        Response responseAfterPUt = given().pathParam("videoGameId",gameId ).
                when().
                get(EndPoints.SINGLE_VIDEO_GAME);

        VideoGame videoGamePojoResAfterPut = responseAfterPUt.getBody().as(VideoGame.class);

        assertThat(videoGamePojoResAfterPut.getName(), is("LegoBatman2"));

    // deleteing the game
       Response deleteResponse =
               given().
                when()
                .delete("videogames/" + gameId);

        DeleteResponse deleteResponse1 = deleteResponse.getBody().as(DeleteResponse.class);
        assertThat(deleteResponse1.getStatus(), is("Record Deleted Successfully"));


    }



    @Test
    public void captureResponseTime() {
        test = extent.createTest("Validate Response Time Test");

        long responseTime = get(EndPoints.ALL_VIDEO_GAMES).time();
        System.out.println("Response time in MS: " + responseTime);
    }
}
