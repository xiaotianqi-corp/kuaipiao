package org.xiaotianqi.kuaipiao.config

import org.xiaotianqi.kuaipiao.config.core.Configuration
import org.xiaotianqi.kuaipiao.config.core.ConfigurationProperty

@Configuration("postgres")
object PostgresConfig {

    @ConfigurationProperty("url")
    var url: String = "jdbc:postgresql://127.0.0.1:5432/kuaipiao"

    @ConfigurationProperty("user")
    var user: String = "postgres"

    @ConfigurationProperty("password")
    var password: String = "nimda"
}
