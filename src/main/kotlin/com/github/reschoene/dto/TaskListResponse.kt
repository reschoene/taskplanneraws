package com.github.reschoene.dto

import com.github.reschoene.model.TaskList

data class TaskListResponse (var id: String = "", var name: String = ""){
    var description: String = ""
}

fun TaskList.toTaskListResponse(): TaskListResponse = TaskListResponse().let { taskListResponse ->
    taskListResponse.id = this.id
    taskListResponse.name = this.name
    taskListResponse.description = this.description

    return taskListResponse
}