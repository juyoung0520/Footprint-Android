package com.footprint.footprint.config

import com.footprint.footprint.data.datasource.remote.*
import org.koin.dsl.module

val dataSourceModule = module {
    single<UserRemoteDataSource> { UserRemoteDataSourceImpl(get()) }
    single<GoalRemoteDataSource> { GoalRemoteDataSourceImpl(get()) }
    single<BadgeRemoteDataSource> { BadgeRemoteDataSourceImpl(get()) }
}