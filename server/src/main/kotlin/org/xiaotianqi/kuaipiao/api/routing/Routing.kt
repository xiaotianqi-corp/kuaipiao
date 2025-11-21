package org.xiaotianqi.kuaipiao.api.routing

import org.xiaotianqi.kuaipiao.core.logic.typedId.serialization.IdKotlinXSerializationModule
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.routing.*
import org.xiaotianqi.kuaipiao.api.routing.v1.auth.authRoutesV1
import org.xiaotianqi.kuaipiao.api.routing.v1.configureV1Routes
import org.xiaotianqi.kuaipiao.api.routing.v1.organization.organizationRoutesV1
import kotlin.time.ExperimentalTime

@ExperimentalTime
fun Application.configureRouting() {
    install(Resources) {
        serializersModule = IdKotlinXSerializationModule
    }

    routing {
        configureV1Routes()
    }
}
