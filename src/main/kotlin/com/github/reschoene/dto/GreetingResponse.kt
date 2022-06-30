package com.github.reschoene.dto

import com.github.reschoene.model.Greeting
import io.quarkus.runtime.annotations.RegisterForReflection

@RegisterForReflection
data class GreetingResponse(var quotation: QuotationResponse, var imagePath: String = "")

fun Greeting.toGreetingResponse(): GreetingResponse = GreetingResponse(this.quotation.toQuotationResponse(), this.imagePath)