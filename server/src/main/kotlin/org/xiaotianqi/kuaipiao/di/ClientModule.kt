package org.xiaotianqi.kuaipiao.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module

@Module(includes = [LogicModule::class])
@ComponentScan("app.dotoo.core.clients")
class ClientModule
