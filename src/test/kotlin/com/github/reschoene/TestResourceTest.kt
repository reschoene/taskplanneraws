package com.github.reschoene

import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured.given
import org.hamcrest.CoreMatchers.`is`
import org.junit.jupiter.api.Test

@QuarkusTest
class TestResourceTest {

    @Test
    fun testEndpoint() {
        given()
          .`when`().get("/test")
          .then()
             .statusCode(200)
             .body(`is`("test OK"))
    }

}