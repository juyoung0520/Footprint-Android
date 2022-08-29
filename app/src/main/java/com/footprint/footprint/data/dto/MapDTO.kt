package com.footprint.footprint.data.dto

data class MapDTO(
    val results: List<ResultX>,
    val status: Status
)

data class ResultX(
    val code: Code,
    val name: String,
    val region: Region
)

data class Code(
    val id: String,
    val mappingId: String,
    val type: String
)

data class Region(
    val area0: Area0,
    val area1: Area0,
    val area2: Area0,
    val area3: Area0,
    val area4: Area0
)

data class Area0(
    val coords: Coords,
    val name: String
)

data class Coords(
    val center: Center
)

data class Center(
    val crs: String,
    val x: Double,
    val y: Double
)

data class Status(
    val code: Int,
    val message: String,
    val name: String
)