package com.github.reschoene.testcontainers

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager
import org.testcontainers.containers.GenericContainer
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.*
import java.net.URI

class DynamodbContainerResource : QuarkusTestResourceLifecycleManager {
    private val dynamoDBTestContainer = GenericContainer("amazon/dynamodb-local:1.11.477")
        .withExposedPorts(8000)
        .withReuse(true)

    override fun start(): MutableMap<String, String> {
        dynamoDBTestContainer.start()

        configureDB()

        return mutableMapOf("quarkus.dynamodb.endpoint-override" to buildDynamoDBURL())
    }

    private fun configureDB() {
        val dynamoDB = buildDynamoDbClient()

        dynamoDB.createTable(createTable("Quotations"))
        dynamoDB.createTable(createTable("Tasks"))
        dynamoDB.createTable(createTable("TaskLists"))
    }

    private fun buildDynamoDbClient(): DynamoDbClient {
        return DynamoDbClient.builder()
            .endpointOverride(URI.create(buildDynamoDBURL()))
            .build()
    }

    private fun buildDynamoDBURL() = "http://localhost:${dynamoDBTestContainer.firstMappedPort}"

    private fun createTable(tableName: String) :CreateTableRequest{
        return CreateTableRequest.builder()
            .tableName(tableName)
            .attributeDefinitions(
                AttributeDefinition.builder()
                    .attributeName("id")
                    .attributeType(ScalarAttributeType.S)
                    .build()
            )
            .keySchema(
                KeySchemaElement.builder()
                    .attributeName("id")
                    .keyType(KeyType.HASH)
                    .build()
            )
            .provisionedThroughput(
                ProvisionedThroughput.builder()
                    .readCapacityUnits(1L)
                    .writeCapacityUnits(1L)
                    .build()
            )
            .build()
    }

    override fun stop() {
        dynamoDBTestContainer.stop()
    }
}