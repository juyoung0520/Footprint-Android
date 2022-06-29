package com.footprint.footprint.utils

import android.util.Log
import com.footprint.footprint.BuildConfig
import com.google.gson.*
import com.google.gson.JsonParseException
import java.lang.reflect.Type
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*


object NetworkUtils {
    fun <T> encrypt(data: T): String {
        val json = Gson().toJson(data)
        LogUtils.d("NetworkUtils", "암호화 전 json: ${json}")
        return AES128(BuildConfig.encrypt_key).encrypt(json)
    }

    fun <T> decrypt(data: String?, className: Class<T>): T {
        val gson = GsonBuilder().registerTypeAdapter(
            LocalDateTime::class.java,
            JsonDeserializer<LocalDateTime?> { json, type, jsonDeserializationContext ->
                ZonedDateTime.parse(
                    json.asString
                ).toLocalDateTime()
            }).create()

//        val gsonBuilder = GsonBuilder()
//        gsonBuilder.registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeDeserializer())
//        val gson = gsonBuilder.setPrettyPrinting().create()
//

//        val gson = GsonBuilder().registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeConvertor).create()
//
//        val gsonBuilder = GsonBuilder()
//        gsonBuilder.registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeDeserializer())
//        val gson = gsonBuilder.setPrettyPrinting().create()

        return gson.fromJson(data, className)
    }

    fun <T> decrypt(data: String?, type: Type): T {
        return Gson().fromJson(data, type)
    }

    internal class LocalDateTimeSerializer: JsonSerializer<LocalDateTime>{
        override fun serialize(
            localDateTime: LocalDateTime?,
            srcType: Type?,
            context: JsonSerializationContext?
        ): JsonElement? {
            return JsonPrimitive(
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(
                    localDateTime
                )
            )
        }

    }

    internal class LocalDateTimeDeserializer : JsonDeserializer<LocalDateTime> {
        @Throws(JsonParseException::class)
        override fun deserialize(
            json: JsonElement,
            typeOfT: Type,
            context: JsonDeserializationContext
        ): LocalDateTime {
            return LocalDateTime.parse(
                json.asString,
                DateTimeFormatter.ofPattern("d::MMM::uuuu HH::mm::ss").withLocale(Locale.ENGLISH)
            )
        }
    }
}
