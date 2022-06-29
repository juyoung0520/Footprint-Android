package com.footprint.footprint.di

import com.footprint.footprint.data.datasource.remote.*
import org.koin.dsl.module

val dataSourceModule = module {
    single<AuthRemoteDataSource> { AuthRemoteDataSourceImpl(get()) }
    single<UserRemoteDataSource> { UserRemoteDataSourceImpl(get()) }
    single<AchieveRemoteDataSource> { AchieveRemoteDataSourceImpl(get()) }
    single<GoalRemoteDataSource> { GoalRemoteDataSourceImpl(get()) }
    single<BadgeRemoteDataSource> { BadgeRemoteDataSourceImpl(get()) }
    single<WalkRemoteDataSource> { WalkRemoteDataSourceImpl(get()) }
    single<FootprintRemoteDataSource> { FootprintRemoteDataSourceImpl(get()) }
    single<WeatherRemoteDataSource> { WeatherRemoteDataSourceImpl(get()) }
    single<NoticeDataSource> { NoticeDataSourceImpl(get())}
}