package com.github.reschoene.dto

import com.github.reschoene.model.Task

data class TaskRequest (var id: String = "", var name: String = ""){
    var description: String = ""
    var taskList: TaskListRequest? = null
    var completed: Boolean = false
    var position: Long = 0L

    fun toModel(): Task{
        val taskRequest = this

        return Task().apply {
            this.id = taskRequest.id
            this.taskList = taskRequest.taskList?.toModel()
            this.name = taskRequest.name
            this.description = taskRequest.description
            this.completed = taskRequest.completed
            this.position = taskRequest.position
        }
    }
}