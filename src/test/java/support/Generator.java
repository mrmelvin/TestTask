package support;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static support.Constants.*;

public class Generator {
	public static String token() {
		return given().headers(HEADERS).contentType(JSON)
				.body(Utils.getConfigData())
				.when()
				.post(String.format("%s/api/tester/login", BASE_URL))
				.then().extract().response().jsonPath().get("accessToken");
	}

	public static Map headersWithToken() {
		return Map.of("accept", "application/json",
				"Content-Type", "application/json",
				"Authorization", String.format("Bearer %s", token()));
	}


}
