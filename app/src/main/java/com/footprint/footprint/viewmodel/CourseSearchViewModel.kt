package com.footprint.footprint.viewmodel

import androidx.lifecycle.*
import com.footprint.footprint.classes.type.NonNullMutableLiveData
import com.footprint.footprint.data.dto.CourseDTO
import com.footprint.footprint.domain.model.BoundsModel
import com.footprint.footprint.domain.model.FilteringMode
import com.footprint.footprint.ui.main.course.Filtering
import com.footprint.footprint.ui.main.course.Filtering.filters
import com.footprint.footprint.utils.LogUtils
import com.footprint.footprint.utils.SEARCH_IN
import com.footprint.footprint.utils.SEARCH_IN_MY_LOCATION
import com.naver.maps.geometry.LatLng

class CourseSearchViewModel : ViewModel() {
    /* 위치 */
    // 모드 (내 위치 중심 vs 지도 중심)
    // API 응답으로 받아온 자료들 <- 카메라 위치 바뀔 때마다 호출? -> 너무 자주는 아닐까.. 일정 거리 범위 내에서는 어떤지 물어봐야 할듯 ㅠ ㅠ
    private val currentBounds = MutableLiveData<BoundsModel>(null)
    val mapBounds = MutableLiveData<BoundsModel>(null)

    /* 위치, 경계 */
    fun setCurrentBounds(boundsModel: BoundsModel){
        currentBounds.postValue(boundsModel)
    }

    fun setMapBounds(boundsModel: BoundsModel){
        mapBounds.postValue(boundsModel)
        LogUtils.d("CourseVM", " bound ${boundsModel}")
    }

    /* 코스 리스트 */
    private val _courseList = NonNullMutableLiveData<MutableList<CourseDTO>>(mutableListOf()) /*수정*/
    val filteredCourseList = NonNullMutableLiveData<MutableList<CourseDTO>>(mutableListOf())

//    fun getCourseList(observer: LifecycleOwner) {
//        mapBounds.observe(observer, androidx.lifecycle.Observer {
//            if (mapBounds.value == null)
//                return@Observer
//
//            // 1. 중심점, 화면 크기, 배율 계산하기
//            // 2. API 호출
//            //  LogUtils.d("CourseVM", "[${mode.value}]" + _bounds.value.toString())
//
//            // 3. 응답이 오면, 필터링해서 저장
//            // updateCourseList()
//        })
//    }

    fun getCourses(){
        var bound = mapBounds

        if(Filtering.filterState[SEARCH_IN] == 1){ // 내 위치 중심
            bound = currentBounds
        }

        LogUtils.d("CourseVM", "mode ${Filtering.filterState[SEARCH_IN]} bound ${bound.value}")
        // API 호출
    }

    /* 필터링 */
    // 필터: 거리, 시간 | 정렬: 내 위치 중심 vs 지도 중심, 거리 vs 인기 vs 좋아요순
     fun updateFilteredCourseList() {
        val filterState = Filtering.filterState
       // setMode(filters[0].contents[filterState[SEARCH_IN]!!])

        LogUtils.d("CourseVm", filterState.toString())

//        val filtered = getFilteredList(
//            courseList = _courseList.value as ArrayList<CourseDTO>,
//            sortBy = filterState[SORT_BY]!!,
//            searchIn = filterState[SEARCH_IN]!!,
//            distance = filterState[DISTANCE],
//            time = filterState[TIME]
//        )
//
//        filteredCourseList.value.clear()
//        filteredCourseList.value.addAll(filtered)
    }
}
