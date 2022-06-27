package com.github.reschoene.resource

import com.github.reschoene.dto.SwapTasksOrderParam
import com.github.reschoene.dto.TaskRequest
import com.github.reschoene.service.TaskService
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition
import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.enums.ParameterIn
import org.eclipse.microprofile.openapi.annotations.info.Info
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse
import javax.inject.Inject
import javax.ws.rs.*
import javax.ws.rs.core.Response

@OpenAPIDefinition(
    info = Info(
        title = "Tasks API",
        version = "1.0.0"
    )
)
@Path("/")
class TaskResource : BaseResource() {
    @Inject
    lateinit var taskService: TaskService

    @GET
    @Path("task")
    @Operation(summary = "Retrieves all tasks")
    @Parameter(name="id", `in` = ParameterIn.PATH,  description = "task list id")
    @APIResponse(responseCode = "200", description = "Returns a list of found items")
    @Produces("application/json")
    fun getAll(): Response{
        return buildSearchResponse(taskService.findAll())
    }

    @GET
    @Path("task/{id}")
    @Operation(summary = "Retrieve a task by ID")
    @Parameter(name="id", required=true, `in` = ParameterIn.PATH,  description = "task list id")
    @APIResponse(responseCode = "404", description = "Specified ID was not found")
    @APIResponse(responseCode = "200", description = "Specified ID was obtained")
    @Produces("application/json")
    fun get(@PathParam("id")id: String?): Response {
        return buildSearchResponse(taskService.getById(id))
    }

    @POST
    @Path("task")
    @Operation(summary = "Create a new task")
    @APIResponse(responseCode = "201", description = "item was successfully created")
    @Consumes("application/json")
    @Produces("application/json")
    fun add(taskRequest: TaskRequest?): Response {
        return buildCreateResponse(taskService.create(taskRequest))
    }

    @PUT
    @Path("task/{id}")
    @Operation(summary = "Update a task for the specified id")
    @Parameter(name="id", required=true, `in` = ParameterIn.PATH,  description = "task list id")
    @APIResponse(responseCode = "404", description = "Specified ID was not found")
    @APIResponse(responseCode = "200", description = "item was successfully updated")
    @Consumes("application/json")
    @Produces("application/json")
    fun update(@PathParam("id")id: String?, taskRequest: TaskRequest?): Response{
        return buildUpdateResponse(taskService.update(id, taskRequest))
    }

    @DELETE
    @Path("task/{id}")
    @Operation(summary = "Delete a task for the specified id")
    @Parameter(name="id", required=true, `in` = ParameterIn.PATH,  description = "task list id")
    @APIResponse(responseCode = "404", description = "Specified ID was not found")
    @APIResponse(responseCode = "200", description = "item was successfully deleted")
    @Produces("application/json")
    fun delete(@PathParam("id")id: String?): Response {
        return buildDeleteResponse(taskService.delete(id))
    }


    @GET
    @Path("tasksByFilter")
    @Operation(summary = "Retrieve a task by ID and numeric filter")
    @Parameter(name="taskListId", required=true, `in` = ParameterIn.QUERY,  description = "id of the task list to which this task belongs")
    @Parameter(name="taskFilter", required=true, `in` = ParameterIn.QUERY,  description = "Number of a task filter. Possible values: 1-Get All Tasks, 2-Only Completed Tasks, 3-Only Pending Tasks")
    @APIResponse(responseCode = "404", description = "Specified ID was not found")
    @APIResponse(responseCode = "200", description = "Specified ID was obtained")
    @Produces("application/json")
    fun tasksByFilter(@QueryParam("taskListId")taskListId: String?,
                      @QueryParam("taskFilter")taskFilter: Int?): Response {
        return buildSearchResponse(taskService.getByFilter(taskListId, taskFilter))
    }

    @PUT
    @Path("swapTasksOrder")
    @Operation(summary = "Retrieve a task by ID and numeric filter")
    @Consumes("application/json")
    @APIResponse(responseCode = "404", description = "Specified ID was not found")
    @APIResponse(responseCode = "200", description = "Tasks position was swapped")
    fun swapTasksOrder(swapTasksOrderParam: SwapTasksOrderParam?) {
        taskService.swapTasksOrder(swapTasksOrderParam)
    }
}