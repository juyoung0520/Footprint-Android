package com.footprint.footprint.data.mapper

import com.footprint.footprint.data.dto.MapDTO
import com.footprint.footprint.domain.model.AddressEntity

object MapMapper {
    fun mapperToAddressEntity(mapDTO: MapDTO): AddressEntity = mapDTO.run {
        AddressEntity(this.results[0].region.area1.name, this.results[0].region.area3.name)
    }
}