package com.footprint.footprint.di

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
    single<ChangeRepresentativeBadgeUseCase> { ChangeRepresentativeBadgeUseCase(get()) }.bind(
        BadgeRepositoryImpl::class
    )
    single<GetWalkByIdxUseCase> { GetWalkByIdxUseCase(get()) }.bind(WalkRepositoryImpl::class)
    single<GetFootprintsByWalkIdxUseCase> { GetFootprintsByWalkIdxUseCase(get()) }.bind(
        FootprintRepositoryImpl::class
    )
    single<UpdateFootprintUseCase> { UpdateFootprintUseCase(get()) }.bind(FootprintRepositoryImpl::class)
    single<DeleteWalkUseCase> { DeleteWalkUseCase(get()) }.bind(WalkRepositoryImpl::class)
    single<SaveWalkUseCase> { SaveWalkUseCase(get()) }.bind(WalkRepositoryImpl::class)

    single<GetWeatherUseCase> { GetWeatherUseCase(get()) }.bind(WeatherRepositoryImpl::class)
    single<RegisterUserUseCase> { RegisterUserUseCase(get()) }.bind(UserRepositoryImpl::class)
    single<GetSimpleUserUseCase> { GetSimpleUserUseCase(get()) }.bind(UserRepositoryImpl::class) //홈, 마이페이지 유저
    single<GetMyInfoUserUseCase> { GetMyInfoUserUseCase(get()) }.bind(UserRepositoryImpl::class) //내 정보 수정 유저
    single<GetTodayUseCase> { GetTodayUseCase(get()) }.bind(AchieveRepositoryImpl::class)
    single<GetTmonthUseCase> { GetTmonthUseCase(get()) }.bind(AchieveRepositoryImpl::class)
    single<AutoLoginUseCase> { AutoLoginUseCase(get()) }.bind(AuthRepositoryImpl::class)
    single<LoginUseCase> { LoginUseCase(get()) }.bind(AuthRepositoryImpl::class)
    single<UnRegisterUseCase> { UnRegisterUseCase(get()) }.bind(AuthRepositoryImpl::class)
    single<GetMonthBadgeUseCase> { GetMonthBadgeUseCase(get()) }.bind(BadgeRepositoryImpl::class)

    single<GetUserInfoUseCase> { GetUserInfoUseCase(get()) }.bind(AchieveRepositoryImpl::class)
    single<GetMonthWalksUseCase> { GetMonthWalksUseCase(get()) }.bind(WalkRepositoryImpl::class)
    single<GetDayWalksUseCase> { GetDayWalksUseCase(get()) }.bind(WalkRepositoryImpl::class)
    single<GetTagWalksUseCase> { GetTagWalksUseCase(get()) }.bind(WalkRepositoryImpl::class)

    single<GetNoticeListUseCase> { GetNoticeListUseCase(get()) }.bind(NoticeRepositoryImpl::class)
    single<GetNoticeUseCase> { GetNoticeUseCase(get()) }.bind(NoticeRepositoryImpl::class)
    single<GetNewNoticeUseCase> { GetNewNoticeUseCase(get()) }.bind(NoticeRepositoryImpl::class)
    single<GetKeyNoticeUseCase> { GetKeyNoticeUseCase(get()) }.bind(NoticeRepositoryImpl::class)
    single<GetVersionUseCase> { GetVersionUseCase(get()) }.bind(NoticeRepositoryImpl::class)

    single<GetAddressUseCase> { GetAddressUseCase((get())) }.bind(MapRepositoryImpl::class)
    single<GetWalkDetailCUseCase> {GetWalkDetailCUseCase(get())}.bind(CourseRepositoryImpl::class)
    single<SaveCourseUseCase> {SaveCourseUseCase((get()))}.bind(CourseRepositoryImpl::class)
    single<GetSelfCourseListUseCase> {GetSelfCourseListUseCase((get()))}.bind(CourseRepositoryImpl::class)

    single<GetCoursesUseCase> { GetCoursesUseCase(get()) }.bind(CourseRepositoryImpl::class)
    single<MarkCourseUseCase> { MarkCourseUseCase(get()) }.bind(CourseRepositoryImpl::class)
    single<GetCourseInfoUseCase> { GetCourseInfoUseCase(get()) }.bind(CourseRepositoryImpl::class)

    single<EvaluateCourseUseCase> { EvaluateCourseUseCase(get()) }.bind(CourseRepositoryImpl::class)
    single<GetMarkedCoursesUseCase> { GetMarkedCoursesUseCase(get()) }.bind(CourseRepositoryImpl::class)
    single<GetMyRecommendedCoursesUseCase> { GetMyRecommendedCoursesUseCase(get()) }.bind(CourseRepositoryImpl::class)
}