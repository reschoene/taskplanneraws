package com.github.reschoene.dao

import com.github.reschoene.dto.TaskFilter
import com.github.reschoene.model.Task
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

@ApplicationScoped
class TaskDao : DynamoDBDao("Tasks") {
    @Inject
    lateinit var taskListDao: TaskListDao

    private val idCol = "id"
    private val taskListIdCol = "taskListId"
    private val nameCol = "name"
    private val descriptionCol = "description"
    private val positionCol = "position"
    private val completedCol = "completed"
    private val attributesToGet = listOf(idCol, taskListIdCol, nameCol, descriptionCol, completedCol, positionCol)

    fun findAll(): List<Task?> {
        return super.findAll(attributesToGet).map(this::toTask)
    }

    fun getById(id: String?): Task? {
        return toTask(super.getById(idCol, id, attributesToGet))
    }

    fun create(task: Task): Task? {
        task.id = task.id.takeIf { it.isNotBlank() } ?: newId()

        task.taskList?.let {
            if (it.id.isNotBlank()){
                task.position = taskListDao.getTaskListCount(it.id).toLong()
                task.taskListId = it.id
            }else {
                task.position = 0
                task.taskListId = taskListDao.create(it)?.id ?: ""
            }
        }

        super.createOrUpdate(mapOf(
            idCol to strAttributeValue(task.id),
            taskListIdCol to strAttributeValue(task.taskListId),
            nameCol to strAttributeValue(task.name),
            descriptionCol to strAttributeValue(task.description),
            completedCol to boolAttributeValue(task.completed),
            positionCol to longAttributeValue(task.position)
        ))

        return task
    }

    fun update(id: String, task: Task): Task? {
        return super.getById(idCol, id, attributesToGet)?.let {
            task.taskList?.let {
                task.taskListId = taskListDao.update(it.id, it)?.id ?: ""
            }

            super.createOrUpdate(mapOf(
                idCol to strAttributeValue(id),
                taskListIdCol to strAttributeValue(task.taskListId),
                nameCol to strAttributeValue(task.name),
                descriptionCol to strAttributeValue(task.description),
                completedCol to boolAttributeValue(task.completed),
                positionCol to longAttributeValue(task.position)
            ))

            task.apply { this.id = id }
        }
    }

    fun delete(id: String) : Task?{
        return getById(id)?.let {
            super.delete(idCol, id)
            it
        }
    }

    fun getByFilter(taskListId: String, taskFilter: TaskFilter): List<Task?> {
        val attributeValues = mutableMapOf(":taskListId" to strAttributeValue(taskListId))
        var filterExpression = "$taskListIdCol = :taskListId"

        filterExpression += when(taskFilter){
            TaskFilter.ALL_TASKS -> ""
            TaskFilter.PENDING -> {
                attributeValues[":completed"] = boolAttributeValue(false)
                " AND completed = :completed "
            }
            TaskFilter.COMPLETED -> {
                attributeValues[":completed"] = boolAttributeValue(true)
                " AND completed = :completed "
            }
        }

        val tasks = super.findByFilter(attributeValues, filterExpression).map(this::toTask)

        return tasks.sortedByDescending { it?.position ?: 0L}
    }

    private fun toTask(item: Map<String, AttributeValue>?): Task?{
        return item?.let {
            Task().apply {
                this.id = item.getStrAttributeValue(idCol)
                this.name = item.getStrAttributeValue(nameCol)
                this.description = item.getStrAttributeValue(descriptionCol)
                this.taskListId = item.getStrAttributeValue(taskListIdCol)
                this.completed = item.getBoolAttributeValue(completedCol)
                this.position = item.getLongAttributeValue(positionCol)

                if (this.taskListId.isNotEmpty()) {
                    taskListDao.getById(this.taskListId)?.let {
                        this.taskList = it
                    }
                }
            }
        }
    }
}