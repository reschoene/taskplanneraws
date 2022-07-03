package com.github.reschoene

import com.github.reschoene.dto.SwapTasksOrderParam
import com.github.reschoene.dto.TaskListRequest
import com.github.reschoene.dto.TaskRequest
import com.github.reschoene.dto.TaskResponse
import com.github.reschoene.testcontainers.DynamodbContainerResource
import io.quarkus.test.common.QuarkusTestResource
import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import io.restassured.response.Response
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream


@QuarkusTest
@QuarkusTestResource(DynamodbContainerResource::class)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TaskResourceTest {
    private var generatedTaskId: String = ""

    @Test
    @Order(1)
    fun postSuccessfullyCreatesTask() {
        val resp1 = createTask("", true, 1)

        assertEquals(201, resp1.statusCode())
        assertEquals("application/json", resp1.contentType())
        assertFalse(resp1.jsonPath().getString("id").isBlank())
        generatedTaskId = resp1.jsonPath().getString("id")

        val resp2 = createTask("taskID", false, 2)

        assertEquals(201, resp2.statusCode())
        assertEquals("application/json", resp2.contentType())
        assertEquals("taskID", resp2.jsonPath().getString("id"))
    }

    @Test
    @Order(2)
    fun postReturns400WhenRequestBodyIsNotEmpty() {
        val resp1 = given()
            .contentType(ContentType.JSON)
            .`when`().post("/task")
            .then()
            .extract().response()

        assertEquals(400, resp1.statusCode())
    }

    @Test
    @Order(3)
    fun getSuccessfullyRetrievesATaskList() {
        val resp = given()
            .`when`()
            .get("/task")
            .then()
            .extract().response()

        assertEquals(200, resp.statusCode())
        assertEquals("application/json", resp.contentType())
        assertEquals("test", resp.jsonPath().getString("name[0]"))
        assertEquals("test task", resp.jsonPath().getString("description[0]"))
    }

    @Test
    @Order(4)
    fun getByIdSuccessfullyRetrievesATask() {
        val resp = given()
            .`when`()
            .get("/task/taskID")
            .then()
            .extract().response()

        assertEquals(200, resp.statusCode())
        assertEquals("application/json", resp.contentType())
        assertEquals("taskID", resp.jsonPath().getString("id"))
        assertEquals("test", resp.jsonPath().getString("name"))
        assertEquals("test task", resp.jsonPath().getString("description"))
    }

    fun tasksByFilterSource(): Stream<Arguments> {
        return Stream.of(
            Arguments.of("taskListId", 1, 200, 2),
            Arguments.of("taskListIdThatNotExist", 1, 200, 0),
            Arguments.of(null, 1, 400, 0),
            Arguments.of("taskListId", null, 400, 0),
            Arguments.of("taskListId", 2, 200, 1),
            Arguments.of("taskListId", 3, 200, 1),
            Arguments.of("taskListId", 4, 400, 0)
        )
    }

    @ParameterizedTest
    @MethodSource("tasksByFilterSource")
    @Order(5)
    fun getTasksByFilterReturnsAccordingly(taskListId: String?, taskFilter: Int?, expectedStatus: Int, resArraySize: Int) {
        var reqSpec = given()

        if (taskListId != null)
            reqSpec = reqSpec.param("taskListId", taskListId)

        if (taskFilter != null)
            reqSpec = reqSpec.param("taskFilter", taskFilter)

        val resp = reqSpec
            .`when`()
            .get("/tasksByFilter")
            .then()
            .extract().response()

        assertEquals(expectedStatus, resp.statusCode())

        if (expectedStatus == 200) {
            assertEquals("application/json", resp.contentType())

            val respArray = resp.`as`(Array<TaskResponse>::class.java)
            assertEquals(resArraySize, respArray.size)
        }
    }

    @Test
    @Order(6)
    fun swapTasksOrderSuccessfullySwapsOrder() {
        val resp = given()
            .contentType(ContentType.JSON)
            .and()
            .body(SwapTasksOrderParam().apply {
                task1Id = generatedTaskId
                task2Id = "taskID"
                task1Pos = 2
                task2Pos = 1
            })
            .`when`()
            .put("/swapTasksOrder")
            .then()
            .extract().response()

        assertEquals(200, resp.statusCode())

        val respGET1 = given()
            .`when`()
            .get("/task/taskID")
            .then()
            .extract().response()

        assertEquals(200, respGET1.statusCode())
        assertEquals("application/json", respGET1.contentType())
        assertEquals("taskID", respGET1.jsonPath().getString("id"))
        assertEquals(1, respGET1.jsonPath().getInt("position"))

        val respGET2 = given()
            .`when`()
            .get("/task/$generatedTaskId")
            .then()
            .extract().response()

        assertEquals(200, respGET2.statusCode())
        assertEquals("application/json", respGET2.contentType())
        assertEquals(generatedTaskId, respGET2.jsonPath().getString("id"))
        assertEquals(2, respGET2.jsonPath().getInt("position"))

    }

    @Test
    @Order(7)
    fun swapTasksOrderReturns400WhenRequestBodyIsEmpty() {
        val resp = given()
            .contentType(ContentType.JSON)
            .`when`()
            .put("/swapTasksOrder")
            .then()
            .extract().response()

        assertEquals(400, resp.statusCode())
    }

    @Test
    @Order(8)
    fun swapTasksOrderReturns404WhenTask1IdWasNotFound() {
        val resp = given()
            .contentType(ContentType.JSON)
            .and()
            .body(SwapTasksOrderParam().apply {
                task1Id = "andIdThatDoesNotExist"
                task2Id = "taskID"
                task1Pos = 2
                task2Pos = 1
            })
            .`when`()
            .put("/swapTasksOrder")
            .then()
            .extract().response()

        assertEquals(404, resp.statusCode())
    }

    @Test
    @Order(9)
    fun swapTasksOrderReturns404WhenTask2IdWasNotFound() {
        val resp = given()
            .contentType(ContentType.JSON)
            .and()
            .body(SwapTasksOrderParam().apply {
                task1Id = generatedTaskId
                task2Id = "taskIDThatNotExist"
                task1Pos = 2
                task2Pos = 1
            })
            .`when`()
            .put("/swapTasksOrder")
            .then()
            .extract().response()

        assertEquals(404, resp.statusCode())
    }

    @Test
    @Order(10)
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
        assertEquals("application/json", resp.contentType())
        assertEquals("taskID", resp.jsonPath().getString("id"))
        assertEquals("new name", resp.jsonPath().getString("name"))
        assertEquals("new description", resp.jsonPath().getString("description"))
    }

    @Test
    @Order(11)
    fun deleteSuccessfullyRemovesAndRetrievesATask() {
        val resp = given()
            .`when`()
            .delete("/task/taskID")
            .then()
            .extract().response()

        assertEquals(200, resp.statusCode())
        assertEquals("application/json", resp.contentType())
        assertEquals("taskID", resp.jsonPath().getString("id"))
        assertEquals("new name", resp.jsonPath().getString("name"))
        assertEquals("new description", resp.jsonPath().getString("description"))
    }

    @Test
    @Order(12)
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
    @Order(13)
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
    @Order(14)
    fun putReturns400WhenReqBodyNotPresent() {
        val resp = given()
            .contentType(ContentType.JSON)
            .`when`()
            .put("/task/taskID")
            .then()
            .extract().response()

        assertEquals(400, resp.statusCode())
    }

    @Test
    @Order(15)
    fun deleteReturns404WhenIdNotFound() {
        val resp = given()
            .`when`()
            .delete("/task/taskID")
            .then()
            .extract().response()

        assertEquals(404, resp.statusCode())
    }

    private fun createTask(taskId: String, completed: Boolean, position: Long): Response {
        return given()
            .contentType(ContentType.JSON)
            .and()
            .body(TaskRequest().apply {
                this.id = taskId
                this.taskList = TaskListRequest("taskListId", "myTaskList")
                this.name = "test"
                this.description = "test task"
                this.completed = completed
                this.position = position
            })
            .`when`().post("/task")
            .then()
            .extract().response()
    }
}