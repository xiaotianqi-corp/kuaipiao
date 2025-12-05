package org.xiaotianqi.kuaipiao.domain.email

import kotlinx.serialization.Serializable

@Serializable
data class ResendCreateDomainRequest(
    val name: String,
    val region: String = "us-east-1",
)

@Serializable
data class ResendDomainRecord(
    val record: String,
    val name: String,
    val type: String,
    val ttl: String? = null,
    val status: String,
    val value: String,
    val priority: Int? = null,
)

@Serializable
data class ResendDomainResponse(
    val id: String,
    val name: String,
    val created_at: String,
    val status: String,
    val records: List<ResendDomainRecord>? = null,
    val region: String,
)

@Serializable
data class ResendDomainItem(
    val id: String,
    val name: String,
    val status: String,
    val created_at: String,
    val region: String,
)

@Serializable
data class ResendDomainsListResponse(
    val data: List<ResendDomainItem>,
)
