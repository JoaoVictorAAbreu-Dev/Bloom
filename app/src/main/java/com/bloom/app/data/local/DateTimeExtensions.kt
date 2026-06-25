package com.bloom.app.data.local

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime

internal fun Long.startOfDayMillis(zoneId: ZoneId = ZoneId.systemDefault()): Long {
    return LocalDate.ofInstant(Instant.ofEpochMilli(this), zoneId)
        .atStartOfDay(zoneId)
        .toInstant()
        .toEpochMilli()
}

internal fun nowDayStartMillis(zoneId: ZoneId = ZoneId.systemDefault()): Long {
    return ZonedDateTime.now(zoneId).toLocalDate().atStartOfDay(zoneId).toInstant().toEpochMilli()
}

internal fun LocalDate.startOfDayMillis(zoneId: ZoneId = ZoneId.systemDefault()): Long {
    return atStartOfDay(zoneId).toInstant().toEpochMilli()
}
