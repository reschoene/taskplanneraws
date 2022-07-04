package com.github.reschoene.dao

import com.github.reschoene.model.Quotation
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class QuotationDao : DynamoDBDao("Quotations") {
    private val idCol = "id"
    private val phraseCol = "phrase"
    private val authorCol = "author"
    private val attributesToGet = listOf(idCol, phraseCol, authorCol)

    fun findAll(): List<Quotation?> {
        return super.findAll(attributesToGet).map(this::toQuotation)
    }

    fun getById(id: String?): Quotation? {
        return toQuotation(super.getById(idCol, id, attributesToGet))
    }

    fun create(quotation: Quotation): Quotation? {
        quotation.id = quotation.id.takeIf { it.isNotBlank() } ?: newId()

        super.createOrUpdate(mapOf(
            idCol to strAttributeValue(quotation.id),
            phraseCol to strAttributeValue(quotation.phrase),
            authorCol to strAttributeValue(quotation.author)
        ))

        return quotation
    }

    fun update(id: String, quotation: Quotation): Quotation? {
        return super.getById(idCol, id, attributesToGet)?.let {
            super.createOrUpdate(mapOf(
                idCol to strAttributeValue(id),
                phraseCol to strAttributeValue(quotation.phrase),
                authorCol to strAttributeValue(quotation.author)
            ))

            quotation.apply { this.id = id }
        }
    }

    fun delete(id: String) : Quotation?{
        return getById(id)?.let {
            super.delete(idCol, id)
            it
        }
    }

    private fun toQuotation(item: Map<String, AttributeValue>?): Quotation?{
        return item?.let {
            Quotation().apply {
                this.id = item.getStrAttributeValue(idCol)
                this.phrase = item.getStrAttributeValue(phraseCol)
                this.author = item.getStrAttributeValue(authorCol)
            }
        }
    }
}