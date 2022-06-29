package com.github.reschoene.dao

import com.github.reschoene.model.TaskList
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class TaskListDao : DynamoDBDao("TaskLists") {
    private val idCol = "id"
    private val nameCol = "name"
    private val taskCountCol = "taskCount"
    private val attributesToGet = listOf(idCol, nameCol, taskCountCol)

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
            nameCol to strAttributeValue(taskList.name),
            taskCountCol to longAttributeValue(taskList.taskCount)
        ))

        return taskList
    }

    fun update(id: String, taskList: TaskList): TaskList? {
        return super.getById(idCol, id, attributesToGet)?.let {
            super.createOrUpdate(mapOf(
                idCol to strAttributeValue(id),
                nameCol to strAttributeValue(taskList.name),
                taskCountCol to longAttributeValue(taskList.taskCount)
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
                this.taskCount = item.getLongAttributeValue(taskCountCol)
            }
        }
    }
}