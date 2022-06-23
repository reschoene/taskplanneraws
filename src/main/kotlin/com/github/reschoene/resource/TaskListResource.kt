package com.github.reschoene.resource

import com.github.reschoene.dao.TaskListDao
import com.github.reschoene.model.TaskList
import mu.KotlinLogging
import javax.inject.Inject
import javax.ws.rs.*
import javax.ws.rs.core.Response

@Path("/tasklist")
class TaskListResourceResource : BaseResource() {
    private val logger = KotlinLogging.logger {}

    @Inject
    lateinit var daoService: TaskListDao

    @GET
    @Produces("application/json")
    fun getAll(): Response{
        logger.info("before get all")
        return buildSearchResponse(daoService.findAll())
    }

    @GET
    @Path("{id}")
    @Produces("application/json")
    fun get(@PathParam("id")id: String): Response {
        logger.info("chegou id = $id")
        return buildSearchResponse(daoService.getById(id))
    }

    @POST
    @Consumes("application/json")
    @Produces("application/json")
    fun add(taskList: TaskList): Response {
        logger.info("chegou taskList = $taskList")
        return buildCreateResponse(daoService.create(taskList))
    }

    @PUT
    @Path("{id}")
    @Consumes("application/json")
    @Produces("application/json")
    fun update(@PathParam("id")id: String, taskList: TaskList): Response{
        logger.info("chegou id = $id, taskList = $taskList")
        return buildUpdateResponse(daoService.update(id, taskList))
    }

    @DELETE
    @Path("{id}")
    @Produces("application/json")
    fun delete(@PathParam("id")id: String): Response {
        logger.info("chegou id = $id")
        return buildDeleteResponse(daoService.delete(id))
    }
}