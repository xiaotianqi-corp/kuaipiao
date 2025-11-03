package org.xiaotianqi.kuaipiao.config

import org.xiaotianqi.kuaipiao.config.core.Configuration
import org.xiaotianqi.kuaipiao.config.core.ConfigurationProperty

@Configuration("postgres")
object PostgresConfig {
    @ConfigurationProperty("url")
    var url: String = "jdbc:postgresql://localhost:5432/dotoodevdb"

    @ConfigurationProperty("user")
    var user: String = "DoTooDevUser"

    @ConfigurationProperty("password")
    var password: String = "DoTooDevPassword"
}
