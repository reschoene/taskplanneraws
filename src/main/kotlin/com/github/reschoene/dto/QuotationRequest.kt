package com.github.reschoene.dto

import com.github.reschoene.model.Quotation
import io.quarkus.runtime.annotations.RegisterForReflection

@RegisterForReflection
data class QuotationRequest (var id: String = ""){
    var phrase: String = ""
    var author: String = ""

    fun toModel(): Quotation {
        val quotationRequest = this

        return Quotation().apply {
            this.id = quotationRequest.id
            this.phrase = quotationRequest.phrase
            this.author = quotationRequest.author
        }
    }
}