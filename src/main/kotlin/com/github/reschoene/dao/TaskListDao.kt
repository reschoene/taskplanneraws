package com.github.reschoene.dao

import com.github.reschoene.model.TaskList
import mu.KotlinLogging
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.*
import java.util.stream.Collectors
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

@ApplicationScoped
class TaskListDao {
    private val tableName = "TaskLists"
    private val idCol = "id"
    private val nameCol = "name"
    private val descriptionCol = "description"

    @Inject
    lateinit var dynamoDB: DynamoDbClient

    private val logger = KotlinLogging.logger {}

    fun findAll(): List<TaskList?> {
        val scanRequest = ScanRequest.builder().tableName(tableName)
            .attributesToGet(idCol, nameCol, descriptionCol).build()

        return dynamoDB.scanPaginator(scanRequest).items()
            .stream()
            .map(this::toTaskList)
            .collect(Collectors.toList())
    }

    fun getById(id: String?): TaskList? {
        val key = mutableMapOf<String, AttributeValue>()
        key[idCol] = strAttributeValue(id)

        val getItemRequest = GetItemRequest.builder()
            .tableName(tableName)
            .key(key)
            .attributesToGet(idCol, nameCol, descriptionCol)
            .build()

        return toTaskList(dynamoDB.getItem(getItemRequest).item())
    }

    fun createOrUpdate(taskList: TaskList): TaskList? {
        logger.info("taskList to create: $taskList")

        val item = mutableMapOf<String, AttributeValue>()
        item[idCol] = strAttributeValue(taskList.id)
        item[nameCol] = strAttributeValue(taskList.name)
        item[descriptionCol] = strAttributeValue(taskList.description)

        val createdItem = dynamoDB.putItem(
            PutItemRequest.builder()
                .tableName(tableName)
                .item(item)
                .build()
        )

        return toTaskList(createdItem.attributes())
    }

    fun delete(id: String) : TaskList?{
        val item = mutableMapOf<String, AttributeValue>()
        item[idCol] = strAttributeValue(id)

        val deleteItem = dynamoDB.deleteItem(
            DeleteItemRequest.builder()
                .tableName(tableName)
                .key(item)
                .build()
        )

        return toTaskList(deleteItem.attributes())
    }

    private fun toTaskList(item: Map<String, AttributeValue>?): TaskList?{
        var taskList: TaskList? = null

        if (item != null && item.isNotEmpty()) {
            taskList = TaskList()
            taskList.id = item[idCol]?.s() ?: ""
            taskList.name = item[nameCol]?.s() ?: ""
            taskList.description = item[descriptionCol]?.s() ?: ""
        }
        return taskList
    }

    private fun strAttributeValue(value: String?) =
        AttributeValue.builder().s(value).build()
}