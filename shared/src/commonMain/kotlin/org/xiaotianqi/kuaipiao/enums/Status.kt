package org.xiaotianqi.kuaipiao.enums

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class OperationStatus {
    PENDING,
    RUNNING,
    SUCCESS,
    FAILED,
    ISSUED,
    WARNING,
    CANCELLED
}

@Serializable
enum class EntityStatus {
    ACTIVE,
    INACTIVE,
    DRAFT,
    POSTED,
    REVERSED,
    SUSPENDED,
    PENDING,
    DELETED
}


@Serializable
enum class SeverityStatus {
    INFO,
    LOW,
    MEDIUM,
    HIGH
}

@Serializable
enum class CustomsStatus {
    DRAFT,
    SUBMITTED,
    UNDER_REVIEW,
    APPROVED,
    REJECTED,
    HELD,
    RELEASED
}

@Serializable
enum class CrossValidationStatus {
    FULL_MATCH,
    PARTIAL_MATCH,
    NO_MATCH,
    SOURCE_MISSING,
    PROCESSING_ERROR,
    INCOMPATIBLE_SOURCES,
    NO_DATA_FOUND,
    PASSED,
    REVIEW_REQUIRED,
}

@Serializable
enum class AuditStatus {
    AUDITED,
    IN_PROGRESS,
    UNAUDITED
}

@Serializable
enum class PaymentStatus {
    @SerialName("PENDING")
    PENDING,
    @SerialName("PARTIALLY_PAID")
    PARTIALLY_PAID,
    @SerialName("PAID")
    PAID,
    @SerialName("OVERDUE")
    OVERDUE,
    @SerialName("REFUNDED")
    REFUNDED
}

@Serializable
enum class InvoiceStatus {
    @SerialName("DRAFT")
    DRAFT,
    @SerialName("PENDING_AUTHORIZATION")
    PENDING_AUTHORIZATION,
    @SerialName("RECEIVED")
    RECEIVED,
    @SerialName("IN_PROCESS")
    IN_PROCESS,
    @SerialName("AUTHORIZED")
    AUTHORIZED,
    @SerialName("REJECTED")
    REJECTED,
    @SerialName("CANCELLED")
    CANCELLED
}