package org.xiaotianqi.kuaipiao.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module

@Module(includes = [LogicModule::class, ClientModule::class])
@ComponentScan("org.xiaotianqi.kuaipiao.data")
class DataModule
