package com.github.reschoene.dto

import com.github.reschoene.model.Greeting

data class GreetingResponse(var quotation: QuotationResponse, var imagePath: String = "")

fun Greeting.toGreetingResponse(): GreetingResponse = GreetingResponse(this.quotation.toQuotationResponse(), this.imagePath)