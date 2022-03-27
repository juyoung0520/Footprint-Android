package com.footprint.footprint.viewmodel

import com.footprint.footprint.data.datasource.remote.AchieveRemoteDataSource
import com.footprint.footprint.data.datasource.remote.AchieveRemoteDataSourceImpl
import com.footprint.footprint.data.datasource.remote.GoalRemoteDataSource
import com.footprint.footprint.data.datasource.remote.GoalRemoteDataSourceImpl
import com.footprint.footprint.data.repository.remote.AchieveRepositoryImpl
import com.footprint.footprint.data.repository.remote.GoalRepositoryImpl
import com.footprint.footprint.data.retrofit.AchieveService
import com.footprint.footprint.domain.repository.AchieveRepository
import com.footprint.footprint.domain.repository.GoalRepository
import com.footprint.footprint.domain.usecase.GetInfoDetailUseCase
import com.footprint.footprint.domain.usecase.GetThisMonthGoalUseCase
import com.footprint.footprint.retrofit.GoalService
import com.footprint.footprint.utils.GlobalApplication
import com.footprint.footprint.utils.RemoteErrorEmitter
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

    viewModel {
        GoalViewModel(get())
        AchieveViewModel(get())
    }
}