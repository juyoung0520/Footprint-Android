package com.footprint.footprint.utils

import com.footprint.footprint.data.dto.CourseDTO

/* 코스 필터링, 정렬 Utils*/

// 정렬 기준 (거리순, 인기순, 좋아요순)
const val SORT_BY_DISTANCE = "sort_by_distance"
const val SORT_BY_POPULARITY = "sort_by_popularity"
const val SORT_BY_LIKE = "sort_by_like"

// 검색 위치 (지도 중심, 내 위치 중심)
const val SEARCH_IN_MAP = "search_in_map"
const val SEARCH_IN_MY_LOCATION = "search_in_my_location"

// 거리 필터
const val DISTANCE_UNDER_1KM = "distance_under_1km"
const val DISTANCE_LESS_1KM = "distance_less_1km"
const val DISTANCE_LESS_2KM = "distance_less_2km"
const val DISTANCE_LESS_3KM = "distance_less_3km"
const val DISTANCE_MORE_3KM = "distance_more_3km"

// 시간 필터
const val TIME_UNDER_30M = "time_under_30m"
const val TIME_LESS_30M = "time_less_30m"
const val TIME_LESS_1H = "time_less_1m"
const val TIME_MORE_1H = "time_more_1h"

fun getFilteredList(courseList: ArrayList<CourseDTO>, searchIn: String, sortBy: String,  distance: String?, time: String?): ArrayList<CourseDTO>{
    // 필터링 후 반환

    return courseList
}

