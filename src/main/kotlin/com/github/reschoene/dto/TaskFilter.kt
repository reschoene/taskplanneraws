package com.github.reschoene.dto

import io.quarkus.runtime.annotations.RegisterForReflection

@RegisterForReflection
enum class TaskFilter(val value: Int){
    ALL_TASKS(1),
    COMPLETED(2),
    PENDING(3)
}