package com.footprint.footprint.model

import java.util.ArrayList

data class PostsModel(
    var posts: ArrayList<PostModel> = arrayListOf()
)

//기록 데이터 클래스(임시, API 연결 시 수정)
data class PostModel(
    var content: String = "",
    var photos: ArrayList<String> = arrayListOf(),
    var time: String = "",
    var hashTags: ArrayList<String> = arrayListOf()
)
