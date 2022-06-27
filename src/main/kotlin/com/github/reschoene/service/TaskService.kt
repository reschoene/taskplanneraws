package com.github.reschoene.service

import com.github.reschoene.dao.TaskDao
import com.github.reschoene.dto.*
import com.github.reschoene.exception.ValidationException
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

@ApplicationScoped
class TaskService {
    @Inject
    lateinit var daoService: TaskDao

    fun findAll(): List<TaskResponse?> {
        return daoService.findAll().map { it?.toTaskResponse() }
    }

    fun getById(id: String?): TaskResponse? {
        if(id == null)
            throw ValidationException("id not present")

        return daoService.getById(id)?.toTaskResponse()
    }

    fun create(taskRequest: TaskRequest?): TaskResponse? {
        if(taskRequest == null)
            throw ValidationException("request body not present")

        val task = taskRequest.toModel()
        return daoService.create(task)?.toTaskResponse()
    }

    fun update(id: String?, taskRequest: TaskRequest?): TaskResponse? {
        if(id == null)
            throw ValidationException("id not present")

        if(taskRequest == null)
            throw ValidationException("taskRequest not present")

        return daoService.update(id, taskRequest.toModel())?.toTaskResponse()
    }

    fun delete(id: String?) : TaskResponse?{
        if(id == null)
            throw ValidationException("id not present")

        return daoService.delete(id)?.toTaskResponse()
    }

    fun getByFilter(taskListId: String?, taskFilter: Int?): List<TaskResponse?> {
        if(taskListId == null)
            throw ValidationException("taskListId not present")

        if(taskFilter == null)
            throw ValidationException("taskFilter not present")

        val taskFilterFound = TaskFilter.values().filter { it.value == taskFilter }

        if(taskFilterFound.isEmpty())
            throw ValidationException("taskFilter is invalid. Possible values: 1, 2 and 3. Filters: 1-Get All Tasks, 2-Only Completed Tasks, 3-Only Pending Tasks")

        val tasks = daoService.getByFilter(taskListId, taskFilterFound.first())
        return tasks.map { it?.toTaskResponse() }
    }

    fun swapTasksOrder(swapTasksOrderParam: SwapTasksOrderParam?) {
        if(swapTasksOrderParam == null)
            throw ValidationException("swapTasksOrderParam not present")

        val task1 = daoService.getById(swapTasksOrderParam.task1Id)
        val task2 = daoService.getById(swapTasksOrderParam.task2Id)

        if(task1 == null)
            throw ValidationException("Task for id task1Id was not found")

        if(task2 == null)
            throw ValidationException("Task for id task2Id was not found")

        task1.position = swapTasksOrderParam.task1Pos
        task2.position = swapTasksOrderParam.task2Pos

        daoService.update(task1.id, task1)
        daoService.update(task2.id, task2)
    }
}