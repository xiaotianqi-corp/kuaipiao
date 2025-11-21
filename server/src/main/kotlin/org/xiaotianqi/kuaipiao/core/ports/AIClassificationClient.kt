package org.xiaotianqi.kuaipiao.core.ports

import org.xiaotianqi.kuaipiao.domain.product.ProductInfo
import org.xiaotianqi.kuaipiao.domain.product.ProductClassificationData

interface AiClassificationClient {
    suspend fun classify(product: ProductInfo, countryOrigin: String, countryDestination: String): ProductClassificationData
}