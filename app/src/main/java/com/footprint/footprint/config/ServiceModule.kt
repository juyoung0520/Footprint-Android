package com.footprint.footprint.config

import com.footprint.footprint.data.retrofit.BadgeService
import com.footprint.footprint.data.retrofit.GoalService
import com.footprint.footprint.data.retrofit.UserService
import com.footprint.footprint.utils.GlobalApplication
import org.koin.dsl.module

val serviceModule = module {
    single<UserService> { GlobalApplication.retrofit.create(UserService::class.java) }
    single<GoalService> { GlobalApplication.retrofit.create(GoalService::class.java) }
    single<BadgeService> { GlobalApplication.retrofit.create(BadgeService::class.java) }
}