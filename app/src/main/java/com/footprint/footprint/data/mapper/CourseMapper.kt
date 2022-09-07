package com.footprint.footprint.data.mapper

import com.footprint.footprint.data.dto.CourseDTO
import com.footprint.footprint.data.dto.CourseInfoDTO
import com.footprint.footprint.data.dto.UserDTO
import com.footprint.footprint.data.mapper.WalkMapper.convertToPaths
import com.footprint.footprint.domain.model.CourseInfoModel
import com.footprint.footprint.domain.model.MyInfoUserModel
import com.footprint.footprint.domain.model.SimpleUserModel
import com.naver.maps.geometry.LatLng

object CourseMapper {
    fun mapperToCourseInfoModel(courseDTO: CourseDTO, courseInfoDTO: CourseInfoDTO): CourseInfoModel{
        return CourseInfoModel(
            previewImageUrl = courseDTO.courseImg,
            title = courseDTO.courseName,
            distance = courseDTO.courseDist, // Double로 바꿔야 함
            time = courseDTO.courseTime,
            tags = courseDTO.courseTags,
            coords = convertToPaths(courseInfoDTO.coordinate),
            description = courseInfoDTO.courseDisc
        )
    }
}