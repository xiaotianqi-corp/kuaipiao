package org.xiaotianqi.kuaipiao.validation

object RegexPatterns {
    val emailPattern = """\w+@\w+\.\w+""".toRegex()
    val passwordPatterns = "(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$".toRegex()
}