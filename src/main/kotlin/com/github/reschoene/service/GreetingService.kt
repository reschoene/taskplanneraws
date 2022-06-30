package com.github.reschoene.service

import com.github.reschoene.dao.QuotationDao
import com.github.reschoene.dto.GreetingResponse
import com.github.reschoene.dto.toGreetingResponse
import com.github.reschoene.model.Greeting
import com.github.reschoene.model.Quotation
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject


@ApplicationScoped
class GreetingService {
    val imageCount = 50
    var rand: Random = Random()

    @Inject
    lateinit var daoService: QuotationDao

    fun generatesRandomGreeting(): GreetingResponse? {
        val quotations: List<Quotation?> = daoService.findAll()

        return if (quotations.isEmpty()) null else {
            val photoId = getRandPhotoId()
            val randQuotation = getRandQuotation(quotations)
            val greeting = Greeting(randQuotation!!, "photos/$photoId.jpg")
            greeting.toGreetingResponse()
        }
    }

    private fun getRandQuotation(quotations: List<Quotation?>) = quotations[rand.nextInt(quotations.size)]

    /**
     * Generates a random number to be used as a table id
     * @return returns the generated id
     */
    //nextInt returns a random number between 0 and its parameters-1, so add to its parameters
    private fun getRandPhotoId() = rand.nextInt(imageCount) + 1
}