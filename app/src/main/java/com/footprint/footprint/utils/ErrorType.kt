package com.footprint.footprint.utils

enum class ErrorType {
    NETWORK,    //네트워크 끊김.
    TIMEOUT,
    SESSION_EXPIRED,
    UNKNOWN,   //Retrofit 관련 에러
    DB_SERVER, //DB, 서버 통신 에러(Ex. 4000, 4001)
    JWT,       //JWT 관련 에러(Ex. 2001-2004)
    NO_BADGE,   //뱃지 관련 에러
    ADDRESS //네이버 맵에서 주소 가져올 때 발생한 에러
}