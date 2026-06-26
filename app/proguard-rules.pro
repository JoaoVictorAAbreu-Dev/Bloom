# Bloom release hardening.
-keep class com.bloom.app.data.entity.** { *; }
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class * { *; }
-keep class org.json.** { *; }
