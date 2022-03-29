package com.footprint.footprint.config

import com.footprint.footprint.data.datasource.remote.BadgeRemoteDataSourceImpl
import com.footprint.footprint.data.datasource.remote.GoalRemoteDataSourceImpl
import com.footprint.footprint.data.datasource.remote.UserRemoteDataSourceImpl
import com.footprint.footprint.data.repository.remote.BadgeRepositoryImpl
import com.footprint.footprint.data.repository.remote.GoalRepositoryImpl
import com.footprint.footprint.data.repository.remote.UserRepositoryImpl
import com.footprint.footprint.domain.repository.BadgeRepository
import com.footprint.footprint.domain.repository.GoalRepository
import com.footprint.footprint.domain.repository.UserRepository
import org.koin.dsl.bind
import org.koin.dsl.module

val repositoryModule = module {
    single<UserRepository> { UserRepositoryImpl(get()) }.bind(UserRemoteDataSourceImpl::class)
    single<GoalRepository> { GoalRepositoryImpl(get()) }.bind(GoalRemoteDataSourceImpl::class)
    single<BadgeRepository> { BadgeRepositoryImpl(get()) }.bind(BadgeRemoteDataSourceImpl::class)
}