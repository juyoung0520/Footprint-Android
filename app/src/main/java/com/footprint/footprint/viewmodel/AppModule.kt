package com.footprint.footprint.viewmodel

import com.footprint.footprint.data.datasource.remote.*
import com.footprint.footprint.data.repository.remote.BadgeRepositoryImpl
import com.footprint.footprint.data.repository.remote.GoalRepositoryImpl
import com.footprint.footprint.data.repository.remote.UserRepositoryImpl
import com.footprint.footprint.data.retrofit.BadgeService
import com.footprint.footprint.domain.repository.GoalRepository
import com.footprint.footprint.data.retrofit.GoalService
import com.footprint.footprint.data.retrofit.UserService
import com.footprint.footprint.domain.repository.BadgeRepository
import com.footprint.footprint.domain.repository.UserRepository
import com.footprint.footprint.domain.usecase.*
import com.footprint.footprint.utils.GlobalApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

val appModule = module {
    single<UserService> { GlobalApplication.retrofit.create(UserService::class.java) }
    single<UserRemoteDataSource> { UserRemoteDataSourceImpl(get()) }
    single<UserRepository> { UserRepositoryImpl(get()) }.bind(UserRemoteDataSourceImpl::class)
    single<UpdateUserUseCase> { UpdateUserUseCase(get()) }.bind(UserRepositoryImpl::class)

    single<GoalService> { GlobalApplication.retrofit.create(GoalService::class.java) }
    single<GoalRemoteDataSource> { GoalRemoteDataSourceImpl(get()) }
    single<GoalRepository> { GoalRepositoryImpl(get()) }.bind(GoalRemoteDataSourceImpl::class)
    single<GetThisMonthGoalUseCase> { GetThisMonthGoalUseCase(get()) }.bind(GoalRepositoryImpl::class)
    single<GetNextMonthGoalUseCase> { GetNextMonthGoalUseCase(get()) }.bind(GoalRepositoryImpl::class)
    single<UpdateGoalUseCase> { UpdateGoalUseCase(get()) }.bind(GoalRepositoryImpl::class)

    single<BadgeService> { GlobalApplication.retrofit.create(BadgeService::class.java) }
    single<BadgeRemoteDataSource> { BadgeRemoteDataSourceImpl(get()) }
    single<BadgeRepository> { BadgeRepositoryImpl(get()) }.bind(BadgeRemoteDataSourceImpl::class)
    single<GetBadgesUseCase> { GetBadgesUseCase(get()) }.bind(BadgeRepositoryImpl::class)
    single<ChangeRepresentativeBadgeUseCase> { ChangeRepresentativeBadgeUseCase(get()) }.bind(BadgeRepositoryImpl::class)

    viewModel {
        UserViewModel(get())
    }

    viewModel {
        GoalViewModel(get(), get(), get())
    }

    viewModel {
        BadgeViewModel(get(), get())
    }
}