package com.footprint.footprint.ui.main.course

import com.footprint.footprint.domain.model.FilteringMode
import com.footprint.footprint.domain.model.FilteringModel
import com.footprint.footprint.utils.*

object Filtering {
    private val sortBy: FilteringModel by lazy {
        FilteringModel(
            type = FilteringMode.ORDER_BY,
            title = SORT_BY,
            contents = arrayListOf(
                SORT_BY_DISTANCE,
                SORT_BY_POPULARITY,
                SORT_BY_LIKE
            ),
            units = null
        )
    }

    private val searchIn: FilteringModel by lazy {
        FilteringModel(
            type = FilteringMode.ORDER_BY,
            title = SEARCH_IN,
            contents = arrayListOf(
                SEARCH_IN_MAP,
                SEARCH_IN_MY_LOCATION
            ),
            units = null
        )
    }

    private val distance: FilteringModel by lazy {
        FilteringModel(
            type = FilteringMode.FILTER,
            title = DISTANCE,
            contents = arrayListOf(
                DISTANCE_UNDER_1KM, // 1km 미만
                DISTANCE_LESS_1KM,  // 1km 이하
                DISTANCE_LESS_2KM,  // 2km 이하
                DISTANCE_LESS_3KM,  // 3km 이하
                DISTANCE_MORE_3KM   // 3km 이상
            ),
            units = arrayListOf(
                "1km 미만",
                "1km",
                "2km",
                "3km",
                "3km 이상"
            )
        )
    }

    private val time: FilteringModel by lazy {
        FilteringModel(
            type = FilteringMode.FILTER,
            title = TIME,
            contents = arrayListOf(
                TIME_UNDER_30M, // 30분 미만
                TIME_LESS_30M,  // 30분 이하
                TIME_LESS_1H,   // 1시간 이하
                TIME_MORE_1H    // 1시간 이상
            ),
            units = arrayListOf(
                "30분 미만",
                "30분",
                "1시간",
                "1시간 이상"
            )
        )
    }

    val filters: ArrayList<FilteringModel> by lazy {
        arrayListOf(sortBy, searchIn, distance, time)
    }

    val filterState: HashMap<String, Int?> by lazy{
       hashMapOf<String, Int?>().apply {
           put(SORT_BY, 0)
           put(SEARCH_IN, 0)
           put(DISTANCE, null)
           put(TIME, null)
       }
    }

    // 필터링 초기화
    fun resetFilterState(){
        filterState.clear()
        filterState.apply {
            put(SORT_BY, 0)
            put(SEARCH_IN, 0)
            put(DISTANCE, null)
            put(TIME, null)
        }
    }

}