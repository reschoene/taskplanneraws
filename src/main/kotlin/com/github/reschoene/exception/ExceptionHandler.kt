package com.github.reschoene.exception

import com.github.reschoene.dto.ErrorResponse
import javax.ws.rs.core.Response
import javax.ws.rs.ext.ExceptionMapper
import javax.ws.rs.ext.Provider

@Provider
class ExceptionHandler : ExceptionMapper<ValidationException> {
    private val exceptionNotPresent: String = "Exception not present"

    override fun toResponse(exception: ValidationException?): Response {
        return Response
            .status(exception?.status ?: Response.Status.BAD_REQUEST)
            .entity(ErrorResponse(exception?.message ?: exceptionNotPresent))
            .build()
    }
}