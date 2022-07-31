package com.github.reschoene.dao

import com.github.reschoene.model.TaskList
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class TaskListDao : DynamoDBDao("TaskLists") {
    private val idCol = "id"
    private val nameCol = "name"
    private val attributesToGet = listOf(idCol, nameCol)

    fun findAll(): List<TaskList?> {
        return super.findAll(attributesToGet).map(this::toTaskList)
    }

    fun getById(id: String?): TaskList? {
        return toTaskList(super.getById(idCol, id, attributesToGet))
    }

    fun create(taskList: TaskList): TaskList? {
        taskList.id = taskList.id.takeIf { it.isNotBlank() } ?: newId()

        super.createOrUpdate(mapOf(
            idCol to strAttributeValue(taskList.id),
            nameCol to strAttributeValue(taskList.name)
        ))

        return taskList
    }

    fun update(id: String, taskList: TaskList): TaskList? {
        return super.getById(idCol, id, attributesToGet)?.let {
            super.createOrUpdate(mapOf(
                idCol to strAttributeValue(id),
                nameCol to strAttributeValue(taskList.name)
            ))

            taskList.apply { this.id = id }
        }
    }

    fun delete(id: String) : TaskList?{
        return getById(id)?.let {
            super.delete(idCol, id)
            it
        }
    }

    private fun toTaskList(item: Map<String, AttributeValue>?): TaskList?{
        return item?.let {
            TaskList().apply {
                this.id = item.getStrAttributeValue(idCol)
                this.name = item.getStrAttributeValue(nameCol)
                this.taskCount = getTaskListCount(this.id)
            }
        }
    }

    fun getTaskListCount(taskListId: String): Int {
        val attributeValues = mutableMapOf(":taskListId" to strAttributeValue(taskListId))
        val filterExpression = "taskListId = :taskListId"

        return super.findByFilter("Tasks", attributeValues, filterExpression).toList().size
    }
}