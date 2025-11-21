package org.xiaotianqi.kuaipiao.domain.customer

import kotlinx.serialization.Serializable
import org.xiaotianqi.kuaipiao.domain.address.AddressData
import org.xiaotianqi.kuaipiao.enums.BuyerDocumentType
import org.xiaotianqi.kuaipiao.enums.CustomerType

@Serializable
data class CustomerData(
    val id: String,
    val firstName: String? = null,
    val lastName: String? = null,
    val businessName: String? = null,
    val customerType: CustomerType = CustomerType.INDIVIDUAL,
    val documentType: BuyerDocumentType = BuyerDocumentType.PASSPORT,
    val documentNumber: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val address: AddressData? = null,
    val dateOfBirth: String? = null,
    val issueDate: String? = null,
    val expirationDate: String? = null,
    val rawText: String? = null
)
