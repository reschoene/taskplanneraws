package com.github.reschoene.resource

import com.github.reschoene.service.GreetingService
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition
import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.info.Info
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse
import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.Response

@Path("/greeting")
@OpenAPIDefinition(
    info = Info(
        title = "Greeting API",
        version = "1.0.0"
    )
)
class GreetingResource : BaseResource() {
    @Inject
    lateinit var greetingService: GreetingService

    @GET
    @Operation(summary = "Get a random greeting")
    @APIResponse(responseCode = "200", description = "Returns a list of found items")
    @APIResponse(responseCode = "404", description = "Database has no quotation")
    @Produces("application/json")
    fun getAll(): Response{
        return buildSearchResponse(greetingService.generatesRandomGreeting())
    }
}