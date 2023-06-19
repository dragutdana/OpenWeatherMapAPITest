import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pojo.CoordinatesByLocalName;
import pojo.WeatherConditions;
import static io.restassured.RestAssured.given;

public class WeatherTests {
    String appID = "Insert API Key Here ";
    private RequestSpecification jsonRequest = new RequestSpecBuilder()
                                                .setBaseUri("https://api.openweathermap.org")
                                                .setContentType(ContentType.JSON)
                                                .build();

    @DataProvider(name ="WeatherDataProvider")
    public Object[][] dataProviderfunc(){
        return new Object[][]{
                {"Bucharest", "2013-07-04"},
                {"Palma", "2013-07-04"}
        };
    }

    @Test(dataProvider = "WeatherDataProvider")
    public void weatherPalmaDeMallorca(String city, String date) {

        CoordinatesByLocalName[] coordinatesByLocalName = given().log().all().spec(jsonRequest)
                .param("q", city)
                .param("limit", 1)
                .param("appid", appID)
                .get("/geo/1.0/direct")
                .then()
                .log().all()
                .statusCode(200)
                .extract().response().as(CoordinatesByLocalName[].class);

        double lat = coordinatesByLocalName[0].getLat();
        double lon = coordinatesByLocalName[0].getLon();

        WeatherConditions weatherConditions = given().log().all().spec(jsonRequest)
                .param("lat", lat)
                .param("lon", lon)
                .param("date", date)
                .param("appid", appID)
                .param("units", "metric")
                .get("data/3.0/onecall/day_summary")
                .then()
                .log().all()
                .statusCode(200)
                .extract().response().as(WeatherConditions.class);

        double minTemperature = weatherConditions.getTemperature().getMin();
        double maxTemperature = weatherConditions.getTemperature().getMax();
        System.out.println(minTemperature);
        System.out.println(maxTemperature);
    }
}



