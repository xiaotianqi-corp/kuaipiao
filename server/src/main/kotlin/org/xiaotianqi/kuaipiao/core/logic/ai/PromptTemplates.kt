package org.xiaotianqi.kuaipiao.core.logic.ai

object PromptTemplates {

    // 🎯 NICHO #1: PYMES Exportadoras
    fun tariffClassificationPrompt(
        productDescription: String,
        originCountry: String,
        destinationCountry: String,
        tradeContext: String
    ): String {
        return """
            Eres un especialista en comercio internacional y clasificación arancelaria.
            
            PRODUCTO: $productDescription
            PAÍS ORIGEN: $originCountry
            PAÍS DESTINO: $destinationCountry
            CONTEXTO: $tradeContext
            
            Clasifica la partida arancelaria según el Sistema Armonizado y proporciona:
            
            1. Código arancelario de 6-8 dígitos
            2. Descripción oficial de la partida
            3. Impuestos de importación aplicables
            4. Documentación requerida
            5. Restricciones o licencias necesarias
            6. Tratados comerciales aplicables
            
            Responde SOLO en formato JSON:
            {
                "tariffCode": "string",
                "description": "string", 
                "importTaxRate": number,
                "requiredDocuments": ["string"],
                "restrictions": ["string"],
                "tradeAgreements": ["string"],
                "confidence": number
            }
        """.trimIndent()
    }

    // 🎯 NICHO #2: Reconciliación Contable
    fun accountingReconciliationPrompt(
        documentData: String,
        companyChartOfAccounts: String,
        historicalPatterns: String
    ): String {
        return """
            Eres un contador especializado en reconciliación automática.
            
            DATOS DEL DOCUMENTO:
            $documentData
            
            PLAN DE CUENTAS DE LA EMPRESA:
            $companyChartOfAccounts
            
            PATRONES HISTÓRICOS:
            $historicalPatterns
            
            Asigna automáticamente las cuentas contables y genera los asientos correspondientes.
            Considera:
            - Naturaleza del gasto/ingreso
            - Centro de costos apropiado
            - Proyecto asociado
            - Impuestos aplicables
            - Retenciones
            
            Responde en JSON:
            {
                "suggestedAccounts": [
                    {
                        "accountCode": "string",
                        "accountName": "string", 
                        "amount": number,
                        "description": "string",
                        "confidence": number
                    }
                ],
                "taxImplications": [
                    {
                        "taxType": "string",
                        "rate": number,
                        "amount": number
                    }
                ],
                "validationWarnings": ["string"],
                "automationLevel": "FULL_AUTOMATION|SEMI_AUTOMATIC|MANUAL"
            }
        """.trimIndent()
    }

    // 🎯 NICHO #3: Compliance Predictivo
    fun complianceRiskPrompt(
        transactionData: String,
        companyHistory: String,
        regulatoryContext: String
    ): String {
        return """
            Eres un auditor especializado en detección de riesgos de compliance.
            
            DATOS DE TRANSACCIÓN:
            $transactionData
            
            HISTORIAL DE LA EMPRESA:
            $companyHistory
            
            CONTEXTO REGULATORIO:
            $regulatoryContext
            
            Analiza y identifica:
            1. Patrones de riesgo (montes redondos, horarios inusuales, etc.)
            2. Probabilidad de auditoría
            3. Transacciones de alto riesgo
            4. Recomendaciones de mitigación
            
            Responde en JSON:
            {
                "riskScore": number,
                "auditProbability": number,
                "highRiskTransactions": [
                    {
                        "transactionId": "string",
                        "riskFactors": ["string"],
                        "suggestedAction": "string"
                    }
                ],
                "riskPatterns": [
                    {
                        "type": "string",
                        "severity": "HIGH|MEDIUM|LOW", 
                        "description": "string"
                    }
                ],
                "recommendations": ["string"]
            }
        """.trimIndent()
    }
}