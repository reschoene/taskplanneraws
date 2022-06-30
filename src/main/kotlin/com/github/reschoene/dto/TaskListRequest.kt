package com.github.reschoene.dto

import com.github.reschoene.model.TaskList
import io.quarkus.runtime.annotations.RegisterForReflection

@RegisterForReflection
data class TaskListRequest (var id: String = "", var name: String = ""){
    fun toModel(): TaskList {
        val taskListRequest = this

        return TaskList().apply {
            this.id = taskListRequest.id
            this.name = taskListRequest.name
        }
    }
}