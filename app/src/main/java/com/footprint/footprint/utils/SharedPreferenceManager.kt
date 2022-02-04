package com.footprint.footprint.utils

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
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

/*암호화*/
fun saveRefreshToken(context: Context, rToken: String){
    val masterkey = MasterKey.Builder(context, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    val spf = EncryptedSharedPreferences
        .create(context,
            "auth2",
            masterkey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM)

    val editor = spf.edit()
    editor.putString("refreshToken", rToken)
    editor.commit()
}

fun getRefreshToken(context: Context): String?{
    val masterkey = MasterKey.Builder(context, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    val spf = EncryptedSharedPreferences
        .create(context,
            "auth2",
            masterkey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM)

    return spf.getString("refreshToken", null)
}

fun removeRefreshToken(context: Context){
    val masterkey = MasterKey.Builder(context, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    val spf = EncryptedSharedPreferences
        .create(context,
            "auth2",
            masterkey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM)

    val editor = spf.edit()
    editor.remove("refreshToken")
    editor.commit()
}

/*JwtId*/
fun saveJwt(jwtToken: String) {
    val editor = mSharedPreferences.edit()
    editor.putString(X_ACCESS_TOKEN, jwtToken)

    editor.apply()
}

fun getJwt(): String? = mSharedPreferences.getString(X_ACCESS_TOKEN, null)

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
