package com.footprint.footprint.data.model

import com.footprint.footprint.utils.ErrorType

sealed class Result<out T> {
    data class Success<out T>(val value: T): Result<T>()
    data class GenericError(val code: Int?, val error: String): Result<Nothing>()
    object NetworkError: Result<Nothing>()
}
