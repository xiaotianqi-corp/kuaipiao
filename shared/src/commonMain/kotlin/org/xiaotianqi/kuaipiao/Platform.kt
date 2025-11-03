package org.xiaotianqi.kuaipiao

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform