package com.github.reschoene.service

import com.github.reschoene.dao.TaskListDao
import com.github.reschoene.dto.TaskListRequest
import com.github.reschoene.dto.TaskListResponse
import com.github.reschoene.dto.toTaskListResponse
import com.github.reschoene.exception.ValidationException
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

@ApplicationScoped
class TaskListService {
    @Inject
    lateinit var daoService: TaskListDao

    fun findAll(): List<TaskListResponse?> {
        return daoService.findAll().map { it?.toTaskListResponse() }
    }

    fun getById(id: String?): TaskListResponse? {
        if(id == null)
            throw ValidationException("id not present")

        return daoService.getById(id)?.toTaskListResponse()
    }

    fun create(TaskListRequest: TaskListRequest?): TaskListResponse? {
        if(TaskListRequest == null)
            throw ValidationException("request body not present")

        val task = TaskListRequest.toModel()
        return daoService.create(task)?.toTaskListResponse()
    }

    fun update(id: String?, TaskListRequest: TaskListRequest?): TaskListResponse? {
        if(id == null)
            throw ValidationException("id not present")

        if(TaskListRequest == null)
            throw ValidationException("TaskListRequest not present")

        return daoService.update(id, TaskListRequest.toModel())?.toTaskListResponse()
    }

    fun delete(id: String?) : TaskListResponse?{
        if(id == null)
            throw ValidationException("id not present")
        
        return daoService.delete(id)?.toTaskListResponse()
    }
}