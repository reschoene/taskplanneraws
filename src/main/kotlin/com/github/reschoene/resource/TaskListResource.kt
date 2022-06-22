package com.github.reschoene.resource

import com.github.reschoene.dao.TaskListDao
import com.github.reschoene.model.TaskList
import mu.KotlinLogging
import javax.inject.Inject
import javax.ws.rs.DELETE
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.PUT
import javax.ws.rs.Path
import javax.ws.rs.PathParam


@Path("/tasklists")
class TaskListResourceResource {
    private val logger = KotlinLogging.logger {}

    @Inject
    lateinit var daoService: TaskListDao

    @GET
    fun getAll(): List<TaskList?> {
        logger.info("before get all")
        return daoService.findAll()
    }

    @GET
    @Path("{id}")
    fun getById(@PathParam("id")id: String): TaskList? {
        logger.info("chegou id = $id")
        return daoService.getById(id)
    }

    @POST
    fun add(taskList: TaskList): TaskList? {
        logger.info("chegou taskList = $taskList")
        return daoService.createOrUpdate(taskList)
    }

    @PUT
    fun update(taskList: TaskList): TaskList? {
        logger.info("chegou taskList = $taskList")
        return daoService.createOrUpdate(taskList)
    }

    @DELETE
    @Path("{id}")
    fun deleteById(@PathParam("id")id: String): TaskList? {
        logger.info("chegou id = $id")
        return daoService.delete(id)
    }
}