package org.xiaotianqi.kuaipiao.data.sources.external

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.*
import org.xiaotianqi.kuaipiao.domain.organization.BusinessDataValidationRequest
import org.xiaotianqi.kuaipiao.domain.organization.BusinessDataValidationResult
import org.xiaotianqi.kuaipiao.domain.organization.CompanySearchResult
import org.xiaotianqi.kuaipiao.domain.product.ProductSearchResult
import org.xiaotianqi.kuaipiao.domain.product.TariffSearchResult
import org.xiaotianqi.kuaipiao.domain.product.TariffSuggestion
import org.xiaotianqi.kuaipiao.domain.tax.TaxRegulation
import org.xiaotianqi.kuaipiao.domain.tax.TaxRegulationSearchResult
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

private val logger = KotlinLogging.logger {}

@ExperimentalTime
class WebSearchDataSource(
    private val httpClient: HttpClient
) {

    suspend fun searchProductInfo(
        productName: String,
        country: String = "EC",
        language: String = "es"
    ): ProductSearchResult {
        return try {
            val searchQuery = "$productName $country taxes category"
            simulateProductSearch(productName, country)
        } catch (e: Exception) {
            logger.warn(e) { "Error in web search for product: $productName" }
            ProductSearchResult.empty()
        }
    }

    suspend fun searchTaxRegulations(
        country: String,
        taxType: String? = null
    ): TaxRegulationSearchResult {
        return try {
            val query = buildTaxSearchQuery(country, taxType)
            simulateTaxSearch(country, taxType)
        } catch (e: Exception) {
            logger.warn(e) { "Error searching tax regulations for: $country" }
            TaxRegulationSearchResult.empty()
        }
    }

    suspend fun searchCompanyInfo(
        companyName: String,
        country: String
    ): CompanySearchResult {
        return try {
            simulateCompanySearch(companyName, country)
        } catch (e: Exception) {
            logger.warn(e) { "Error searching company info: $companyName" }
            CompanySearchResult.empty()
        }
    }

    suspend fun searchTariffCodes(
        productDescription: String,
        countries: List<String>
    ): TariffSearchResult {
        return try {
            simulateTariffSearch(productDescription, countries)
        } catch (e: Exception) {
            logger.warn(e) { "Error searching tariff codes for: $productDescription" }
            TariffSearchResult.empty()
        }
    }

    suspend fun validateBusinessData(
        data: BusinessDataValidationRequest
    ): BusinessDataValidationResult {
        return try {
            simulateBusinessValidation(data)
        } catch (e: Exception) {
            logger.warn(e) { "Error validating business data" }
            BusinessDataValidationResult(
                isValid = false,
                confidence = 0.0,
                sources = emptyList(),
                warnings = listOf("External validation error")
            )
        }
    }

    private fun buildTaxSearchQuery(country: String, taxType: String?): String {
        val baseQuery = "tax regulations $country"
        return if (taxType != null) "$baseQuery $taxType" else baseQuery
    }

    private fun simulateProductSearch(productName: String, country: String): ProductSearchResult {
        return ProductSearchResult(
            productName = productName,
            category = inferCategory(productName),
            taxCategory = inferTaxCategory(productName),
            descriptions = listOf("Standard commercial product"),
            sources = listOf("Local database"),
            confidence = 0.7,
            lastUpdated = Clock.System.now()
        )
    }

    private fun simulateTaxSearch(country: String, taxType: String?): TaxRegulationSearchResult {
        return TaxRegulationSearchResult(
            country = country,
            taxType = taxType ?: "General",
            regulations = listOf(
                TaxRegulation(
                    name = "Internal Tax Regime Law",
                    description = "General tax regulations",
                    effectiveDate = "2024-01-01",
                    authority = "SRI"
                )
            ),
            lastUpdated = Clock.System.now()
        )
    }

    private fun simulateCompanySearch(companyName: String, country: String): CompanySearchResult {
        return CompanySearchResult(
            companyName = companyName,
            country = country,
            status = "ACTIVA",
            taxId = "1234567890001",
            registrationDate = "2020-01-01",
            sources = listOf("Commercial Registry"),
            confidence = 0.8
        )
    }

    private fun simulateTariffSearch(productDescription: String, countries: List<String>): TariffSearchResult {
        return TariffSearchResult(
            productDescription = productDescription,
            suggestions = countries.map { country ->
                TariffSuggestion(
                    country = country,
                    code = "9999.99.99",
                    description = "Other products",
                    confidence = 0.6
                )
            },
            sources = listOf("Harmonized System"),
            lastUpdated = Clock.System.now()
        )
    }

    private fun simulateBusinessValidation(data: BusinessDataValidationRequest): BusinessDataValidationResult {
        return BusinessDataValidationResult(
            isValid = true,
            confidence = 0.9,
            sources = listOf("Internal validation"),
            warnings = emptyList()
        )
    }

    private fun inferCategory(productName: String): String {
        return when {
            productName.contains("computer", ignoreCase = true) -> "Electronics"
            productName.contains("furniture", ignoreCase = true) -> "Furniture"
            productName.contains("food", ignoreCase = true) -> "Food"
            else -> "Others"
        }
    }

    private fun inferTaxCategory(productName: String): String {
        return when (inferCategory(productName)) {
            "Food" -> "IVA_0"
            "Electronics" -> "IVA_12"
            else -> "IVA_12"
        }
    }
}
