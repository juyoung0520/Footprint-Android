package com.footprint.footprint.viewmodel

import android.location.Location
import androidx.lifecycle.*
import com.footprint.footprint.classes.type.NonNullMutableLiveData
import com.footprint.footprint.data.dto.CourseDTO
import com.footprint.footprint.domain.model.BoundsModel
import com.footprint.footprint.utils.*

class CourseViewModel: ViewModel() {
    /* 데이터 */
    // 모드 (내 위치 중심 vs 지도 중심)
    // API 응답으로 받아온 자료들 <- 카메라 위치 바뀔 때마다 호출? -> 너무 자주는 아닐까.. 일정 거리 범위 내에서는 어떤지 물어봐야 할듯 ㅠ ㅠ
    val mode = NonNullMutableLiveData(SEARCH_IN_MY_LOCATION)
    val currentLocation = MutableLiveData<Location?>(null)
    private val _bounds = MutableLiveData<BoundsModel>(null)
    private val _courseList = NonNullMutableLiveData<MutableList<CourseDTO>>(mutableListOf()) /*수정*/

    // 정렬, 필터
    private val _sortBy = NonNullMutableLiveData<String>(SORT_BY_DISTANCE)
    private val _distance = MutableLiveData<String?>(null)
    private val _time = MutableLiveData<String?>(null)
    val isUpdated = MutableLiveData<Boolean>(false)
    val filteredCourseList = NonNullMutableLiveData<MutableList<CourseDTO>>(mutableListOf())

    /* 위치, 경계 */
    fun setCurrentLocation(location: Location){
        currentLocation.postValue(location)
    }

    fun setBounds(boundsModel: BoundsModel){
        _bounds.postValue(boundsModel)
    }

    /* 필터/정렬 기준 */
    fun setMode(newMode: String){
        mode.postValue(newMode)
        //updateCourseList()
    }

    fun setSortBy(sortBy: String){
        _sortBy.postValue(sortBy)
      //  updateCourseList()
    }

    fun setDistance(distance: String?){
        _distance.postValue(distance)
       // updateCourseList()
    }

    fun setTime(time: String?){
        _time.postValue(time)
        //updateCourseList()
    }

    fun resetAllFilter(){
        mode.postValue(SEARCH_IN_MAP)
        _sortBy.postValue(SORT_BY_DISTANCE)
        _distance.postValue(null)
        _time.postValue(null)

        //updateCourseList()
    }

    /* 필터링, 정렬해 주는 함수들 */
    // 필터: 거리, 시간 | 정렬: 내 위치 중심 vs 지도 중심, 거리 vs 인기 vs 좋아요순
    /* API 호출 */
    fun getCourseList(observer: LifecycleOwner){
        _bounds.observe(observer, androidx.lifecycle.Observer {
            if(_bounds.value == null)
                return@Observer

            // 1. 중심점, 화면 크기, 배율 계산하기
            // 2. API 호출
            LogUtils.d("CourseVM", "[${mode.value}]" +  _bounds.value.toString())

            // 3. 응답이 오면, 필터링해서 저장
           // updateCourseList()
        })
    }

 /*   private fun updateCourseList(){
        val list = _courseList.value.toList() as ArrayList
        val filtered = getFilteredList(list, mode.value, _sortBy.value, _distance.value, _time.value)
        filteredCourseList.value.clear()
        filteredCourseList.value.addAll(filtered)

        isUpdated = true
    }*/

}
