package com.footprint.footprint.ui.main.home

import java.util.*
import kotlin.math.ceil

/* HomeMonthCalendar.kt
* Calendar 프래그먼트의 현재 날짜(currentDate)를 넘겨서
* 달력에 표시할 Int값 42개를 받아와 RV에 보여주는 클래스
*/
class HomeMonthCalendar(date: Date) {

    //기본 세팅
    companion object {
        const val DAYS_OF_WEEK = 7
        var LOW_OF_CALENDAR = 6
    }

    val calendar = Calendar.getInstance()

    var prevTail = 0
    var nextHead = 0
    var currentMaxDate = 0
    var currentDays = 0
    var weeks = 0

    var dateList = arrayListOf<Int>()

    init {
        calendar.time = date
    }

    fun initBaseCalendar() {
         makeMonthDate()
    }


    /*함수*/
    private fun makeMonthDate() {

        dateList.clear() //리스트 초기화

        calendar.set(Calendar.DATE, 1) //1일부터

        currentMaxDate = calendar.getActualMaximum(Calendar.DAY_OF_MONTH) //month의 day까지

        //이전 달
        prevTail = calendar.get(Calendar.DAY_OF_WEEK) - 1
        val prevDays = makePrevTail(calendar.clone() as Calendar)

        //이번 달
        currentDays = makeCurrentMonth(calendar) - prevDays

        /*5주 or 6주 */
        //1. 7 - prevDays = 일요일까지 남은 일수
        val untilSun = 7 - prevDays
        //2. currentDays - 남은 일수 = 나머지
        val rest = currentDays - untilSun
        //3. Math.ceil(나머지/7)
        weeks = ceil(rest/7.toDouble()).toInt() + 1
        LOW_OF_CALENDAR = weeks

        //다음 달
        nextHead = LOW_OF_CALENDAR * DAYS_OF_WEEK - (prevTail + currentMaxDate)
        makeNextHead()
    }
    private fun makePrevTail(calendar: Calendar): Int {
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1) //이전달로 바꾸기
        val maxDate = calendar.getActualMaximum(Calendar.DATE)
        var maxOffsetDate = maxDate - prevTail

        for (i in 1..prevTail) dateList.add(++maxOffsetDate)

        return dateList.size
    }
    private fun makeCurrentMonth(calendar: Calendar): Int {
        for (i in 1..calendar.getActualMaximum(Calendar.DATE)) dateList.add(i)

        val currentDays = dateList.size
        return currentDays
    }

    private fun makeNextHead() {
        var date = 1

        for (i in 1..nextHead) dateList.add(date++)
    }
}