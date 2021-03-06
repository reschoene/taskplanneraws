package com.github.reschoene.dto

import io.quarkus.runtime.annotations.RegisterForReflection

@RegisterForReflection
data class SwapTasksOrderParam (var task1Id: String = "", var task2Id: String = "",
                                var task1Pos: Long = 0L, var task2Pos: Long = 0L)