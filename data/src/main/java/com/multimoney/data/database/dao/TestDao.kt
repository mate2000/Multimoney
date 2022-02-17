package com.multimoney.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.multimoney.data.base.BaseDao
import com.multimoney.data.database.model.TestEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TestDao : BaseDao<TestEntity> {

    @Insert(onConflict = REPLACE)
    suspend fun insertTest(testEntity: TestEntity)

    @Query("DELETE FROM TestEntity")
    suspend fun deleteTest()

    @Query("SELECT * FROM TestEntity")
    fun getTest(): Flow<TestEntity>
}
