package com.eswar.employeemanager.data

import androidx.room.TypeConverter
import java.time.LocalDate

/** SQLite has no DATE type - store LocalDate as the epoch-day number (INTEGER). */
class Converters {
    @TypeConverter fun fromEpochDay(value: Long?): LocalDate? = value?.let(LocalDate::ofEpochDay)
    @TypeConverter fun toEpochDay(date: LocalDate?): Long? = date?.toEpochDay()
}
