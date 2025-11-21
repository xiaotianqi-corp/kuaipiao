package org.xiaotianqi.kuaipiao.data.sources.db.schemas.company

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.timestamp
import org.xiaotianqi.kuaipiao.domain.address.AddressData
import org.xiaotianqi.kuaipiao.domain.organization.CompanyInfo
import org.xiaotianqi.kuaipiao.domain.organization.ContactInfo
import java.util.*
import kotlin.time.ExperimentalTime
import kotlin.time.toJavaInstant
import kotlin.time.toKotlinInstant

object CompaniesTable : UUIDTable("companies") {
    val name = varchar("name", 255)
    val tax_id = varchar("tax_id", 50).uniqueIndex()
    val industry = varchar("industry", 100).nullable()
    val street = varchar("street", 255)
    val city = varchar("city", 100)
    val state = varchar("state", 100)
    val postal_code = varchar("postal_code", 20)
    val country = varchar("country", 100)
    val phone = varchar("phone", 50)
    val email = varchar("email", 150)
    val contact_person = varchar("contact_person", 255)
    val previous_compliance_issues = text("previous_compliance_issues").nullable()
    val established_date = timestamp("established_date").nullable()

    val created_at = timestamp("created_at")
    val updated_at = timestamp("updated_at").nullable()
}

class CompanyEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<CompanyEntity>(CompaniesTable)

    var name by CompaniesTable.name
    var taxId by CompaniesTable.tax_id
    var industry by CompaniesTable.industry
    var street by CompaniesTable.street
    var city by CompaniesTable.city
    var state by CompaniesTable.state
    var postalCode by CompaniesTable.postal_code
    var country by CompaniesTable.country
    var phone by CompaniesTable.phone
    var email by CompaniesTable.email
    var contactPerson by CompaniesTable.contact_person
    var previousComplianceIssues by CompaniesTable.previous_compliance_issues
    var establishedDate by CompaniesTable.established_date
    var createdAt by CompaniesTable.created_at
    var updatedAt by CompaniesTable.updated_at
}

@ExperimentalTime
fun CompanyEntity.toCompanyInfo() = CompanyInfo(
    name = name,
    taxId = taxId,
    address = AddressData(
        street = street,
        city = city,
        state = state,
        postalCode = postalCode,
        country = country
    ),
    contact = ContactInfo(
        phone = phone,
        email = email,
        contactPerson = contactPerson
    ),
    createdAt = createdAt.toKotlinInstant(),
    updatedAt = updatedAt?.toKotlinInstant(),
)

@ExperimentalTime
fun CompanyEntity.fromCompanyInfo(data: CompanyInfo) {
    name = data.name
    taxId = data.taxId
    street = data.address.street
    city = data.address.city
    state = data.address.state
    postalCode = data.address.postalCode
    country = data.address.country
    phone = data.contact.phone
    email = data.contact.email
    contactPerson = data.contact.contactPerson
    createdAt = data.createdAt.toJavaInstant()
    updatedAt = data.updatedAt?.toJavaInstant()
}
