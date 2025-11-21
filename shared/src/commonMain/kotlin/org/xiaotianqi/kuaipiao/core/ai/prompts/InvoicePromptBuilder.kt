package org.xiaotianqi.kuaipiao.core.ai.prompts

object InvoicePromptBuilder {

    fun build(country: String, language: String = "en"): String {
        return when (language.lowercase()) {
            "es" -> buildSpanishPrompt(country)
            else -> buildEnglishPrompt(country)
        }
    }

    private fun buildEnglishPrompt(country: String) = """
        You are an invoice extraction AI specialized in $country.
        Extract structured invoice data in STRICT JSON format following this schema:

        {
          "id": "string",
          "organizationid": {
              "id": "string",
              "name": "string",
              "country": "string",
              "region": "string",
              "city": "string",
              "address": "string"
          },
          "customerId": "string",
          "number": "string",
          "date": "YYYY-MM-DD",
          "dueDate": "YYYY-MM-DD",
          "items": [
              {
                  "id": "string",
                  "code": "string",
                  "description": "string",
                  "quantity": number,
                  "unitPrice": number,
                  "discount": number,
                  "taxRate": number,
                  "taxAmount": number,
                  "subtotal": number,
                  "total": number
              }
          ],
          "subtotal": number,
          "tax": number,
          "total": number,
          "currency": "string",
          "status": "ISSUED",
          "paymentStatus": "UNPAID",
          "notes": "string"
        }

        Output ONLY valid JSON. Do not explain anything else.
    """.trimIndent()

    private fun buildSpanishPrompt(country: String) = """
        Eres una IA especializada en extraer información de facturas en el país: $country.
        Extrae los datos en formato JSON ESTRICTO con esta estructura:

        {
          "id": "string",
          "organizationid": {
              "id": "string",
              "name": "string",
              "country": "string",
              "region": "string",
              "city": "string",
              "address": "string"
          },
          "customerId": "string",
          "number": "string",
          "date": "YYYY-MM-DD",
          "dueDate": "YYYY-MM-DD",
          "items": [
              {
                  "id": "string",
                  "code": "string",
                  "description": "string",
                  "quantity": number,
                  "unitPrice": number,
                  "discount": number,
                  "taxRate": number,
                  "taxAmount": number,
                  "subtotal": number,
                  "total": number
              }
          ],
          "subtotal": number,
          "tax": number,
          "total": number,
          "currency": "string",
          "status": "ISSUED",
          "paymentStatus": "UNPAID",
          "notes": "string"
        }

        Devuelve SOLO un JSON válido. No des explicaciones.
    """.trimIndent()
}
