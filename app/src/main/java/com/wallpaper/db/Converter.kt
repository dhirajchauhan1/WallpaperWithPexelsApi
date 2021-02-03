package com.wallpaper.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.wallpaper.models.Src


class Converter {

    @TypeConverter
    fun fromSrcToString(src: Src): String{
        return Gson().toJson(src)
    }

    @TypeConverter
    fun fromStringToSrc(src:String): Src{
        return Gson().fromJson(src, Src::class.java)
    }
}