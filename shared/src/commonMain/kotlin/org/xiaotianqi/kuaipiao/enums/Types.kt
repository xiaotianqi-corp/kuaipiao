package org.xiaotianqi.kuaipiao.enums

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
enum class ClassifierType {
    PRODUCT_CATEGORY,
    EXPENSE_TYPE,
    DOCUMENT_TYPE,
    SENTIMENT,
    INTENT,
    LANGUAGE,
    CUSTOM,
    OCR
}

@Serializable
enum class ModelType {
    SALES_FORECAST,
    DEMAND_PREDICTION,
    RISK_ASSESSMENT,
    CUSTOMER_CHURN,
    PRICE_OPTIMIZATION,
    INVENTORY_PREDICTION,
    CASH_FLOW_FORECAST
}


@Serializable
enum class AlertType {
    MISSING_DOCUMENTATION,
    LOW_CONFIDENCE_TARIFF,
    REGULATORY_CHANGE,
    TAX_COMPLIANCE,
    RESTRICTED_PRODUCT
}


@Serializable
enum class DocumentType {
    INVOICE,
    RECEIPT,
    CONTRACT,
    IDENTIFICATION,
    OTHERS
}

@Serializable
enum class AccountingType {
    DEBIT,
    CREDIT
}

@Serializable
enum class EntryPurpose {
    UNKNOWN,
    SALES_INVOICE,
    PURCHASE_INVOICE,
    PAYMENT_RECEIPT,
    JOURNAL_ADJUSTMENT,
    BANK_RECONCILIATION,
    YEAR_END_CLOSING
}

@Serializable
enum class TransactionType {
    AGGREGATED,
    INCOME,
    EXPENSE,
    TRANSFER,
    LOAN,
    INVESTMENT
}

@Serializable
enum class FeatureType {
    TEXT_DETECTION,
    DOCUMENT_TEXT_DETECTION,
    LABEL_DETECTION,
    LOGO_DETECTION,
    FACE_DETECTION,
    OBJECT_LOCALIZATION
}

@Serializable
enum class FileType {
    XML,
    PDF,
    EXCEL,
    CSV,
    JSON,
    ZIP,
    IMAGE,
    UNKNOWN
}

@Serializable
enum class TaxType {
    @SerialName("VAT")
    VAT,
    @SerialName("SALES_TAX")
    SALES_TAX,
    @SerialName("WITHHOLDING")
    WITHHOLDING,
    @SerialName("IVA")
    IVA,
    @SerialName("IGV")
    IGV,
    @SerialName("OTHER")
    OTHER
}

@Serializable
enum class ItemType {
    @SerialName("product")
    PRODUCT,
    @SerialName("service")
    SERVICE,
    @SerialName("license")
    LICENSE,
    @SerialName("subscription")
    SUBSCRIPTION,
    @SerialName("gift_card")
    GIFT_CARD,
    @SerialName("bundle")
    BUNDLE,
    @SerialName("shipping")
    SHIPPING
}

@Serializable
enum class LedgerBook {
    FINANCIAL,
    TAX,
    MANAGEMENT,
    GENERAL
}

@Serializable
enum class CustomerType {
    INDIVIDUAL,
    BUSINESS
}

@Serializable
enum class BuyerDocumentType {
    NATIONAL_ID,
    PASSPORT,
    DRIVER_LICENSE,
    TAX_ID,
    BUSINESS_REGISTRY,
    RUC,
    DNI,
    CI,
    CUIT,
    RUT,
    NIT,
    VAT_ID,
    EIN,
    SSN,
    ITIN,
    UNKNOWN
}
