package com.footprint.footprint.config

import com.footprint.footprint.data.repository.remote.BadgeRepositoryImpl
import com.footprint.footprint.data.repository.remote.GoalRepositoryImpl
import com.footprint.footprint.data.repository.remote.UserRepositoryImpl
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
}