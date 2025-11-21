package org.xiaotianqi.kuaipiao.domain.invoice

object InvoiceCountryRules {

    fun getTaxRate(country: String): Double = when (country.lowercase()) {
        "ecuador" -> 15.0
        "colombia" -> 19.0
        "chile" -> 19.0
        "peru" -> 18.0
        "estados unidos", "usa" -> 0.0
        else -> 10.0
    }

    fun getTaxType(country: String): String = when (country.lowercase()) {
        "ecuador" -> "IVA"
        "colombia" -> "IVA"
        "chile" -> "IVA"
        "peru" -> "IGV"
        "estados unidos", "usa" -> "SALES_TAX"
        else -> "VAT"
    }
}
