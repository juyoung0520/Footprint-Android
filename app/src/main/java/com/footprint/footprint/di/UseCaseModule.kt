package com.footprint.footprint.di

import com.footprint.footprint.data.datasource.remote.AchieveRemoteDataSourceImpl
import com.footprint.footprint.data.datasource.remote.AuthRemoteDataSourceImpl
import com.footprint.footprint.data.datasource.remote.BadgeRemoteDataSourceImpl
import com.footprint.footprint.data.repository.remote.*
import com.footprint.footprint.domain.usecase.*
import org.koin.dsl.bind
import org.koin.dsl.module

val useCaseModule = module {
    single<UpdateUserUseCase> { UpdateUserUseCase(get()) }.bind(UserRepositoryImpl::class)
    single<GetThisMonthGoalUseCase> { GetThisMonthGoalUseCase(get()) }.bind(GoalRepositoryImpl::class)
    single<GetNextMonthGoalUseCase> { GetNextMonthGoalUseCase(get()) }.bind(GoalRepositoryImpl::class)
    single<UpdateGoalUseCase> { UpdateGoalUseCase(get()) }.bind(GoalRepositoryImpl::class)
    single<GetBadgesUseCase> { GetBadgesUseCase(get()) }.bind(BadgeRepositoryImpl::class)
    single<ChangeRepresentativeBadgeUseCase> { ChangeRepresentativeBadgeUseCase(get()) }.bind(BadgeRepositoryImpl::class)
    single<GetWalkByIdxUseCase> { GetWalkByIdxUseCase(get()) }.bind(WalkRepositoryImpl::class)
    single<GetFootprintsByWalkIdxUseCase> { GetFootprintsByWalkIdxUseCase(get()) }.bind(FootprintRepositoryImpl::class)
    single<UpdateFootprintUseCase> { UpdateFootprintUseCase(get()) }.bind(FootprintRepositoryImpl::class)
    single<DeleteWalkUseCase> { DeleteWalkUseCase(get()) }.bind(WalkRepositoryImpl::class)
    single<WriteWalkUseCase> { WriteWalkUseCase(get()) }.bind(WalkRepositoryImpl::class)


    single<GetWeatherUseCase> { GetWeatherUseCase(get()) }.bind(WeatherRepositoryImpl::class)
    single<RegisterUserUseCase> { RegisterUserUseCase(get()) }.bind(UserRepositoryImpl::class)
    single<GetSimpleUserUseCase> { GetSimpleUserUseCase(get()) }.bind(UserRepositoryImpl::class) //홈, 마이페이지 유저
    single<GetMyInfoUserUseCase> { GetMyInfoUserUseCase(get()) }.bind(UserRepositoryImpl::class) //내 정보 수정 유저
    single<GetTodayUseCase> { GetTodayUseCase(get()) }.bind(AchieveRemoteDataSourceImpl::class)
    single<GetTmonthUseCase> { GetTmonthUseCase(get()) }.bind(AchieveRemoteDataSourceImpl::class)
    single<AutoLoginUseCase> { AutoLoginUseCase(get()) }.bind(AuthRemoteDataSourceImpl::class)
    single<LoginUseCase> { LoginUseCase(get()) }.bind(AuthRemoteDataSourceImpl::class)
    single<UnRegisterUseCase> { UnRegisterUseCase(get()) }.bind(AuthRemoteDataSourceImpl::class)
    single<GetMonthBadgeUseCase> { GetMonthBadgeUseCase(get()) }.bind(BadgeRemoteDataSourceImpl::class)

    single<GetInfoDetailUseCase> { GetInfoDetailUseCase(get()) }.bind(AchieveRepositoryImpl::class)
    single<GetMonthWalksUseCase> { GetMonthWalksUseCase(get()) }.bind(WalkRepositoryImpl::class)
    single<GetDayWalksUseCase> { GetDayWalksUseCase(get()) }.bind(WalkRepositoryImpl::class)

}