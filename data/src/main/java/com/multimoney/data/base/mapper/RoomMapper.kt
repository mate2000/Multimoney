package com.multimoney.data.base.mapper

interface RoomMapper<out T : Any> {
    fun mapToRoomEntity(): T
}