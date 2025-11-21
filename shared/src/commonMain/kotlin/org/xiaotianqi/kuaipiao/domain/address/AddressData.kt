package org.xiaotianqi.kuaipiao.domain.address

import kotlinx.serialization.Serializable


@Serializable
data class AddressData(
    val street: String,
    val city: String,
    val state: String,
    val postalCode: String,
    val country: String
)