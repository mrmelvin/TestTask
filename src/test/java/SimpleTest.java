import io.restassured.RestAssured;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.equalTo;
import static support.Constants.*;

import support.Generator;
import support.models.Player;
import support.Utils;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

public class SimpleTest {

	static RequestSpecification httpRequest;

	@BeforeAll
	public static void customHttpClient() {
		RestAssured.baseURI = BASE_URL;
		httpRequest = RestAssured.given().baseUri(BASE_URL).basePath("/api");
		httpRequest.headers(Generator.headersWithToken());
		httpRequest.contentType(JSON);
	}


	@Test
	void isTokenValid() {
		Response response = httpRequest.headers(HEADERS).body(CREDENTIALS)
				.when()
				.post("/tester/login")
				.then()
				.statusCode(201)
				.extract().response();
		String token = response.jsonPath().get("accessToken").toString();
		Assertions.assertTrue(token.length() > 0);
	}

	@Test
	void createPlayer() {
		InputStream createBookingJsonSchema = getClass().getClassLoader()
				.getResourceAsStream ("validation/createPlayer.json");
		Player player = new Player();
		Response response = httpRequest.body(player.toMap()).when()
				.post("/automationTask/create")
				.then()
				.statusCode(201)
				.and()
				.assertThat().body(JsonSchemaValidator.matchesJsonSchema(createBookingJsonSchema)).extract().response();
		Assertions.assertEquals(player.getCurrencyCode(), response.jsonPath().get("currency_code"));
		Assertions.assertEquals(player.getEmail(), response.jsonPath().get("email"));
	}

	@Nested
	class testMethodGetOne {

		private static Map responseBody = new HashMap<>();
		InputStream getOneJsonSchema = getClass().getClassLoader()
				.getResourceAsStream ("validation/getOnePlayer.json");

		@BeforeEach
		void createPlayer() {
			Player player = new Player();
			responseBody = Utils.preparedCreatePlayer(player.toMap());
		}

		@AfterEach
		void deletePlayer() {
			String playersId = responseBody.get("_id").toString();
			Utils.deletePlayer(playersId);
		}

		@Test
		void checkMethodGetOne() {
			String playersEmail = responseBody.get("email").toString();
			Map requestBody = Map.of("email", playersEmail);
			Response response = httpRequest
					.body(requestBody)
					.when()
					.post("/automationTask/getOne")
					.then().statusCode(201)
					.and()
					.assertThat().body(JsonSchemaValidator.matchesJsonSchema(getOneJsonSchema))
					.extract().response();
			Assertions.assertEquals(responseBody.get("_id").toString(), response.jsonPath().get("id"));
			Assertions.assertEquals(responseBody.get("email").toString(), response.jsonPath().get("email"));
		}
	}

	@Nested
	class testCreation12Players {

		private List<String> createdPlayersIds = new ArrayList();
		private List<String> createdPlayersName = new ArrayList();

		private InputStream createPlayerJsonSchema = getClass().getClassLoader()
				.getResourceAsStream ("validation/createPlayer.json");
		private InputStream getAllPlayersJsonSchema = getClass().getClassLoader()
				.getResourceAsStream ("validation/getAllPlayers.json");

		@RepeatedTest(12)
		void createPlayerInCycle() {
			Player player = new Player();
			Response response = httpRequest
					.body(player.toMap())
					.when()
					.post("/automationTask/create")
					.then()
					.statusCode(201)
					.and()
					.assertThat().body(JsonSchemaValidator.matchesJsonSchema(createPlayerJsonSchema))
					.extract().response();
			createdPlayersIds.add(response.jsonPath().get("_id"));
			createdPlayersName.add(response.jsonPath().get("name"));
			System.out.println(createdPlayersName);
			Assertions.assertEquals(player.getCurrencyCode(), response.jsonPath().get("currency_code"));
			Assertions.assertEquals(player.getEmail(), response.jsonPath().get("email"));
		}

		@Test
		void getAllPlayersWithSorting() {

			Response response = httpRequest.when()
					.get("/automationTask/getAll")
					.then()
					.statusCode(200)
					.and()
					.assertThat().body(JsonSchemaValidator.matchesJsonSchema(getAllPlayersJsonSchema))
					.extract().response();
			List<Map<String, String>> responseList = response.getBody().as(List.class);
			List<String> responseListSorted = responseList.stream()
											.map(i->i.get("name"))
											.sorted((name1, name2) -> name1.compareTo(name2))
											.collect(Collectors.toList());
			Assertions.assertEquals(
					createdPlayersName.stream().sorted((name1, name2) -> name1.compareTo(name2)).collect(Collectors.toList()),
					responseListSorted);
		}

		@Test
		void deleteCreatedPlayers() {
			for (var playerId: createdPlayersIds) {
				Response response = httpRequest.when()
						.delete(String.format("%s/api/automationTask/deleteOne/%s", playerId))
						.then()
						.and()
						.statusCode(200)
						.extract().response();
				Assertions.assertEquals(playerId, response.jsonPath().get("id"));
			}
		}

		@Test
		void getIsAllPlayersEmpty() {
			Response response = httpRequest.when()
					.get("/automationTask/getAll")
					.then().extract().response();
			List<String> jsonResponse = response.jsonPath().getList("$");
			Assertions.assertTrue(jsonResponse.isEmpty());
		}
	}
}
