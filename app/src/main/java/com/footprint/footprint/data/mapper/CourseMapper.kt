package com.footprint.footprint.data.mapper

import com.footprint.footprint.data.dto.*
import com.footprint.footprint.domain.model.*
import com.footprint.footprint.data.dto.CourseInfoDTO
import com.footprint.footprint.data.mapper.WalkMapper.convertToPaths
import com.naver.maps.geometry.LatLng

object CourseMapper {
    private fun mapperToHashtagEntity(hashtags: List<CourseHashtagDTO>): List<CourseHashtagEntity> {
        val courseHashtagEntities: MutableList<CourseHashtagEntity> = mutableListOf()

        for (hashtag in hashtags) {
            courseHashtagEntities.add(CourseHashtagEntity(hashtag.hashtagIdx, hashtag.hashtag))
        }

        return courseHashtagEntities
    }

    private fun mapperToHashtagDTO(hashtags: List<CourseHashtagEntity>): List<CourseHashtagDTO> {
        val courseHashtagDTOs: MutableList<CourseHashtagDTO> = mutableListOf()

        for (hashtag in hashtags) {
            courseHashtagDTOs.add(CourseHashtagDTO(hashtag.hashtagIdx, hashtag.hashtag))
        }

        return courseHashtagDTOs
    }

    private fun mapperToGetSelfCourseEntity(selfCourseDTO: SelfCourseDTO): SelfCourseEntity {
        return selfCourseDTO.run {
            SelfCourseEntity(userDateWalk.walkIdx, "${userDateWalk.startTime}~${userDateWalk.endTime}", userDateWalk.pathImageUrl, hashtag)
        }
    }

    fun mapperToGetCourseByCourseNameEntity(courseName: String, getCourseByCourseNameDTO: GetCourseByCourseNameDTO): GetCourseByCourseNameEntity {
        return getCourseByCourseNameDTO.run {
            GetCourseByCourseNameEntity(courseName, address, mapperToHashtagEntity(allHashtags), courseIdx, courseImg, courseTime, description, distance, mapperToHashtagEntity(selectedHashtags), walkIdx)
        }
    }

    fun mapperToRecommendDTO(recommendEntity: RecommendEntity): RecommendDTO {
        recommendEntity.run {
            return RecommendDTO(courseName, courseImg, WalkMapper.convertToCoordinates(coordinates), mapperToHashtagDTO(hashtags), address, length, courseTime, walkIdx, description)
        }
    }

    fun mapperToWalkDetailCEntity(walkDetailCDTO: WalkDetailCDTO): WalkDetailCEntity {
        walkDetailCDTO.run {
            return WalkDetailCEntity(walkIdx, walkTime, distance, WalkMapper.convertToPaths(coordinates), mapperToHashtagEntity(hashtags), photos)
        }
    }

    fun mapperToGetSelfCourseListEntity(getSelfCourseDTOs: List<SelfCourseDTO>): List<SelfCourseEntity> {
        val selfCourseEntities: MutableList<SelfCourseEntity> = mutableListOf()

        for (getSelfCourse in getSelfCourseDTOs) {
            selfCourseEntities.add(mapperToGetSelfCourseEntity(getSelfCourse))
        }

        return selfCourseEntities
    }

    fun mapperToCourseInfoModel(courseDTO: CourseDTO, courseInfoDTO: CourseInfoDTO): CourseInfoModel {
        return CourseInfoModel(
                idx = courseDTO.courseIdx,
                previewImageUrl = courseDTO.courseImg,
                title = courseDTO.courseName,
                distance = courseDTO.courseDist,
                time = courseDTO.courseTime,
                courseCount = courseDTO.courseCount,
                courseLike = courseDTO.courseLike,
                tags = courseDTO.courseTags,
                coords = convertToPaths(courseInfoDTO.coordinate),
                description = courseInfoDTO.courseDisc
        )
    }

    fun mapperToUpdateCourseReqDTO(updateCourseReqEntity: UpdateCourseReqEntity): UpdateCourseReqDTO {
        return updateCourseReqEntity.run {
            UpdateCourseReqDTO(
                    courseIdx,
                    courseName,
                    courseImg,
                    mapperToHashtagDTO(hashtags),
                    address,
                    length,
                    courseTime,
                    walkIdx,
                    description
            )
        }
    }
}