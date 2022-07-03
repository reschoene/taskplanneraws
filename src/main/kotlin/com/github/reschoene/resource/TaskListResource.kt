package com.github.reschoene.resource

import com.github.reschoene.dto.TaskListRequest
import com.github.reschoene.service.TaskListService
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition
import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.enums.ParameterIn
import org.eclipse.microprofile.openapi.annotations.info.Info
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse
import javax.inject.Inject
import javax.ws.rs.*
import javax.ws.rs.core.Response

@Path("/tasklist")
@OpenAPIDefinition(
    info = Info(
        title = "Task Lists API",
        version = "1.0.0"
    )
)
class TaskListResourceResource : BaseResource() {
    @Inject
    lateinit var taskListService: TaskListService

    @GET
    @Operation(summary = "Retrieves all task lists")
    @APIResponse(responseCode = "200", description = "Returns a list of found items")
    @Produces("application/json")
    fun getAll(): Response{
        return buildSearchResponse(taskListService.findAll())
    }

    @GET
    @Path("{id}")
    @Operation(summary = "Retrieve a task list by ID")
    @Parameter(name="id", required=true, `in` = ParameterIn.PATH,  description = "task id")
    @APIResponse(responseCode = "404", description = "Specified ID was not found")
    @APIResponse(responseCode = "200", description = "Specified ID was obtained")
    @Produces("application/json")
    fun get(@PathParam("id")id: String): Response {
        return buildSearchResponse(taskListService.getById(id))
    }

    @POST
    @Operation(summary = "Create a new task list")
    @APIResponse(responseCode = "201", description = "item was successfully created")
    @Consumes("application/json")
    @Produces("application/json")
    fun add(taskListRequest: TaskListRequest?): Response {
        return buildCreateResponse(taskListService.create(taskListRequest))
    }

    @PUT
    @Path("{id}")
    @Operation(summary = "Update a task list for the specified id")
    @Parameter(name="id", required=true, `in` = ParameterIn.PATH,  description = "task id")
    @APIResponse(responseCode = "404", description = "Specified ID was not found")
    @APIResponse(responseCode = "200", description = "item was successfully updated")
    @Consumes("application/json")
    @Produces("application/json")
    fun update(@PathParam("id")id: String, taskListRequest: TaskListRequest?): Response{
        return buildUpdateResponse(taskListService.update(id, taskListRequest))
    }

    @DELETE
    @Path("{id}")
    @Operation(summary = "Delete a task list for the specified id")
    @Parameter(name="id", required=true, `in` = ParameterIn.PATH,  description = "task id")
    @APIResponse(responseCode = "404", description = "Specified ID was not found")
    @APIResponse(responseCode = "200", description = "item was successfully deleted")
    @Produces("application/json")
    fun delete(@PathParam("id")id: String): Response {
        return buildDeleteResponse(taskListService.delete(id))
    }
}