package com.footprint.footprint.utils

import com.footprint.footprint.data.dto.CourseDTO
import com.footprint.footprint.domain.model.FilteringMode
import com.footprint.footprint.ui.main.course.Filtering
import com.footprint.footprint.ui.main.course.Filtering.filters

/* 코스 필터링, 정렬 Utils*/
const val SORT_BY = "정렬 기준"
const val SEARCH_IN = "검색 위치 설정"
const val DISTANCE = "거리 설정"
const val TIME = "시간 설정"

// 정렬 기준 (거리순, 인기순, 좋아요순)
const val SORT_BY_DISTANCE = "거리순"
const val SORT_BY_POPULARITY = "인기순"
const val SORT_BY_LIKE = "좋아요순"

// 검색 위치 (지도 중심, 내 위치 중심)
const val SEARCH_IN_MAP = "지도 중심"
const val SEARCH_IN_MY_LOCATION = "내 위치 중심"

// 거리 필터
const val DISTANCE_UNDER_1KM = "1km 미만"
const val DISTANCE_LESS_1KM = "1km 이하"
const val DISTANCE_LESS_2KM = "2km 이하"
const val DISTANCE_LESS_3KM = "3km 이하"
const val DISTANCE_MORE_3KM = "3km 이상"

// 시간 필터
const val TIME_UNDER_30M = "30분 미만"
const val TIME_LESS_30M = "30분 이하"
const val TIME_LESS_1H = "1시간 이하"
const val TIME_MORE_1H = "1시간 이상"

fun getFilteredList(courseList: List<CourseDTO>, sortBy: Int, distance: Int?, time: Int?): List<CourseDTO> {
    var filteredList = courseList

    filteredList = sortBy(filteredList, sortBy)
    if(distance != null) filteredList = filterWithDistance(filteredList, distance)
    if(time != null) filteredList = filterWithTime(filteredList, time)

    return filteredList
}

fun getSearchedList(searchWord: String, courseList: List<CourseDTO>): List<CourseDTO>{
    // 제목, 태그에서 검색
    return courseList.filter { it.courseName.contains(searchWord) || it.courseTags.contains(searchWord)}
}

// 정렬
fun sortBy(courseList: List<CourseDTO>, sortBy: Int): List<CourseDTO> {
    return when(sortBy){
        0 -> courseList.sortedBy { it.courseDist }
        1 -> courseList.sortedByDescending { it.courseCount }
        else -> courseList.sortedByDescending { it.courseLike }
    }
}

// 필터링
fun filterWithDistance(courseList: List<CourseDTO>, distance: Int): List<CourseDTO>{
    return when(distance){
        0 -> courseList.filter { it.courseDist < 1 }     // 1km 미만
        1 -> courseList.filter { it.courseDist <= 1 }    // 1km 이하
        2 -> courseList.filter { it.courseDist <= 2 }    // 2km 이하
        3 -> courseList.filter { it.courseDist <= 3 }    // 3km 이하
        else -> courseList.filter { it.courseDist >= 3 } // 3km 이상
    }
}

fun filterWithTime(courseList: List<CourseDTO>, time: Int): List<CourseDTO>{
    return when(time){
        0 -> courseList.filter { it.courseTime < 30 }     // 30분 미만
        1 -> courseList.filter { it.courseTime <= 30 }    // 30분 이상
        2 -> courseList.filter { it.courseTime <= 60 }    // 1시간 이하
        else -> courseList.filter { it.courseTime >= 60 } // 1시간 이상
    }
}

/* */
fun getNumberOfActivateFilters(filterState: HashMap<String, Int?>): Int{
    var num = 0

    for(filter in filters){
        if(!isDefault(filter.type, filterState[filter.title])) num++
    }

    return num
}

fun isDefault(type: Int, state: Int?): Boolean{
    var default: Int? = 0
    if(type ==  FilteringMode.FILTER) default = null

    return state == default
}

