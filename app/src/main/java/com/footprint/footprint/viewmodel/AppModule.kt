package com.footprint.footprint.viewmodel

import com.footprint.footprint.data.datasource.remote.*
import com.footprint.footprint.data.repository.remote.AchieveRepositoryImpl
import com.footprint.footprint.data.repository.remote.GoalRepositoryImpl
import com.footprint.footprint.data.repository.remote.WalkRepositoryImpl
import com.footprint.footprint.data.retrofit.AchieveService
import com.footprint.footprint.data.retrofit.WalkService
import com.footprint.footprint.domain.repository.AchieveRepository
import com.footprint.footprint.domain.repository.GoalRepository
import com.footprint.footprint.domain.repository.WalkRepository
import com.footprint.footprint.domain.usecase.GetDayWalksUseCase
import com.footprint.footprint.domain.usecase.GetInfoDetailUseCase
import com.footprint.footprint.domain.usecase.GetMonthWalksUseCase
import com.footprint.footprint.domain.usecase.GetThisMonthGoalUseCase
import com.footprint.footprint.retrofit.GoalService
import com.footprint.footprint.utils.GlobalApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

val appModule = module {
    single<GoalService> { GlobalApplication.retrofit.create(GoalService::class.java) }
    single<GoalRemoteDataSource> { GoalRemoteDataSourceImpl(get()) }
    single<GoalRepository> { GoalRepositoryImpl(get()) }.bind(GoalRemoteDataSourceImpl::class)
    single<GetThisMonthGoalUseCase> { GetThisMonthGoalUseCase(get()) }.bind(GoalRepositoryImpl::class)

    single<AchieveService> { GlobalApplication.retrofit.create(AchieveService::class.java) }
    single<AchieveRemoteDataSource> { AchieveRemoteDataSourceImpl(get()) }
    single<AchieveRepository> { AchieveRepositoryImpl(get()) }.bind(AchieveRemoteDataSourceImpl::class)
    single<GetInfoDetailUseCase> { GetInfoDetailUseCase(get()) }.bind(AchieveRepositoryImpl::class)

    single<WalkService> { GlobalApplication.retrofit.create(WalkService::class.java) }
    single<WalkRemoteDataSource> { WalkRemoteDataSourceImpl(get()) }
    single<WalkRepository> { WalkRepositoryImpl(get()) }.bind(WalkRemoteDataSourceImpl::class)
    single<GetMonthWalksUseCase> { GetMonthWalksUseCase(get()) }.bind(WalkRepositoryImpl::class)
    single<GetDayWalksUseCase> { GetDayWalksUseCase(get()) }.bind(WalkRepositoryImpl::class)

    viewModel {
        GoalViewModel(get())
    }

    viewModel {
        AchieveViewModel(get())
    }

    viewModel {
        CalendarViewModel(get(), get())
    }
}