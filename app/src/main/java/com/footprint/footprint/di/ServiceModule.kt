package com.footprint.footprint.di

import com.footprint.footprint.data.retrofit.*
import com.footprint.footprint.utils.GlobalApplication
import org.koin.dsl.module

val serviceModule = module {
    single<AuthService> { GlobalApplication.retrofit.create(AuthService::class.java) }
    single<UserService> { GlobalApplication.retrofit.create(UserService::class.java) }
    single<AchieveService> { GlobalApplication.retrofit.create(AchieveService::class.java) }
    single<GoalService> { GlobalApplication.retrofit.create(GoalService::class.java) }
    single<BadgeService> { GlobalApplication.retrofit.create(BadgeService::class.java) }
    single<WalkService> { GlobalApplication.retrofit.create(WalkService::class.java) }
    single<FootprintService> { GlobalApplication.retrofit.create(FootprintService::class.java) }
    single<WeatherService> { GlobalApplication.retrofit.create(WeatherService::class.java) }
    single<NoticeService> { GlobalApplication.retrofit.create(NoticeService::class.java) }
}