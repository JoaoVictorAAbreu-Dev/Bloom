package com.bloom.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
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
    version = 2,
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
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                    .also { INSTANCE = it }
            }
        }

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE habits ADD COLUMN priority TEXT NOT NULL DEFAULT 'Medium'")
                db.execSQL("ALTER TABLE habits ADD COLUMN emoji TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE habits ADD COLUMN dailyGoal INTEGER NOT NULL DEFAULT 1")
                db.execSQL("ALTER TABLE habits ADD COLUMN weeklyGoal INTEGER NOT NULL DEFAULT 5")
                db.execSQL("ALTER TABLE habits ADD COLUMN customRepeat TEXT NOT NULL DEFAULT ''")
            }
        }
    }
}
