package com.github.reschoene.dto

import com.github.reschoene.model.Task

data class TaskResponse (var id: String = "", var name: String = ""){
    var description: String = ""
    var completed: Boolean = false
    var position: Long = 0L
    var taskList: TaskListResponse? = null
}

fun Task.toTaskResponse(): TaskResponse = TaskResponse().let { taskResponse ->
    taskResponse.id = this.id
    taskResponse.taskList = this.taskList?.toTaskListResponse()
    taskResponse.name = this.name
    taskResponse.description = this.description
    taskResponse.completed = this.completed
    taskResponse.position = this.position

    return taskResponse
}