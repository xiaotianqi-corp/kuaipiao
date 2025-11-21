package org.xiaotianqi.kuaipiao.core.ports

import org.xiaotianqi.kuaipiao.domain.classification.ClassificationResult
import org.xiaotianqi.kuaipiao.domain.classification.ClassificationInput
import kotlin.time.ExperimentalTime

@ExperimentalTime
interface ProductClassificationService {
    suspend fun classify(input: ClassificationInput): ClassificationResult
}
