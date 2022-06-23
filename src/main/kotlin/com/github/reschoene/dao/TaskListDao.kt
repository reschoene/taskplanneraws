package com.github.reschoene.dao

import com.github.reschoene.model.TaskList
import mu.KotlinLogging
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class TaskListDao : DynamoDBDao("TaskLists") {
    private val idCol = "id"
    private val nameCol = "name"
    private val descriptionCol = "description"
    private val attributesToGet = listOf(idCol, nameCol, descriptionCol)

    private val logger = KotlinLogging.logger {}

    fun findAll(): List<TaskList?> {
        return super.findAll(attributesToGet).map(this::toTaskList)
    }

    fun getById(id: String?): TaskList? {
        return toTaskList(super.getById(idCol, id, attributesToGet))
    }

    fun createOrUpdate(taskList: TaskList): TaskList? {
        logger.info("taskList to create: $taskList")

        val item = mapOf(
            idCol to strAttributeValue(taskList.id),
            nameCol to strAttributeValue(taskList.name),
            descriptionCol to strAttributeValue(taskList.description)
        )

        return toTaskList(super.createOrUpdate(item))
    }

    fun delete(id: String) : TaskList?{
        return toTaskList(super.delete(idCol, id))
    }

    private fun toTaskList(item: Map<String, AttributeValue>?): TaskList?{
        return item?.takeIf { it.isNotEmpty() }
                   ?.let {
                        TaskList().apply {
                            this.id = item.getStrAttributeValue(idCol)
                            this.name = item.getStrAttributeValue(nameCol)
                            this.description = item.getStrAttributeValue(descriptionCol)
                        }
                   }
    }
}