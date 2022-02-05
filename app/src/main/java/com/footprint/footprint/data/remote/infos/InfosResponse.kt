package com.footprint.footprint.data.remote.infos

import com.google.gson.annotations.SerializedName

/*정보 등록 API-> Response*/
data class InfosResponse(
    @SerializedName("isSuccess") val isSuccess: Boolean,
    @SerializedName("code")val code: Int,
    @SerializedName("message")val message: String,
    @SerializedName("result")val result: String
)

