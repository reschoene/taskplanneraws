package com.github.reschoene

import com.github.reschoene.dto.QuotationRequest
import com.github.reschoene.testcontainers.DynamodbContainerResource
import io.quarkus.test.common.QuarkusTestResource
import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder

@QuarkusTest
@QuarkusTestResource(DynamodbContainerResource::class)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class GreetingResourceTest {
    @Test
    @Order(1)
    fun getReturns404WhenTheresNotQuotationOnDatabase() {
        val resp = given()
            .`when`()
            .get("/greeting")
            .then()
            .extract().response()

        assertEquals(404, resp.statusCode())
    }

    @Test
    @Order(1)
    fun getSuccessfullyRetrievesAGreeting() {
        val createQuotationResp = given()
            .contentType(ContentType.JSON)
            .and()
            .body(QuotationRequest().apply {
                id = "testID"
                author = "renato"
                phrase = "test containers helps a lot"
            })
            .`when`().post("/quotation")
            .then()
            .extract().response()

        assertEquals(201, createQuotationResp.statusCode())

        val resp = given()
            .`when`()
            .get("/greeting")
            .then()
            .extract().response()

        assertEquals(200, resp.statusCode())
        assertEquals("application/json", resp.contentType())
        assertFalse(resp.jsonPath().getString("quotation.phrase").isBlank())
        assertFalse(resp.jsonPath().getString("quotation.author").isBlank())
        assertFalse(resp.jsonPath().getString("imagePath").isBlank())
    }
}