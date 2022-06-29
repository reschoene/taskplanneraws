package com.github.reschoene.model

data class Task (var id: String = "", var name: String = ""){
    var taskListId: String = ""
    var description: String = ""
    var completed: Boolean = false
    var taskList: TaskList? = null
    var position: Long = 0L
}