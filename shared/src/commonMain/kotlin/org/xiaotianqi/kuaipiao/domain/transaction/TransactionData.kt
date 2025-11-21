package org.xiaotianqi.kuaipiao.domain.transaction

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.xiaotianqi.kuaipiao.enums.RiskLevel
import org.xiaotianqi.kuaipiao.enums.TransactionType
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Serializable
@ExperimentalTime
data class TransactionData(
    val id: String,
    val date: Instant,
    val amount: Double,
    val currency: String,
    val type: TransactionType,
    val counterparty: String,
    val description: String,
    val metadata: Map<String, String> = emptyMap()
) {
    fun toJson(): String = Json.encodeToString(this)
}

@Serializable
data class SuspiciousTransaction(
    val transactionId: String,
    val riskLevel: RiskLevel,
    val reasons: List<String>,
    val suggestedActions: List<String>
)