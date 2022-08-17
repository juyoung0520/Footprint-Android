package com.footprint.footprint.data.repository.remote

import com.footprint.footprint.data.dto.Result
import com.footprint.footprint.utils.LogUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

abstract class BaseRepository {
    suspend fun <T> safeApiCall(apiCall: suspend () -> T): Result<T> {
        return withContext(Dispatchers.IO) {
            try {
                Result.Success(apiCall.invoke()) //api 부르기
            } catch (throwable: Throwable) {
                when (throwable) {
                    is IOException -> Result.NetworkError   //네트워크 연결 실패
                    is HttpException -> {
                        val code = throwable.code()
                        var error = ""
                        when (code) {
                            500 -> error = "서버 에러입니다."
                        }
                        Result.GenericError(code, error)
                    }
                    else -> {
                        LogUtils.d("safeApiCall", throwable.stackTraceToString())
                        Result.GenericError(null, "오류가 발생했습니다. 다시 시도해 주세요.")
                    }
                }
            }
        }
    }

    suspend fun <T> safeApiCall2(apiCall: suspend () -> Response<T>): Result<T> {
        return try {
            val myResp = apiCall.invoke()

            if (myResp.isSuccessful)
                Result.Success(myResp.body()!!)
            else
                Result.GenericError(myResp.code(), myResp.message() ?: "Something goes wrong")  //code: HTTP STATUS, message: Retrofit 에서 보내주는 message.
        } catch (e: IOException) {
            Result.NetworkError
        } catch (e: Exception) {
            Result.GenericError(600, e.message?: "Retrofit Error")
        }
    }
}