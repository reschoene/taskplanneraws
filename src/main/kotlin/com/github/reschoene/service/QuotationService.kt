package com.github.reschoene.service

import com.github.reschoene.dao.QuotationDao
import com.github.reschoene.dto.QuotationRequest
import com.github.reschoene.dto.QuotationResponse
import com.github.reschoene.dto.toQuotationResponse
import com.github.reschoene.exception.ValidationException
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

@ApplicationScoped
class QuotationService {
    @Inject
    lateinit var daoService: QuotationDao

    fun findAll(): List<QuotationResponse?> {
        return daoService.findAll().map { it?.toQuotationResponse() }
    }

    fun getById(id: String?): QuotationResponse? {
        if(id == null)
            throw ValidationException("id not present")

        return daoService.getById(id)?.toQuotationResponse()
    }

    fun create(quotationRequest: QuotationRequest?): QuotationResponse? {
        if(quotationRequest == null)
            throw ValidationException("request body not present")

        val task = quotationRequest.toModel()
        return daoService.create(task)?.toQuotationResponse()
    }

    fun update(id: String?, quotationRequest: QuotationRequest?): QuotationResponse? {
        if(id == null)
            throw ValidationException("id not present")

        if(quotationRequest == null)
            throw ValidationException("TaskListRequest not present")

        return daoService.update(id, quotationRequest.toModel())?.toQuotationResponse()
    }

    fun delete(id: String?) : QuotationResponse?{
        if(id == null)
            throw ValidationException("id not present")
        
        return daoService.delete(id)?.toQuotationResponse()
    }
}