package com.example.myapplication
import java.text.SimpleDateFormat
import java.util.*

fun convertUtcToKst(utcTimestamp: String): String {
    val sdfUtc = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
    sdfUtc.timeZone = TimeZone.getTimeZone("UTC")

    val utcDate = sdfUtc.parse(utcTimestamp)
    val kstCalendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"))
    kstCalendar.time = utcDate

    val sdfKst = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
    sdfKst.timeZone = TimeZone.getTimeZone("Asia/Seoul")

    return sdfKst.format(kstCalendar.time)
}