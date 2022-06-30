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

    fun create(Quotation: Quotation): Quotation? {
        Quotation.id = Quotation.id.takeIf { it.isNotBlank() } ?: newId()

        super.createOrUpdate(mapOf(
            idCol to strAttributeValue(Quotation.id),
            phraseCol to strAttributeValue(Quotation.phrase),
            authorCol to strAttributeValue(Quotation.author)
        ))

        return Quotation
    }

    fun update(id: String, Quotation: Quotation): Quotation? {
        return super.getById(idCol, id, attributesToGet)?.let {
            super.createOrUpdate(mapOf(
                idCol to strAttributeValue(id),
                phraseCol to strAttributeValue(Quotation.phrase),
                authorCol to strAttributeValue(Quotation.author)
            ))

            Quotation.apply { this.id = id }
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