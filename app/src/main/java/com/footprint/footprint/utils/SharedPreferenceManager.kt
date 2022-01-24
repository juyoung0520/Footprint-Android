package com.footprint.footprint.utils

import android.content.Context
import androidx.appcompat.app.AppCompatActivity

/*userIdx - 유저별 고유 숫자 id*/
fun saveUserIdx(context: Context, userIdx: String){
    val spf = context.getSharedPreferences("auth", AppCompatActivity.MODE_PRIVATE)
    val editor = spf.edit()

    editor.putString("userIdx", userIdx)
    editor.apply()
}

fun getUserIdx(context: Context): String{
    val spf = context.getSharedPreferences("auth", AppCompatActivity.MODE_PRIVATE)

    return spf.getString("userIdx", "")!!
}

fun removeUserIdx(context: Context){
    val spf = context.getSharedPreferences("auth", AppCompatActivity.MODE_PRIVATE)
    val editor = spf!!.edit()

    editor.remove("userIdx")
    editor.apply()
}

/*JWT-token*/

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
