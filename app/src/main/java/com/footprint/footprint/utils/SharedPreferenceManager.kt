package com.footprint.footprint.utils

import android.content.Context
import androidx.appcompat.app.AppCompatActivity

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

/*Token - Google, Kakao 로그인 시 받는 Access Token*/
fun saveToken(context: Context, token: String){
    val spf = context.getSharedPreferences("auth", AppCompatActivity.MODE_PRIVATE)
    val editor = spf.edit()

    editor.putString("token", token)
    editor.apply()
}

fun getToken(context: Context): String{
    val spf = context.getSharedPreferences("auth", AppCompatActivity.MODE_PRIVATE)

    return spf.getString("token", "")!!
}

fun removeToken(context: Context){
    val spf = context.getSharedPreferences("auth", AppCompatActivity.MODE_PRIVATE)
    val editor = spf!!.edit()

    editor.remove("token")
    editor.apply()
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
