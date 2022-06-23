package com.github.reschoene.resource

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Path("/test")
class TestResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    fun hello() = "test OK"
}