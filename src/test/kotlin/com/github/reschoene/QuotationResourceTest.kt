package com.github.reschoene

import com.github.reschoene.dto.QuotationRequest
import com.github.reschoene.testcontainers.DynamodbContainerResource
import io.quarkus.test.common.QuarkusTestResource
import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals

@QuarkusTest
@QuarkusTestResource(DynamodbContainerResource::class)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class QuotationResourceTest {
    @Test
    @Order(1)
    fun postSuccessfullyCreatesQuotation() {
        val resp = given()
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

        assertEquals(201, resp.statusCode())
        assertEquals("testID", resp.jsonPath().getString("id"))
    }

    @Test
    @Order(2)
    fun getSuccessfullyRetrievesAQuotationList() {
        val resp = given()
            .contentType(ContentType.JSON)
            .`when`()
            .get("/quotation")
            .then()
            .extract().response()

        assertEquals(200, resp.statusCode())
        assertEquals("testID", resp.jsonPath().getString("id[0]"))
        assertEquals("renato", resp.jsonPath().getString("author[0]"))
        assertEquals("test containers helps a lot", resp.jsonPath().getString("phrase[0]"))
    }

    @Test
    @Order(3)
    fun getByIdSuccessfullyRetrievesAQuotation() {
        val resp = given()
            .contentType(ContentType.JSON)
            .`when`()
            .get("/quotation/testID")
            .then()
            .extract().response()

        assertEquals(200, resp.statusCode())
        assertEquals("testID", resp.jsonPath().getString("id"))
        assertEquals("renato", resp.jsonPath().getString("author"))
        assertEquals("test containers helps a lot", resp.jsonPath().getString("phrase"))
    }

    @Test
    @Order(4)
    fun putSuccessfullyUpdatesAndRetrievesAQuotation() {
        val resp = given()
            .contentType(ContentType.JSON)
            .and()
            .body(QuotationRequest().apply {
                author = "Renatao"
                phrase = "Kotlin shines"
            })
            .`when`()
            .put("/quotation/testID")
            .then()
            .extract().response()

        assertEquals(200, resp.statusCode())
        assertEquals("testID", resp.jsonPath().getString("id"))
        assertEquals("Renatao", resp.jsonPath().getString("author"))
        assertEquals("Kotlin shines", resp.jsonPath().getString("phrase"))
    }

    @Test
    @Order(5)
    fun deleteSuccessfullyRemovesAndRetrievesAQuotation() {
        val resp = given()
            .`when`()
            .delete("/quotation/testID")
            .then()
            .extract().response()

        assertEquals(200, resp.statusCode())
        assertEquals("testID", resp.jsonPath().getString("id"))
        assertEquals("Renatao", resp.jsonPath().getString("author"))
        assertEquals("Kotlin shines", resp.jsonPath().getString("phrase"))
    }

    @Test
    @Order(6)
    fun getByIdReturns404WhenIdNotFound() {
        val resp = given()
            .contentType(ContentType.JSON)
            .`when`()
            .get("/quotation/testID")
            .then()
            .extract().response()

        assertEquals(404, resp.statusCode())
    }

    @Test
    @Order(7)
    fun putReturns404WhenIdNotFound() {
        val resp = given()
            .contentType(ContentType.JSON)
            .and()
            .body(QuotationRequest().apply {
                author = "Renatao"
                phrase = "Kotlin shines"
            })
            .`when`()
            .put("/quotation/testID")
            .then()
            .extract().response()

        assertEquals(404, resp.statusCode())
    }

    @Test
    @Order(8)
    fun deleteReturns404WhenIdNotFound() {
        val resp = given()
            .`when`()
            .delete("/quotation/testID")
            .then()
            .extract().response()

        assertEquals(404, resp.statusCode())
    }
}