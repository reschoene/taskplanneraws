package com.github.reschoene.resource

import com.github.reschoene.dao.TaskListDao
import com.github.reschoene.model.TaskList
import mu.KotlinLogging
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition
import org.eclipse.microprofile.openapi.annotations.info.Info
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
    private val logger = KotlinLogging.logger {}

    @Inject
    lateinit var daoService: TaskListDao

    @GET
    @APIResponse(responseCode = "200", description = "Returns a list of found items")
    @Produces("application/json")
    fun getAll(): Response{
        logger.info("before get all")
        return buildSearchResponse(daoService.findAll())
    }

    @GET
    @Path("{id}")
    @APIResponse(responseCode = "404", description = "Specified ID was not found")
    @APIResponse(responseCode = "200", description = "Specified ID was obtained")
    @Produces("application/json")
    fun get(@PathParam("id")id: String): Response {
        logger.info("GET endpoint. Received params: id = $id")
        return buildSearchResponse(daoService.getById(id))
    }

    @POST
    @APIResponse(responseCode = "201", description = "item was successfully created")
    @Consumes("application/json")
    @Produces("application/json")
    fun add(taskList: TaskList): Response {
        logger.info("POST endpoint. Received params:  taskList = $taskList")
        return buildCreateResponse(daoService.create(taskList))
    }

    @PUT
    @Path("{id}")
    @APIResponse(responseCode = "404", description = "Specified ID was not found")
    @APIResponse(responseCode = "200", description = "item was successfully updated")
    @Consumes("application/json")
    @Produces("application/json")
    fun update(@PathParam("id")id: String, taskList: TaskList): Response{
        logger.info("PATH endpoint. Received params: id = $id, taskList = $taskList")
        return buildUpdateResponse(daoService.update(id, taskList))
    }

    @DELETE
    @Path("{id}")
    @APIResponse(responseCode = "404", description = "Specified ID was not found")
    @APIResponse(responseCode = "200", description = "item was successfully deleted")
    @Produces("application/json")
    fun delete(@PathParam("id")id: String): Response {
        logger.info("DELETE endpoint. Received params: id = $id")
        return buildDeleteResponse(daoService.delete(id))
    }
}