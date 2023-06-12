package com.multimoney.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.multimoney.data.database.model.TestEntity

@Dao
interface TestDao {
    @Insert(onConflict = REPLACE)
    suspend fun insertTest(testEntity: TestEntity)

    @Query("DELETE FROM TestEntity")
    suspend fun deleteTest()

    @Query("SELECT * FROM TestEntity")
    fun getTest(): TestEntity
}
