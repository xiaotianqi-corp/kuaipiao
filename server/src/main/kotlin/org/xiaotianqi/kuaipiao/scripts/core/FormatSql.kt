package org.xiaotianqi.kuaipiao.scripts.core

fun formatSql(sql: String): String {
    val formatted = StringBuilder()
    val statements = sql.split(";").map { it.trim() }.filter { it.isNotEmpty() }

    for (statement in statements) {
        if (statement.startsWith("CREATE TABLE", ignoreCase = true)) {
            val parts = statement.split("(", limit = 2)
            formatted.append(parts[0].trim()).append(" (\n")

            if (parts.size > 1) {
                val columns = parts[1]
                    .removeSuffix(")")
                    .split(",")
                    .map { it.trim() }

                columns.forEachIndexed { i, col ->
                    formatted.append("    ").append(col)
                    if (i < columns.lastIndex) formatted.append(",")
                    formatted.append("\n")
                }
                formatted.append(");\n\n")
            }
        } else {
            formatted.append(statement.trim()).append(";\n\n")
        }
    }

    return formatted.toString().trimEnd() + "\n"
}
