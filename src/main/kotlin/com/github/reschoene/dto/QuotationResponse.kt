package com.github.reschoene.dto

import com.github.reschoene.model.Quotation

data class QuotationResponse(var id: String = ""){
    var phrase: String = ""
    var author: String = ""
}

fun Quotation.toQuotationResponse(): QuotationResponse = QuotationResponse().let { quotationResponse ->
    quotationResponse.id = this.id
    quotationResponse.phrase = this.phrase
    quotationResponse.author = this.author

    return quotationResponse
}