package com.github.reschoene.resource

import javax.ws.rs.core.Response

open class BaseResource {
    protected fun <T> buildSearchResponse(model: T?): Response {
        return model?.let {
            Response.ok(it).status(Response.Status.OK).build()
        } ?: Response.status(Response.Status.NOT_FOUND).build()
    }

    protected fun <T> buildUpdateResponse(model: T?): Response = buildSearchResponse(model)

    protected fun <T> buildDeleteResponse(model: T?): Response = buildSearchResponse(model)

    protected fun <T> buildCreateResponse(model: T?): Response {
        return Response.ok(model)
            .status(Response.Status.CREATED)
            .build()
    }
}