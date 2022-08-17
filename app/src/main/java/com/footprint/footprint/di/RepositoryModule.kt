package com.footprint.footprint.di

import com.footprint.footprint.data.datasource.remote.*
import com.footprint.footprint.data.repository.remote.*
import com.footprint.footprint.domain.repository.*
import org.koin.dsl.bind
import org.koin.dsl.module

val repositoryModule = module {
    single<AuthRepository> { AuthRepositoryImpl(get()) }.bind(AuthRemoteDataSourceImpl::class)
    single<UserRepository> { UserRepositoryImpl(get()) }.bind(UserRemoteDataSourceImpl::class)
    single<AchieveRepository> { AchieveRepositoryImpl(get()) }.bind(AchieveRemoteDataSourceImpl::class)
    single<GoalRepository> { GoalRepositoryImpl(get()) }.bind(GoalRemoteDataSourceImpl::class)
    single<BadgeRepository> { BadgeRepositoryImpl(get()) }.bind(BadgeRemoteDataSourceImpl::class)
    single<WalkRepository> { WalkRepositoryImpl(get()) }.bind(WalkRepositoryImpl::class)
    single<FootprintRepository> { FootprintRepositoryImpl(get()) }.bind(FootprintRepositoryImpl::class)
    single<WeatherRepository> { WeatherRepositoryImpl(get()) }.bind(WeatherRemoteDataSourceImpl::class)
    single<NoticeRepository> { NoticeRepositoryImpl(get()) }.bind(NoticeRepositoryImpl::class)
}