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
class TaskResourceTest {
    @Test
    @Order(1)
    fun postSuccessfullyCreatesTask() {
        val resp = given()
            .contentType(ContentType.JSON)
            .and()
            .body(TaskRequest().apply {
                id = "taskID"
                taskList = TaskListRequest("taskListId", "myTaskList")
                name = "test"
                description = "test task"
                completed = false
                position = 2L
            })
            .`when`().post("/task")
            .then()
            .extract().response()

        assertEquals(201, resp.statusCode())
        assertEquals("taskID", resp.jsonPath().getString("id"))
    }

    @Test
    @Order(2)
    fun getSuccessfullyRetrievesATaskList() {
        val resp = given()
            .contentType(ContentType.JSON)
            .`when`()
            .get("/task")
            .then()
            .extract().response()

        assertEquals(200, resp.statusCode())
        assertEquals("taskID", resp.jsonPath().getString("id[0]"))
        assertEquals("test", resp.jsonPath().getString("name[0]"))
        assertEquals("test task", resp.jsonPath().getString("description[0]"))
    }

    @Test
    @Order(3)
    fun getByIdSuccessfullyRetrievesATask() {
        val resp = given()
            .contentType(ContentType.JSON)
            .`when`()
            .get("/task/taskID")
            .then()
            .extract().response()

        assertEquals(200, resp.statusCode())
        assertEquals("taskID", resp.jsonPath().getString("id"))
        assertEquals("test", resp.jsonPath().getString("name"))
        assertEquals("test task", resp.jsonPath().getString("description"))
    }

    @Test
    @Order(4)
    fun putSuccessfullyUpdatesAndRetrievesATask() {
        val resp = given()
            .contentType(ContentType.JSON)
            .and()
            .body(TaskRequest().apply {
                id = "taskID"
                name = "new name"
                taskList = TaskListRequest("taskListId", "myTaskList")
                description = "new description"
                completed = false
                position = 2L
            })
            .`when`()
            .put("/task/taskID")
            .then()
            .extract().response()

        assertEquals(200, resp.statusCode())
        assertEquals("taskID", resp.jsonPath().getString("id"))
        assertEquals("new name", resp.jsonPath().getString("name"))
        assertEquals("new description", resp.jsonPath().getString("description"))
    }

    @Test
    @Order(5)
    fun deleteSuccessfullyRemovesAndRetrievesATask() {
        val resp = given()
            .`when`()
            .delete("/task/taskID")
            .then()
            .extract().response()

        assertEquals(200, resp.statusCode())
        assertEquals("taskID", resp.jsonPath().getString("id"))
        assertEquals("new name", resp.jsonPath().getString("name"))
        assertEquals("new description", resp.jsonPath().getString("description"))
    }

    @Test
    @Order(6)
    fun getByIdReturns404WhenIdNotFound() {
        val resp = given()
            .contentType(ContentType.JSON)
            .`when`()
            .get("/task/taskID")
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
            .body(TaskRequest().apply {
                id = "taskID"
                name = "new name"
                taskList = TaskListRequest("taskListId", "myTaskList")
                description = "new description"
                completed = false
                position = 2L
            })
            .`when`()
            .put("/task/taskID")
            .then()
            .extract().response()

        assertEquals(404, resp.statusCode())
    }

    @Test
    @Order(8)
    fun deleteReturns404WhenIdNotFound() {
        val resp = given()
            .`when`()
            .delete("/task/taskID")
            .then()
            .extract().response()

        assertEquals(404, resp.statusCode())
    }
}