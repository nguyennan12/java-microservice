package com.techie.microservice.order;

import com.techie.microservice.order.stubs.InventoryClientStub;
import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.mysql.MySQLContainer;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderServiceApplicationTests {

	@ServiceConnection
	static MySQLContainer mySQLContainer = new MySQLContainer("mysql:8.4");
	@LocalServerPort
	private Integer port;

	@BeforeAll
	static void startWireMock() {
		InventoryClientStub.start();
	}

	@AfterAll
	static void stopWireMock() {
		InventoryClientStub.stop();
	}

	@DynamicPropertySource
	static void registerInventoryUrl(DynamicPropertyRegistry registry) {
		registry.add("inventory.url", InventoryClientStub::baseUrl);
	}

	@BeforeEach
	void Setup(){
		RestAssured.port = port;
		RestAssured.baseURI = "http://localhost";
		InventoryClientStub.reset();
	}


	static {
		mySQLContainer.start();
	}

	@Test
	void shouldSubmitOrder() {
		String orderRequest = """
				{
					"skuCode": "iphone_15",
					"price": 1200,
					"quantity": 1
				}
				""";
		InventoryClientStub.stubInventoryCall("iphone_15", 1);

		var response = RestAssured.given()
				.contentType("application/json")
				.body(orderRequest)
				.when()
				.post("api/order")
				.then()
				.log().all()
				.statusCode(201)
				.extract()
				.body().asString();

		assertThat(response).contains("Order Placed Successfully");

	}

}
