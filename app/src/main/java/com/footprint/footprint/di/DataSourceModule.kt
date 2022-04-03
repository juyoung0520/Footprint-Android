package com.footprint.footprint.di

import com.footprint.footprint.data.datasource.remote.*
import org.koin.dsl.module

val dataSourceModule = module {
    single<UserRemoteDataSource> { UserRemoteDataSourceImpl(get()) }
    single<GoalRemoteDataSource> { GoalRemoteDataSourceImpl(get()) }
    single<BadgeRemoteDataSource> { BadgeRemoteDataSourceImpl(get()) }
    single<WalkRemoteDataSource> { WalkRemoteDataSourceImpl(get()) }
    single<FootprintRemoteDataSource> { FootprintRemoteDataSourceImpl(get()) }
}