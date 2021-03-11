package com.hdwallpaper4K.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.hdwallpaper4K.models.Photo

@Database(
    entities = [Photo::class],
    version = 1
)
@TypeConverters(Converter::class)
abstract class MainDatabase : RoomDatabase() {

    abstract fun getPhotoDao(): PhotoDao

    companion object{
        @Volatile
        private var instance: MainDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK){
            instance ?: createDatabase(context).also{ instance = it}
        }

        private fun createDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                MainDatabase::class.java,
                "main_db.db"
            ).build()
    }
}