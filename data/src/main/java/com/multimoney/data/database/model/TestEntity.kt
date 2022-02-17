package com.multimoney.data.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.multimoney.data.base.DomainMapper
import com.multimoney.domain.model.launch.LaunchConnection

@Entity
data class TestEntity(
    @PrimaryKey
    val id: String
) : DomainMapper<LaunchConnection> {
    override fun mapToDomainModel() = LaunchConnection(cursor = id, true, launches = listOf())
}
