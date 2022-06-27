package com.github.reschoene.dto

import com.github.reschoene.model.Task

data class TaskResponse (var id: String = "", var name: String = ""){
    var description: String = ""
    var completed: Boolean = false
    var position: Long = 0L
    var taskListId: String = ""
}

fun Task.toTaskResponse(): TaskResponse = TaskResponse().let { taskResponse ->
    taskResponse.id = this.id
    taskResponse.taskListId = this.taskListId
    taskResponse.name = this.name
    taskResponse.description = this.description
    taskResponse.completed = this.completed
    taskResponse.position = this.position

    return taskResponse
}