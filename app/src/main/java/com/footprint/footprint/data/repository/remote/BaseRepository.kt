package com.footprint.footprint.data.repository.remote

import com.footprint.footprint.data.model.Result
import com.footprint.footprint.utils.LogUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

abstract class BaseRepository {
    suspend fun <T> safeApiCall(apiCall: suspend () -> T): Result<T> {
        return withContext(Dispatchers.IO) {
            try {
                Result.Success(apiCall.invoke())
            } catch (throwable: Throwable) {
                when (throwable) {
                    is IOException -> Result.NetworkError
                    is HttpException -> {
                        val code = throwable.code()
                        var error = ""
                        when (code) {
                            500 -> error = "서버 에러입니다."
                        }

                        Result.GenericError(code, error)
                    }
                    else -> Result.GenericError(null, "오류가 발생했습니다. 다시 시도해 주세요.")
                }
            }
        }
    }

}