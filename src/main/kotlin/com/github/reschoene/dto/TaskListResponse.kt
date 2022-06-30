package com.github.reschoene.dto

import com.github.reschoene.model.TaskList
import io.quarkus.runtime.annotations.RegisterForReflection

@RegisterForReflection
data class TaskListResponse (var id: String = "", var name: String = ""){
    var taskCount: Int = 0
}

fun TaskList.toTaskListResponse(): TaskListResponse = TaskListResponse().let { taskListResponse ->
    taskListResponse.id = this.id
    taskListResponse.name = this.name
    taskListResponse.taskCount = this.taskCount

    return taskListResponse
}