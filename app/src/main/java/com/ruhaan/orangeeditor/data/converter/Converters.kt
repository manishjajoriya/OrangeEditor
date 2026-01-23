package com.ruhaan.orangeeditor.data.converter

import androidx.room.TypeConverter
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset

class Converters {

  @TypeConverter
  fun fromOffsetDateTime(value: OffsetDateTime): Long {
    return value.toEpochSecond()
  }

  @TypeConverter
  fun toOffsetDateTime(value: Long): OffsetDateTime {
    return OffsetDateTime.ofInstant(Instant.ofEpochSecond(value), ZoneOffset.UTC)
  }
}
