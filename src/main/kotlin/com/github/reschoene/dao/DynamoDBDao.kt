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

    protected fun Map<String, AttributeValue>.getStrAttributeValue(colName: String) = get(colName)?.s() ?: ""

    protected fun newId() = UUID.randomUUID().toString()
}