package com.footprint.footprint.di

import com.footprint.footprint.viewmodel.*
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        GoalViewModel(get(), get(), get())
    }

    viewModel {
        BadgeViewModel(get(), get())
    }

    viewModel {
        WalkViewModel(get(), get(), get(), get(), get())
    }

    viewModel {
        HomeViewModel(get(), get(), get(), get())
    }

    viewModel {
        MyInfoViewModel(get(), get())
    }

    viewModel {
        RegisterViewModel(get())
    }

    viewModel {
        SplashViewModel(get(), get())
    }

    viewModel {
        SignInViewModel(get())
    }

    viewModel {
        SettingViewModel(get(), get())
    }

    viewModel {
        MainViewModel(get(), get())
    }

    viewModel {
        MyPageViewModel(get(), get())
    }

    viewModel {
        CalendarViewModel(get(), get(), get())
    }

    viewModel {
        TagSearchViewModel(get())
    }

    viewModel {
        NoticeListViewModel(get())
    }

    viewModel {
        NoticeDetailViewModel(get())
    }

    viewModel{
        CourseViewModel(get(), get(), get())
    }

    viewModel{
        CourseDetailViewModel(get(), get(), get())
    }

    viewModel {
        CourseSetViewModel(get())
    }

    viewModel {
        CourseShareViewModel(get(), get(), get(), get())
    }

    viewModel {
        CourseSelectViewModel(get())
    }

    viewModel {
        CourseWalkViewModel(get())
    }

    viewModel {
        RecommendedCourseViewModel(get(), get(), get(), get())
    }
}