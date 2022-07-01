package com.github.reschoene

import com.github.reschoene.dto.TaskListRequest
import com.github.reschoene.dto.TaskRequest
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
class TaskListResourceTest {
    @Test
    @Order(1)
    fun postSuccessfullyCreatesTaskList() {
        val resp = given()
            .contentType(ContentType.JSON)
            .and()
            .body(TaskListRequest("taskListID", "test"))
            .`when`().post("/tasklist")
            .then()
            .extract().response()

        assertEquals(201, resp.statusCode())
        assertEquals("taskListID", resp.jsonPath().getString("id"))
    }

    @Test
    @Order(2)
    fun getSuccessfullyRetrievesATaskList() {
        val resp = given()
            .contentType(ContentType.JSON)
            .`when`()
            .get("/tasklist")
            .then()
            .extract().response()

        assertEquals(200, resp.statusCode())
        assertEquals("taskListID", resp.jsonPath().getString("id[0]"))
        assertEquals("test", resp.jsonPath().getString("name[0]"))
    }

    @Test
    @Order(3)
    fun getByIdSuccessfullyRetrievesATaskList() {
        val resp = given()
            .contentType(ContentType.JSON)
            .`when`()
            .get("/tasklist/taskListID")
            .then()
            .extract().response()

        assertEquals(200, resp.statusCode())
        assertEquals("taskListID", resp.jsonPath().getString("id"))
        assertEquals("test", resp.jsonPath().getString("name"))
    }

    @Test
    @Order(4)
    fun putSuccessfullyUpdatesAndRetrievesATaskList() {
        val resp = given()
            .contentType(ContentType.JSON)
            .and()
            .body(TaskListRequest("taskListID", "new name"))
            .`when`()
            .put("/tasklist/taskListID")
            .then()
            .extract().response()

        assertEquals(200, resp.statusCode())
        assertEquals("taskListID", resp.jsonPath().getString("id"))
        assertEquals("new name", resp.jsonPath().getString("name"))
    }

    @Test
    @Order(5)
    fun deleteSuccessfullyRemovesAndRetrievesATaskList() {
        val resp = given()
            .`when`()
            .delete("/tasklist/taskListID")
            .then()
            .extract().response()

        assertEquals(200, resp.statusCode())
        assertEquals("taskListID", resp.jsonPath().getString("id"))
        assertEquals("new name", resp.jsonPath().getString("name"))
    }

    @Test
    @Order(6)
    fun getByIdReturns404WhenIdNotFound() {
        val resp = given()
            .contentType(ContentType.JSON)
            .`when`()
            .get("/tasklist/taskListID")
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
            .body(TaskListRequest("taskListID", "new name"))
            .`when`()
            .put("/tasklist/taskListID")
            .then()
            .extract().response()

        assertEquals(404, resp.statusCode())
    }

    @Test
    @Order(8)
    fun deleteReturns404WhenIdNotFound() {
        val resp = given()
            .`when`()
            .delete("/tasklist/taskListID")
            .then()
            .extract().response()

        assertEquals(404, resp.statusCode())
    }
}