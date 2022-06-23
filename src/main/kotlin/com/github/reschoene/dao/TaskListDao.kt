package com.github.reschoene.dao

import com.github.reschoene.model.TaskList
import mu.KotlinLogging
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import java.util.stream.Collectors
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class TaskListDao : DynamoDBDao("TaskLists") {
    private val idCol = "id"
    private val nameCol = "name"
    private val descriptionCol = "description"
    private val attributesToGet = listOf(idCol, nameCol, descriptionCol)

    private val logger = KotlinLogging.logger {}

    fun findAll(): List<TaskList?> {
        return super.findAll(attributesToGet)
            .stream()
            .map(this::toTaskList)
            .collect(Collectors.toList())
    }

    fun getById(id: String?): TaskList? {
        return toTaskList(super.getById(idCol, id, attributesToGet))
    }

    fun createOrUpdate(taskList: TaskList): TaskList? {
        logger.info("taskList to create: $taskList")

        val item = mutableMapOf<String, AttributeValue>()
        item[idCol] = strAttributeValue(taskList.id)
        item[nameCol] = strAttributeValue(taskList.name)
        item[descriptionCol] = strAttributeValue(taskList.description)

        return toTaskList(super.createOrUpdate(item))
    }

    fun delete(id: String) : TaskList?{
        return toTaskList(super.delete(idCol, id))
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
}