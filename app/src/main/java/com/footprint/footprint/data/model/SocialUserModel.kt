package com.footprint.footprint.data.model

import java.io.Serializable

data class SocialUserModel(
    val userId: String? = null,
    var username: String? = null,
    var email: String? = null
): Serializable