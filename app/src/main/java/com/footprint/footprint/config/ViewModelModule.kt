package com.footprint.footprint.config

import com.footprint.footprint.viewmodel.BadgeViewModel
import com.footprint.footprint.viewmodel.GoalViewModel
import com.footprint.footprint.viewmodel.UserViewModel
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
}