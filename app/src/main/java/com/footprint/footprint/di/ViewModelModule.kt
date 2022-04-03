package com.footprint.footprint.di

import com.footprint.footprint.viewmodel.BadgeViewModel
import com.footprint.footprint.viewmodel.GoalViewModel
import com.footprint.footprint.viewmodel.UserViewModel
import com.footprint.footprint.viewmodel.WalkViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        UserViewModel(get())
    }

    viewModel {
        GoalViewModel(get(), get(), get())
    }

    viewModel {
        BadgeViewModel(get(), get())
    }

    viewModel {
        WalkViewModel(get(), get(), get(), get(), get())
    }
}