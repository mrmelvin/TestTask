package support;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import support.Utils;
import java.util.Map;
import java.util.stream.Collectors;

import static support.Constants.*;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import support.models.Player;

public class Tmp {
	public static void main(String[] args) {
		//Player player = new Player();
		String id = "64f997d415ea3d2a62aaf2ca";
		Response response = given().headers(Generator.headersWithToken()).contentType(JSON)
				.when()
				.get(String.format("%s/api/automationTask/getAll", BASE_URL))
				.then().extract().response();
		List<Map<String, String>> lst = response.getBody().as(List.class);
		System.out.println(lst.size());
//		List<String> lstSorted = lst.stream().map(i->i.get("id")).sorted((name1, name2) -> name1.compareTo(name2)).collect(Collectors.toList());
//		System.out.println(lstSorted);
	}
}
