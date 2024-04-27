import dev.skuggi.core.RestAssuredLite;
import io.restassured.http.ContentType;

public class clientTest {


    public void test1() {

        RestAssuredLite lite = RestAssuredLite.getInstance(true);

        lite
                .setContentType(ContentType.JSON)
                .setBasicAuth("user", "pass")
                .setBaseUri("adsfasdf")
                .appendToBaseUri("sdafadf")
                .setBasePath("sdfsadf")
                .appendToBasePath("adsfadsf")
                .setBody("sdf")
                .buildRequest();


    }
}
