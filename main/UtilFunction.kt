package com.example.myapplication
import java.text.SimpleDateFormat
import java.util.*
import java.math.RoundingMode
import java.text.DecimalFormat
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

fun listToString(list: List<String>, delimiter: String = ", "): String {
    return list.joinToString(delimiter)
}
fun roundStringToOneDecimalPlaces(numberString: String): String {
    try {
        val originalNumber = numberString.toFloat() // 문자열을 부동 소수점 숫자로 파싱
        val df = DecimalFormat("#.#")
        df.roundingMode = RoundingMode.HALF_UP // 반올림 설정
        return df.format(originalNumber) // 반올림된 문자열 반환
    } catch (e: NumberFormatException) {
        return "Invalid Number" // 유효하지 않은 숫자 문자열 처리
    }
}