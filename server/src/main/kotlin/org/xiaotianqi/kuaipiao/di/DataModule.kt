package org.xiaotianqi.kuaipiao.di

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.koin.core.annotation.Module
import org.xiaotianqi.kuaipiao.core.logic.ObjectMapper
import org.xiaotianqi.kuaipiao.core.logic.PasswordEncoder
import org.xiaotianqi.kuaipiao.data.daos.user.UserDao
import org.xiaotianqi.kuaipiao.data.daos.auth.UserSessionDao
import org.xiaotianqi.kuaipiao.data.daos.auth.EmailVerificationDao
import org.xiaotianqi.kuaipiao.data.daos.auth.PasswordResetDao
import org.xiaotianqi.kuaipiao.data.daos.enterprise.EnterpriseDao
import org.xiaotianqi.kuaipiao.data.daos.organization.OrganizationDao
import org.xiaotianqi.kuaipiao.data.daos.company.CompanyDao
import org.xiaotianqi.kuaipiao.data.daos.rbac.RoleDao
import org.xiaotianqi.kuaipiao.data.daos.rbac.PermissionDao
import org.xiaotianqi.kuaipiao.data.daos.rbac.UserRoleDao
import org.xiaotianqi.kuaipiao.data.sources.db.dbi.user.UserDBI
import org.xiaotianqi.kuaipiao.data.sources.db.dbi.user.EmailVerificationDBI
import org.xiaotianqi.kuaipiao.data.sources.db.dbi.user.PasswordResetDBI
import org.xiaotianqi.kuaipiao.data.sources.db.dbi.user.impl.UserDBIImpl
import org.xiaotianqi.kuaipiao.data.sources.db.dbi.user.impl.EmailVerificationDBIImpl
import org.xiaotianqi.kuaipiao.data.sources.db.dbi.user.impl.PasswordResetDBIImpl
import org.xiaotianqi.kuaipiao.data.sources.db.dbi.enterprise.EnterpriseDBI
import org.xiaotianqi.kuaipiao.data.sources.db.dbi.enterprise.impl.EnterpriseDBIImpl
import org.xiaotianqi.kuaipiao.data.sources.db.dbi.organization.OrganizationDBI
import org.xiaotianqi.kuaipiao.data.sources.db.dbi.organization.impl.OrganizationDBIImpl
import org.xiaotianqi.kuaipiao.data.sources.db.dbi.company.CompanyDBI
import org.xiaotianqi.kuaipiao.data.sources.db.dbi.company.impl.CompanyDBIImpl
import org.xiaotianqi.kuaipiao.data.sources.db.dbi.rbac.RoleDBI
import org.xiaotianqi.kuaipiao.data.sources.db.dbi.rbac.PermissionDBI
import org.xiaotianqi.kuaipiao.data.sources.db.dbi.rbac.UserRoleDBI
import org.xiaotianqi.kuaipiao.data.sources.db.dbi.rbac.impl.RoleDBIImpl
import org.xiaotianqi.kuaipiao.data.sources.db.dbi.rbac.impl.PermissionDBIImpl
import org.xiaotianqi.kuaipiao.data.sources.db.dbi.rbac.impl.UserRoleDBIImpl
import org.xiaotianqi.kuaipiao.data.sources.cache.cm.users.UserSessionCM
import org.xiaotianqi.kuaipiao.data.sources.cache.cm.users.impl.UserSessionCMImpl
import org.xiaotianqi.kuaipiao.core.logic.TokenGenerator
import org.xiaotianqi.kuaipiao.data.daos.ai.AiCacheDao
import org.xiaotianqi.kuaipiao.data.daos.ai.ComplianceRiskDao
import org.xiaotianqi.kuaipiao.data.daos.ai.DocumentProcessingDao
import org.xiaotianqi.kuaipiao.data.daos.ai.ModelResultDao
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class, ExperimentalStdlibApi::class)
val dataModuleDeclarations = module {
    // Logic components
    single { TokenGenerator() }
    single { PasswordEncoder() }
    single { ObjectMapper() }

    // DBI Implementations
    single<UserDBI> { UserDBIImpl() }
    single<EmailVerificationDBI> { EmailVerificationDBIImpl(get()) }
    single<PasswordResetDBI> { PasswordResetDBIImpl(get()) }
    single<EnterpriseDBI> { EnterpriseDBIImpl() }
    single<OrganizationDBI> { OrganizationDBIImpl() }
    single<CompanyDBI> { CompanyDBIImpl() }
    single<RoleDBI> { RoleDBIImpl() }
    single<PermissionDBI> { PermissionDBIImpl() }
    single<UserRoleDBI> { UserRoleDBIImpl() }

    // Cache Managers
    single<UserSessionCM> { UserSessionCMImpl(get(), get()) }

    // DAOs
    single { UserDao(get()) }
    single { UserSessionDao(get()) }
    single { EmailVerificationDao(get()) }
    single { PasswordResetDao(get()) }
    single { EnterpriseDao(get()) }
    single { OrganizationDao(get()) }
    single { CompanyDao(get()) }
    single { RoleDao(get()) }
    single { PermissionDao(get()) }
    single { UserRoleDao(get()) }

    // AI DAOs
    single { AiCacheDao() }
    single { ComplianceRiskDao() }
    single { DocumentProcessingDao() }
    single { ModelResultDao() }
}

@Module(includes = [LogicModule::class, ClientModule::class])
class DataModule