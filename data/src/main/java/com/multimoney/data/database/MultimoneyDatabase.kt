package com.multimoney.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.multimoney.data.database.dao.TestDao
import com.multimoney.data.database.model.TestEntity

@Database(
    entities = [TestEntity::class],
    version = 1,
    exportSchema = false
)
// @TypeConverters(Converters::class)
abstract class MultimoneyDatabase : RoomDatabase() {

    abstract fun testDao(): TestDao

    companion object {
        const val DATABASE_NAME = "multimoney_database"
    }
}
