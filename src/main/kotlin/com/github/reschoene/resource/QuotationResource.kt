package com.github.reschoene.resource

import com.github.reschoene.dto.QuotationRequest
import com.github.reschoene.service.QuotationService
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition
import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.enums.ParameterIn
import org.eclipse.microprofile.openapi.annotations.info.Info
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse
import javax.inject.Inject
import javax.ws.rs.*
import javax.ws.rs.core.Response

@Path("/quotation")
@OpenAPIDefinition(
    info = Info(
        title = "Quotation API",
        version = "1.0.0"
    )
)
class QuotationResource : BaseResource() {
    @Inject
    lateinit var quotationService: QuotationService

    @GET
    @Operation(summary = "Retrieves all quotations")
    @APIResponse(responseCode = "200", description = "Returns a list of found items")
    @Produces("application/json")
    fun getAll(): Response{
        return buildSearchResponse(quotationService.findAll())
    }

    @GET
    @Path("{id}")
    @Operation(summary = "Retrieve a quotation by ID")
    @Parameter(name="id", required=true, `in` = ParameterIn.PATH,  description = "quotation id")
    @APIResponse(responseCode = "404", description = "Specified ID was not found")
    @APIResponse(responseCode = "200", description = "Specified ID was obtained")
    @Produces("application/json")
    fun get(@PathParam("id")id: String?): Response {
        return buildSearchResponse(quotationService.getById(id))
    }

    @POST
    @Operation(summary = "Create a new quotation")
    @APIResponse(responseCode = "201", description = "item was successfully created")
    @Consumes("application/json")
    @Produces("application/json")
    fun add(quotationRequest: QuotationRequest?): Response {
        return buildCreateResponse(quotationService.create(quotationRequest))
    }

    @PUT
    @Path("{id}")
    @Operation(summary = "Update a quotation for the specified id")
    @Parameter(name="id", required=true, `in` = ParameterIn.PATH,  description = "quotation id")
    @APIResponse(responseCode = "404", description = "Specified ID was not found")
    @APIResponse(responseCode = "200", description = "item was successfully updated")
    @Consumes("application/json")
    @Produces("application/json")
    fun update(@PathParam("id")id: String?, quotationRequest: QuotationRequest?): Response{
        return buildUpdateResponse(quotationService.update(id, quotationRequest))
    }

    @DELETE
    @Path("{id}")
    @Operation(summary = "Delete a quotation for the specified id")
    @Parameter(name="id", required=true, `in` = ParameterIn.PATH,  description = "quotation id")
    @APIResponse(responseCode = "404", description = "Specified ID was not found")
    @APIResponse(responseCode = "200", description = "item was successfully deleted")
    @Produces("application/json")
    fun delete(@PathParam("id")id: String?): Response {
        return buildDeleteResponse(quotationService.delete(id))
    }
}