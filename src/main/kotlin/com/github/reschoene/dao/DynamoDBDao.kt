package com.github.reschoene.dao

import software.amazon.awssdk.core.pagination.sync.SdkIterable
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.*
import java.util.*
import javax.inject.Inject

abstract class DynamoDBDao (private val tableName: String){
    @Inject
    lateinit var dynamoDB: DynamoDbClient

    protected fun findAll(attributesToGet: List<String>): SdkIterable<Map<String, AttributeValue>> {
        val scanRequest = ScanRequest.builder().tableName(tableName)
            .attributesToGet(attributesToGet).build()

        return dynamoDB.scanPaginator(scanRequest).items()
    }

    protected fun findByFilter(pTableName: String, attributeValues: Map<String, AttributeValue>, filterExpression: String): SdkIterable<Map<String, AttributeValue>> {
        val scanRequest = ScanRequest.builder().tableName(pTableName)
            .expressionAttributeValues(attributeValues)
            .filterExpression(filterExpression)
            .build()

        return dynamoDB.scanPaginator(scanRequest).items()
    }
    protected fun findByFilter(attributeValues: Map<String, AttributeValue>, filterExpression: String): SdkIterable<Map<String, AttributeValue>> {
        return findByFilter(tableName, attributeValues, filterExpression)
    }

    protected fun getById(idColName: String, idValue: String?, attributesToGet: List<String>): Map<String, AttributeValue>? {
        val key = mapOf(idColName to strAttributeValue(idValue))

        val getItemRequest = GetItemRequest.builder()
            .tableName(tableName)
            .key(key)
            .attributesToGet(attributesToGet)
            .build()

        return dynamoDB.getItem(getItemRequest).item()?.takeIf { it.isNotEmpty() }
    }

    protected fun createOrUpdate(item: Map<String, AttributeValue>): Map<String, AttributeValue>? {
        val createdItem = dynamoDB.putItem(
            PutItemRequest.builder()
                .tableName(tableName)
                .item(item)
                .build()
        )

        return createdItem.attributes()
    }

    protected fun delete(idColName: String, idValue: String?): Map<String, AttributeValue>? {
        val item = mapOf(idColName to strAttributeValue(idValue))

        val deleteItem = dynamoDB.deleteItem(
            DeleteItemRequest.builder()
                .tableName(tableName)
                .key(item)
                .build()
        )

        return deleteItem.attributes()
    }

    protected fun strAttributeValue(value: String?): AttributeValue =
        AttributeValue.builder().s(value).build()

    protected fun boolAttributeValue(value: Boolean?): AttributeValue =
        AttributeValue.builder().bool(value).build()

    protected fun longAttributeValue(value: Long?): AttributeValue =
        AttributeValue.builder().n(value?.toString() ?: "0").build()

    protected fun Map<String, AttributeValue>.getStrAttributeValue(colName: String) = get(colName)?.s() ?: ""
    protected fun Map<String, AttributeValue>.getBoolAttributeValue(colName: String) = get(colName)?.bool() ?: false
    protected fun Map<String, AttributeValue>.getLongAttributeValue(colName: String) = get(colName)?.n()?.toLong() ?: 0L

    protected fun newId() = UUID.randomUUID().toString()
}