package org.xiaotianqi.kuaipiao.api.routing

import org.xiaotianqi.kuaipiao.api.routing.auth.authRoutes
import org.xiaotianqi.kuaipiao.api.routing.user.userRoutes
import org.xiaotianqi.kuaipiao.core.logic.typedId.serialization.IdKotlinXSerializationModule
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    install(Resources) {
        serializersModule = IdKotlinXSerializationModule
    }

    routing {
        authRoutes()
        userRoutes()
    }
}
