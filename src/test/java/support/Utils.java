package support;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;
import support.models.Player;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static support.Constants.*;

public class Utils {

	private static ObjectMapper mapper = new ObjectMapper();
	private static Path getConfigPath(String fileName) {
		return Paths.get("src", "test", "resources", fileName)
				.toAbsolutePath().normalize();
	}

	private static String readConfig(String fileName) throws Exception {
		Path filePath = getConfigPath(fileName);
		return Files.readString(filePath).trim();
	}

	public static Map getConfigData() {
		Map<String, String> configMap = new HashMap<>();
		try {
			configMap = mapper.readValue(readConfig(CONFIG_FILE), Map.class);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return configMap;
	}


	public static Map preparedCreatePlayer(Map initialData) {

		return given().headers(Generator.headersWithToken()).contentType(JSON)
				.body(initialData)
				.when()
				.post(String.format("%s/api/automationTask/create", BASE_URL))
				.then().extract().response().body().as(Map.class);
	}

	public static void deletePlayer(String id) {
		given().headers(Generator.headersWithToken()).contentType(JSON)
				.when()
				.delete(String.format("%s/api/automationTask/deleteOne/%s", BASE_URL, id))
				.then().extract().response();
	}

	public static void main(String[] args) {
		deletePlayer("64f6e3ee15ea3d2a62aab8a8");
	}
}
