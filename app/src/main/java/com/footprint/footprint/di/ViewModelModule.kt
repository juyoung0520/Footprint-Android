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

    viewModel{
        HomeViewModel(get(), get(), get(), get())
    }

    viewModel{
        MyInfoViewModel(get(), get())
    }

    viewModel{
        RegisterViewModel(get())
    }

    viewModel{
        SplashViewModel(get())
    }

    viewModel{
        SignInViewModel(get())
    }

    viewModel{
        SettingViewModel(get())
    }

    viewModel{
        MainViewModel(get())
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
}