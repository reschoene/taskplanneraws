package com.github.reschoene.dto

import io.quarkus.runtime.annotations.RegisterForReflection

@RegisterForReflection
data class ErrorResponse (val message: String)