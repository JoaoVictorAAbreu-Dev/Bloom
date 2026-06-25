package com.bloom.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.bloom.app.data.dao.HabitCompletionDao
import com.bloom.app.data.dao.HabitDao
import com.bloom.app.data.dao.PomodoroSessionDao
import com.bloom.app.data.entity.HabitCompletionEntity
import com.bloom.app.data.entity.HabitEntity
import com.bloom.app.data.entity.PomodoroSessionEntity

@Database(
    entities = [
        HabitEntity::class,
        HabitCompletionEntity::class,
        PomodoroSessionEntity::class,
    ],
    version = 1,
    exportSchema = false,
)
abstract class BloomDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao
    abstract fun habitCompletionDao(): HabitCompletionDao
    abstract fun pomodoroSessionDao(): PomodoroSessionDao

    companion object {
        @Volatile
        private var INSTANCE: BloomDatabase? = null

        fun getInstance(context: Context): BloomDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    BloomDatabase::class.java,
                    "bloom.db",
                ).build().also { INSTANCE = it }
            }
        }
    }
}
