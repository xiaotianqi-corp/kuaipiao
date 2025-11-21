package org.xiaotianqi.kuaipiao.domain.accounting

import kotlinx.serialization.Serializable

@Serializable
data class AccountingDimension(
    val costCenter: String? = null,
    val projectCode: String? = null,
    val department: String? = null,
    val custom: Map<String, String> = emptyMap()
) {
    companion object {
        fun fromMap(m: Map<String, String>?): AccountingDimension? {
            if (m == null || m.isEmpty()) return null
            return AccountingDimension(
                costCenter = m["costCenter"].takeIf { !it.isNullOrBlank() },
                projectCode = m["projectCode"].takeIf { !it.isNullOrBlank() },
                department = m["department"].takeIf { !it.isNullOrBlank() },
                custom = m.filterKeys { key -> key != "costCenter" && key != "projectCode" && key != "department" }
            )
        }
    }
}
