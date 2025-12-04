package org.xiaotianqi.kuaipiao.api.routing.v1

import io.ktor.server.routing.*
import org.xiaotianqi.kuaipiao.api.routing.v1.auth.adminAuthRoutesV1
import org.xiaotianqi.kuaipiao.api.routing.v1.auth.authRoutesV1
import org.xiaotianqi.kuaipiao.api.routing.v1.company.companyRoutesV1
import org.xiaotianqi.kuaipiao.api.routing.v1.email.emailRoutesV1
import org.xiaotianqi.kuaipiao.api.routing.v1.enterprise.enterpriseRoutesV1
import org.xiaotianqi.kuaipiao.api.routing.v1.organization.organizationRoutesV1
import kotlin.time.ExperimentalTime

@ExperimentalTime
fun Route.configureV1Routes() {
    route("/api/v1") {
        authRoutesV1()
        adminAuthRoutesV1()
        enterpriseRoutesV1()
        emailRoutesV1()
        organizationRoutesV1()
        companyRoutesV1()
    }
}