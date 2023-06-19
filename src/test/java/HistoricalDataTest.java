import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pojo.CoordinatesByLocalName;
import pojo.WeatherConditions;
import static io.restassured.RestAssured.given;


public class HistoricalDataTest {

    String appID = "Insert Here the API Key";

    private RequestSpecification jsonRequest = new RequestSpecBuilder()
            .setBaseUri("https://api.openweathermap.org")
            .setContentType(ContentType.JSON)
            .build();

    @DataProvider(name ="WeatherDataProvider")
    public Object[][] dataProviderfunc(){
        return new Object[][]{
                {"Bucharest"},
                {"Palma"}
        };
    }

    @Test (dataProvider = "WeatherDataProvider")
    public void WeatherCity10YearsInRow(String city) {

        CoordinatesByLocalName[] coordinatesByLocalName = given().log().all().spec(jsonRequest)
            .param("q", city)
            .param("limit", 1)
            .param("appid",appID)
            .get("/geo/1.0/direct")
            .then()
            .log().all()
            .statusCode(200)
            .extract().response().as(CoordinatesByLocalName[].class);

        double lat = coordinatesByLocalName[0].getLat();
        double lon = coordinatesByLocalName[0].getLon();

        for (int year = 2012; year < 2023; year++) {

            String date = year + "-06-13";

            WeatherConditions weatherConditions = given().log().all().spec(jsonRequest)
                    .param("lat", lat)
                    .param("lon", lon)
                    .param("date", date)
                    .param("appid", appID)
                    .param("units", "metric" )
                    .get("data/3.0/onecall/day_summary")
                    .then()
                    .log().all()
                    .statusCode(200)
                    .extract().response().as(WeatherConditions.class);

            double minTemperature = weatherConditions.getTemperature().getMin();
            double maxTemperature = weatherConditions.getTemperature().getMax();
            double avgTemp = minTemperature + maxTemperature / 2;
            System.out.println(maxTemperature);
            System.out.println(minTemperature);
            System.out.println(avgTemp);
        }
    }
}
