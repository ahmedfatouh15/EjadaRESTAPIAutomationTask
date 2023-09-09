package Tests;

import org.testng.annotations.Test;

import ConfigReader.ConfigFileReader;
import io.restassured.http.ContentType;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import utils.Shared;

import static io.restassured.RestAssured.*;

import static org.hamcrest.Matchers.*;
import static org.testng.Assert.*;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;

import org.json.simple.JSONObject;

public class BooksAPIsTests {

	private Shared shared = new Shared();
	private ConfigFileReader reader = new ConfigFileReader();
	
	private String accessToken;
	private int bookId;
	private String bookName;
	private String bookType;
	private boolean bookAvailability;
	private String email;
	private String orderId;
	
	

	@Test(priority = -1)
	public void checkServiceAvailability() {
		baseURI = "https://simple-books-api.glitch.me";
		given().get("/status")
		.then()
		.statusCode(200)
		.body("status", equalTo("OK"));
	}

	@SuppressWarnings("unchecked")
	@Test(priority = -1)
	public void testAuthApi() {

		JSONObject requestBody = new JSONObject();

		requestBody.put("clientName", reader.getClientName());
		email = shared.generateFakeEmail(10, reader.getEmailDomain());
		requestBody.put("clientEmail", email);

		Response resp = given()
				.contentType(ContentType.JSON)
				.body(requestBody)
				.when()
				.post("/api-clients").then()
				.statusCode(201).extract().response();

		accessToken = resp.getBody().jsonPath().get("accessToken");
//		System.out.println("EMAIL: " + this.email);
//		System.out.println("DOMAIN: " + this.reader.getEmailDomain());

	}

	@SuppressWarnings("unchecked")
	//@Test
	public void testAuthApiFailure() {

		JSONObject requestBody = new JSONObject();

		requestBody.put("clientName", reader.getClientName());
		requestBody.put("clientEmail", email);

		Response resp = given()
				.contentType(ContentType.JSON)
				.body(requestBody)
				.when()
				.post("/api-clients").then()
				.statusCode(409).extract().response();

		String errorMsg = resp.getBody().jsonPath().get("error");
		assertEquals(errorMsg, "API client already registered. Try a different email.");

	}
	
	@Test
	public void testGETBooksApi() {

		Response resp = given().get("/books")
		.then()
		.statusCode(200)
		.extract().response();
		List<LinkedHashMap<String, Object>> books = resp.getBody().jsonPath().getList("");
		bookId = (Integer) books.get(0).get("id");
		bookName = (String) books.get(0).get("name");
		bookType = (String) books.get(0).get("type");
		bookAvailability = (boolean) books.get(0).get("available");
	}
	
	@Test(priority = 1)
	public void testGETBookApi() {
		
		File jsonSchema = new File("jsonSchema\\getBookResponseSchema.json");
		
		given().get("/books/" + bookId)
		.then().statusCode(200)
		.body(JsonSchemaValidator.matchesJsonSchema(jsonSchema))
		.body("id", equalTo(bookId))
		.body("name", equalTo(bookName))
		.body("type", equalTo(bookType))
		.body("available", equalTo(bookAvailability));
		
	}
	
	@SuppressWarnings("unchecked")
	@Test(priority = 1)
	public void testPOSTOrdersApi() {
		
		JSONObject requestBody = new JSONObject();

		requestBody.put("bookId", bookId);
		requestBody.put("customerName", "user first name");
		
		
		Response resp = given().header("Authorization", "Bearer " + accessToken)
		.contentType(ContentType.JSON)
		.body(requestBody)
		.post("/orders")
		.then().statusCode(201)
		.body("created", equalTo(true))
		.extract().response();
		
		JsonPath responseBody = resp.getBody().jsonPath();
		orderId = responseBody.get("orderId");
		assertNotEquals(orderId, null);
		assertNotEquals(orderId, "");
	}
	
	@Test(priority = 2)
	public void testGETOrdersApi() {
		
		File jsonSchema = new File("jsonSchema\\getOrdersResponseSchema.json");

		given().header("Authorization", "Bearer " + accessToken)
		.get("/orders")
		.then().statusCode(200)
		.body("bookId", contains(bookId))
		.body("id", contains(orderId))
		.body(JsonSchemaValidator.matchesJsonSchema(jsonSchema));
		

	}
	
	@SuppressWarnings("unchecked")
	@Test(priority = 2)
	public void testPATCHOrdersApi() {
		
		JSONObject requestBody = new JSONObject();

		requestBody.put("customerName", "updated user first name");
		
		given().header("Authorization", "Bearer " + accessToken)
		.contentType(ContentType.JSON)
		.body(requestBody)
		.patch("/orders/" + orderId)
		.then().statusCode(204);
		
	}
	
	@Test(priority = 3)
	public void testDELETEOrdersApi() {
				
		given().header("Authorization", "Bearer " + accessToken)
		.contentType(ContentType.JSON)
		.delete("/orders/" + orderId)
		.then().statusCode(204);
		
		given().header("Authorization", "Bearer " + accessToken)
		.get("/orders")
		.then().statusCode(200)
		.body("id", not(contains(orderId)));
		
	}

}
