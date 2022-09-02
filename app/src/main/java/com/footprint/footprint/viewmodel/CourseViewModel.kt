package com.footprint.footprint.viewmodel

import androidx.lifecycle.*
import com.footprint.footprint.classes.type.NonNullMutableLiveData
import com.footprint.footprint.data.dto.CourseDTO
import com.footprint.footprint.data.dto.Result
import com.footprint.footprint.domain.model.BoundsModel
import com.footprint.footprint.domain.usecase.GetCourseInfoUseCase
import com.footprint.footprint.domain.usecase.GetCoursesUseCase
import com.footprint.footprint.domain.usecase.MarkCourseUseCase
import com.footprint.footprint.ui.main.course.Filtering
import com.footprint.footprint.ui.main.course.Filtering.filterState
import com.footprint.footprint.utils.*
import kotlinx.coroutines.launch

class CourseViewModel(private val getCoursesUseCase: GetCoursesUseCase, private val markCourseUseCase: MarkCourseUseCase, private val getCourseInfoUseCase: GetCourseInfoUseCase) : BaseViewModel() {
    private var errorMethod: String? = null

    /* 위치, 경계 */
    private val currentBounds = MutableLiveData<BoundsModel>(null)
    val mapBounds = MutableLiveData<BoundsModel>(null)

    fun setCurrentBounds(boundsModel: BoundsModel){
        currentBounds.value = boundsModel
    }

    fun setMapBounds(boundsModel: BoundsModel){
        mapBounds.value = boundsModel
    }

    /* 코스 리스트 */
    private val _courseList: MutableLiveData<List<CourseDTO>> = MutableLiveData()
    val filteredCourseList: MutableLiveData<List<CourseDTO>> = MutableLiveData()

    // 코스 검색 화면에서 검색어 입력해서 사용
    fun getCourses(searchWord: String?){

        var bound = mapBounds.value
        if(filterState[SEARCH_IN] == 1) bound = currentBounds.value // 내 위치 중심
        LogUtils.d("CourseVM[코스 조회]", "mode ${Filtering.filterState[SEARCH_IN]} bound $bound")

        if(bound == null)
            return

        viewModelScope.launch {
            when (val response = getCoursesUseCase.invoke(bound)) {
                is Result.Success -> {
                    if(searchWord == null) _courseList.value = response.value
                    else _courseList.value = getSearchedList(searchWord = searchWord, courseList = response.value)

                    updateFilteredCourseList()
                }
                is Result.NetworkError -> {
                    errorMethod = "getCourses"

                    mutableErrorType.postValue(ErrorType.NETWORK)
                }
                is Result.GenericError -> {
                    errorMethod = "getCourses"

                    if (response.code==600)
                        mutableErrorType.postValue(ErrorType.UNKNOWN)
                    else
                        mutableErrorType.postValue(ErrorType.DB_SERVER)
                }
            }
        }
    }

    /* 필터링 */
    // 필터: 거리, 시간 | 정렬: 내 위치 중심 vs 지도 중심, 거리 vs 인기 vs 좋아요순
    fun updateFilteredCourseList() {
        LogUtils.d("CourseVM[코스 필터링]", filterState.toString() + _courseList.value.toString())

        if(_courseList.value.isNullOrEmpty()){
            filteredCourseList.postValue(mutableListOf())
            return
        }

        val filtered = getFilteredList(
            courseList = _courseList.value!!,
            sortBy = filterState[SORT_BY]!!,
            distance = filterState[DISTANCE],
            time = filterState[TIME]
        )

        filteredCourseList.postValue(filtered as MutableList<CourseDTO>)
        LogUtils.d("CourseVM[코스 필터링-코스]", filtered.toString())
    }

    /* 코스 북마크 */
    private val _isMarked: SingleLiveEvent<Boolean> = SingleLiveEvent()
    val isMarked: SingleLiveEvent<Boolean> get() = _isMarked

    fun markCourse(courseIdx: Int){
        viewModelScope.launch {
            when (val response = markCourseUseCase.invoke(courseIdx)) {
                is Result.Success -> {
                    when(response.value.code){
                        1000 -> {
                            if(response.value.result=="찜하기")
                                _isMarked.postValue(true)
                            else
                                _isMarked.postValue(false)
                        }
                    }
                }
                is Result.NetworkError -> {
                    errorMethod = "markCourse"

                    mutableErrorType.postValue(ErrorType.NETWORK)
                }
                is Result.GenericError -> {
                    errorMethod = "markCourse"

                    if (response.code==600)
                        mutableErrorType.postValue(ErrorType.UNKNOWN)
                    else
                        mutableErrorType.postValue(ErrorType.DB_SERVER)
                }
            }
        }
    }
}
