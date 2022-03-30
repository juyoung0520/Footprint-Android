package com.footprint.footprint.viewmodel

import com.footprint.footprint.data.datasource.remote.*
import com.footprint.footprint.data.repository.remote.*
import com.footprint.footprint.data.retrofit.*
import com.footprint.footprint.domain.repository.*
import com.footprint.footprint.domain.usecase.*
import com.footprint.footprint.utils.GlobalApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

val appModule = module {
    single<GoalService> { GlobalApplication.retrofit.create(GoalService::class.java) }
    single<GoalRemoteDataSource> { GoalRemoteDataSourceImpl(get()) }
    single<GoalRepository> { GoalRepositoryImpl(get()) }.bind(GoalRemoteDataSourceImpl::class)
    single<GetThisMonthGoalUseCase> { GetThisMonthGoalUseCase(get()) }.bind(GoalRepositoryImpl::class)

    single<WeatherService> { GlobalApplication.retrofit.create(WeatherService::class.java) }
    single<WeatherRemoteDataSource> { WeatherRemoteDataSourceImpl(get()) }
    single<WeatherRepository> { WeatherRepositoryImpl(get()) }.bind(WeatherRemoteDataSourceImpl::class)
    single<GetWeatherUseCase> { GetWeatherUseCase(get()) }.bind(WeatherRepositoryImpl::class)

    single<UserService> { GlobalApplication.retrofit.create(UserService::class.java) }
    single<UserRemoteDataSource> { UserRemoteDataSourceImpl(get()) }
    single<UserRepository> { UserRepositoryImpl(get()) }.bind(UserRemoteDataSourceImpl::class)
    single<RegisterUserUseCase> { RegisterUserUseCase(get()) }.bind(UserRepositoryImpl::class)
    single<UpdateUserUseCase> { UpdateUserUseCase(get()) }.bind(UserRepositoryImpl::class)
    single<GetSimpleUserUseCase> { GetSimpleUserUseCase(get()) }.bind(UserRepositoryImpl::class) //홈, 마이페이지 유저
    single<GetMyInfoUserUseCase> { GetMyInfoUserUseCase(get()) }.bind(UserRepositoryImpl::class) //내 정보 수정 유저

    single<AchieveService> { GlobalApplication.retrofit.create(AchieveService::class.java) }
    single<AchieveRemoteDataSource> { AchieveRemoteDataSourceImpl(get()) }
    single<AchieveRepository> { AchieveRepositoryImpl(get()) }.bind(AchieveRemoteDataSourceImpl::class)
    single<GetTodayUseCase> { GetTodayUseCase(get()) }.bind(AchieveRemoteDataSourceImpl::class)
    single<GetTmonthUseCase> { GetTmonthUseCase(get()) }.bind(AchieveRemoteDataSourceImpl::class)

    single<AuthService> { GlobalApplication.retrofit.create(AuthService::class.java) }
    single<AuthRemoteDataSource> { AuthRemoteDataSourceImpl(get()) }
    single<AuthRepository> { AuthRepositoryImpl(get()) }.bind(AuthRemoteDataSourceImpl::class)
    single<AutoLoginUseCase> { AutoLoginUseCase(get()) }.bind(AuthRemoteDataSourceImpl::class)
    single<LoginUseCase> { LoginUseCase(get()) }.bind(AuthRemoteDataSourceImpl::class)
    single<UnRegisterUseCase> { UnRegisterUseCase(get()) }.bind(AuthRemoteDataSourceImpl::class)

    viewModel {
        GoalViewModel(get())
    }

    viewModel{
        HomeViewModel(get(), get(), get(), get())
    }

    viewModel{
        MyInfoViewModel(get(), get())
    }

    viewModel{
        UserViewModel(get())
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
}