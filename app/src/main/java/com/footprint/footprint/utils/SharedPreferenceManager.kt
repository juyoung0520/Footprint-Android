package com.footprint.footprint.utils

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.footprint.footprint.utils.GlobalApplication.Companion.X_ACCESS_TOKEN
import com.footprint.footprint.utils.GlobalApplication.Companion.mSharedPreferences
import com.google.gson.reflect.TypeToken

/*Onboarding- true(온보딩 실행 이력 O), false(온보딩 실행 이력 X/첫 접속)*/
fun saveOnboarding(context: Context, onboardingStatus: Boolean){
    val spf = context.getSharedPreferences("app", AppCompatActivity.MODE_PRIVATE)
    val editor = spf.edit()

    editor.putBoolean("onboarding", onboardingStatus)
    editor.apply()
}

fun getOnboarding(context: Context): Boolean{
    val spf = context.getSharedPreferences("app", AppCompatActivity.MODE_PRIVATE)

    return spf.getBoolean("onboarding", false)
}


/*Login Status - kakao, google, null*/
fun saveLoginStatus(context: Context, loginStatus: String){
    val spf = context.getSharedPreferences("auth", AppCompatActivity.MODE_PRIVATE)
    val editor = spf.edit()

    editor.putString("loginStatus", loginStatus)
    editor.apply()
}

fun getLoginStatus(context: Context): String{
    val spf = context.getSharedPreferences("auth", AppCompatActivity.MODE_PRIVATE)

    return spf.getString("loginStatus", "null")!!
}

fun removeLoginStatus(context: Context){
    val spf = context.getSharedPreferences("auth", AppCompatActivity.MODE_PRIVATE)
    val editor = spf!!.edit()

    editor.remove("loginStatus")
    editor.apply()
}


/*JwtId - 로그인 API 호출 후, 서버에서 받아오는 JwtId & API 요청 시 사용하는 X-ACCESS-TOKEN*/
fun saveJwt(jwtToken: String) {
    val editor = mSharedPreferences.edit()
    editor.putString(X_ACCESS_TOKEN, jwtToken)

    editor.apply()
}

fun getJwt(): String? = mSharedPreferences.getString(X_ACCESS_TOKEN, null)

fun removeJwt(){
    val editor = mSharedPreferences.edit()

    editor.remove(X_ACCESS_TOKEN)
    editor.apply()
}

/*Tag: 태그 검색 기록 확인에서 활용*/
fun saveTags(context: Context,tags: ArrayList<String>) {
    val spf = context.getSharedPreferences("tag", AppCompatActivity.MODE_PRIVATE)
    val editor = spf.edit()
    val json = gson.toJson(tags)

    editor.putString("tags", json)
    editor.apply()
}

fun getTags(context: Context): ArrayList<String>? {
    val spf = context.getSharedPreferences("tag", AppCompatActivity.MODE_PRIVATE)
    val json = spf.getString("tags", null)
    val type = object : TypeToken<ArrayList<String>>() {}.type

    return gson.fromJson(json, type)
}

/*Password: 산책 일기 암호*/
fun savePWD(context: Context, password: String){
    val spf = context.getSharedPreferences("app", AppCompatActivity.MODE_PRIVATE)
    val editor = spf.edit()

    editor.putString("password", password)
    editor.apply()
}

fun getPWD(context: Context): String?{
    val spf = context.getSharedPreferences("app", AppCompatActivity.MODE_PRIVATE)

    return spf.getString("password", null)
}

/*PWDstatus: 산책 일기 암호 상태 - DEFAULT(암호 X), ON(암호 ON), OFF(암호 OFF)*/
fun savePWDstatus(context: Context, status: String){
    val spf = context.getSharedPreferences("app", AppCompatActivity.MODE_PRIVATE)
    val editor = spf.edit()

    editor.putString("pwdStatus", status)
    editor.apply()
}

fun getPWDstatus(context: Context): String? {
    val spf = context.getSharedPreferences("app", AppCompatActivity.MODE_PRIVATE)

    return spf.getString("pwdStatus", "DEFAULT")
}

/*Notification: 앱 푸시 알림 - true(알림 on) false(알림 off)*/
fun saveNotification(context: Context, status: Boolean){
    val spf = context.getSharedPreferences("app", AppCompatActivity.MODE_PRIVATE)
    val editor = spf.edit()

    editor.putBoolean("notification", status)
    editor.apply()
}

fun getNotification(context: Context): Boolean{
    val spf = context.getSharedPreferences("app", AppCompatActivity.MODE_PRIVATE)

    return spf.getBoolean("notification", false)
}