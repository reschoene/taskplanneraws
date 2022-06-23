package com.github.reschoene.resource

import com.github.reschoene.dao.TaskListDao
import com.github.reschoene.model.TaskList
import mu.KotlinLogging
import javax.inject.Inject
import javax.ws.rs.*


@Path("/tasklist")
class TaskListResourceResource {
    private val logger = KotlinLogging.logger {}

    @Inject
    lateinit var daoService: TaskListDao

    @GET
    @Produces("application/json")
    fun getAll(): List<TaskList?> {
        logger.info("before get all")
        return daoService.findAll()
    }

    @GET
    @Path("{id}")
    @Produces("application/json")
    fun get(@PathParam("id")id: String): TaskList? {
        logger.info("chegou id = $id")
        return daoService.getById(id)
    }

    @POST
    @Consumes("application/json")
    @Produces("application/json")
    fun add(taskList: TaskList): TaskList? {
        logger.info("chegou taskList = $taskList")
        return daoService.create(taskList)
    }

    @PUT
    @Path("{id}")
    @Consumes("application/json")
    @Produces("application/json")
    fun update(@PathParam("id")id: String, taskList: TaskList): TaskList? {
        logger.info("chegou id = $id, taskList = $taskList")
        return daoService.update(id, taskList)
    }

    @DELETE
    @Path("{id}")
    @Produces("application/json")
    fun delete(@PathParam("id")id: String): TaskList? {
        logger.info("chegou id = $id")
        return daoService.delete(id)
    }
}