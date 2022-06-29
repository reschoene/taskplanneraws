package com.github.reschoene.dto

import com.github.reschoene.model.TaskList

data class TaskListResponse (var id: String = "", var name: String = ""){
    var taskCount: Long = 0L
}

fun TaskList.toTaskListResponse(): TaskListResponse = TaskListResponse().let { taskListResponse ->
    taskListResponse.id = this.id
    taskListResponse.name = this.name
    taskListResponse.taskCount = this.taskCount

    return taskListResponse
}