package com.github.reschoene.dto

import com.github.reschoene.model.TaskList

data class TaskListRequest (var id: String = "", var name: String = ""){
    fun toModel(): TaskList {
        val taskListRequest = this

        return TaskList().apply {
            this.id = taskListRequest.id
            this.name = taskListRequest.name
        }
    }
}