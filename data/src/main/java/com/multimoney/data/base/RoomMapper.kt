package com.multimoney.data.base

interface RoomMapper<out T : Any> {
    fun mapToRoomEntity(): T
}