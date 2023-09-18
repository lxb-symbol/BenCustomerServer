package com.ben.bencustomerserver.utils

import android.annotation.SuppressLint
import android.content.Context
import com.ben.bencustomerserver.model.TimeInfo
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object BenDateUtils {
    private const val INTERVAL_IN_MILLISECONDS = (30 * 1000).toLong()
    private val UTC = TimeZone.getTimeZone("UTC")
    fun getTimestampString(context: Context?, messageDate: Date): String {
        var format: String? = null
        val language = Locale.getDefault().language
        val isZh = language.startsWith("zh")
        val messageTime = messageDate.time
        format = if (isSameDay(messageTime)) {
            if (is24HourFormat(context)) {
                "HH:mm"
            } else {
                if (isZh) {
                    "aa hh:mm"
                } else {
                    "hh:mm aa"
                }
            }
        } else if (isYesterday(messageTime)) {
            if (isZh) {
                if (is24HourFormat(context)) {
                    "昨天 HH:mm"
                } else {
                    "昨天aa hh:mm"
                }
            } else {
                return if (is24HourFormat(context)) {
                    "Yesterday " + SimpleDateFormat("HH:mm", Locale.ENGLISH)
                        .format(messageDate)
                } else {
                    "Yesterday " + SimpleDateFormat("hh:mm aa", Locale.ENGLISH)
                        .format(messageDate)
                }
            }
        } else {
            if (isZh) {
                if (is24HourFormat(context)) {
                    "M月d日 HH:mm"
                } else {
                    "M月d日aa hh:mm"
                }
            } else {
                if (is24HourFormat(context)) {
                    "MMM dd HH:mm"
                } else {
                    "MMM dd hh:mm aa"
                }
            }
        }
        return if (isZh) {
            SimpleDateFormat(format, Locale.CHINESE).format(messageDate)
        } else {
            SimpleDateFormat(format, Locale.ENGLISH).format(messageDate)
        }
    }

    fun getTimestampSimpleString(context: Context?, msgTimestamp: Long): String {
        val language = Locale.getDefault().language
        val isZh = language.startsWith("zh")
        val builder = StringBuilder()
        if (isWithinOneMinute(msgTimestamp)) {
            return if (isZh) {
                "刚刚"
            } else {
                "just"
            }
        }
        if (isWithinOneHour(msgTimestamp)) {
            val minute = getMinuteDifWithinSameHour(msgTimestamp)
            return if (isZh) {
                builder.append(minute).append(" 分钟前").toString()
            } else {
                builder.append(minute).append("m ago").toString()
            }
        }
        if (isWithin24Hour(msgTimestamp)) {
            val hour = getHourDifWithin24Hour(msgTimestamp)
            return if (isZh) {
                builder.append(hour).append(" 小时前").toString()
            } else {
                builder.append(hour).append("h ago").toString()
            }
        }
        if (isSameWeek(msgTimestamp)) {
            val day = getDayDifWithinSameWeek(msgTimestamp)
            return if (isZh) {
                builder.append(day).append(" 天前").toString()
            } else {
                builder.append(day).append("d ago").toString()
            }
        }
        if (isSameMonth(msgTimestamp)) {
            val week = getWeekDifWithinSameMonth(msgTimestamp)
            return if (isZh) {
                builder.append(week).append(" 周前").toString()
            } else {
                builder.append(week).append("wk ago").toString()
            }
        }
        if (isSameYear(msgTimestamp)) {
            val month = getMonthDifWithinSameYear(msgTimestamp)
            return if (isZh) {
                builder.append(month).append(" 月前").toString()
            } else {
                builder.append(month).append("mo ago").toString()
            }
        }
        val yearDif = getYearDif(msgTimestamp)
        return if (isZh) {
            builder.append(yearDif).append(" 年前").toString()
        } else {
            builder.append(yearDif).append("yr ago").toString()
        }
    }

    fun isCloseEnough(time1: Long, time2: Long): Boolean {
        // long time1 = date1.getTime();
        // long time2 = date2.getTime();
        var delta = time1 - time2
        if (delta < 0) {
            delta = -delta
        }
        return delta < INTERVAL_IN_MILLISECONDS
    }

    private fun isSameDay(inputTime: Long): Boolean {
        val tStartAndEndTime = todayStartAndEndTime
        return inputTime > tStartAndEndTime.startTime && inputTime < tStartAndEndTime.endTime
    }

    private fun isYesterday(inputTime: Long): Boolean {
        val yStartAndEndTime = yesterdayStartAndEndTime
        return inputTime > yStartAndEndTime.startTime && inputTime < yStartAndEndTime.endTime
    }

    @SuppressLint("SimpleDateFormat")
    fun stringToDate(dateStr: String?, formatStr: String?): Date? {
        val format: DateFormat = SimpleDateFormat(formatStr)
        var date: Date? = null
        try {
            date = format.parse(dateStr)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return date
    }

    /**
     *
     * @param timeLength Millisecond
     * @return
     */
    @SuppressLint("DefaultLocale")
    fun toTime(timeLength: Int): String {
        var timeLength = timeLength
        timeLength /= 1000
        var minute = timeLength / 60
        var hour = 0
        if (minute >= 60) {
            hour = minute / 60
            minute = minute % 60
        }
        val second = timeLength % 60
        // return String.format("%02d:%02d:%02d", hour, minute, second);
        return String.format("%02d:%02d", minute, second)
    }

    /**
     *
     * @param timeLength second
     * @return
     */
    @SuppressLint("DefaultLocale")
    fun toTimeBySecond(timeLength: Int): String {
//		timeLength /= 1000;
        var minute = timeLength / 60
        var hour = 0
        if (minute >= 60) {
            hour = minute / 60
            minute = minute % 60
        }
        val second = timeLength % 60
        // return String.format("%02d:%02d:%02d", hour, minute, second);
        return String.format("%02d:%02d", minute, second)
    }

    val yesterdayStartAndEndTime: TimeInfo
        get() {
            val calendar1 = Calendar.getInstance()
            calendar1.add(Calendar.DATE, -1)
            calendar1[Calendar.HOUR_OF_DAY] = 0
            calendar1[Calendar.MINUTE] = 0
            calendar1[Calendar.SECOND] = 0
            calendar1[Calendar.MILLISECOND] = 0
            val startDate = calendar1.time
            val startTime = startDate.time
            val calendar2 = Calendar.getInstance()
            calendar2.add(Calendar.DATE, -1)
            calendar2[Calendar.HOUR_OF_DAY] = 23
            calendar2[Calendar.MINUTE] = 59
            calendar2[Calendar.SECOND] = 59
            calendar2[Calendar.MILLISECOND] = 999
            val endDate = calendar2.time
            val endTime = endDate.time
            val info = TimeInfo()
            info.startTime = startTime
            info.endTime = endTime
            return info
        }
    val todayStartAndEndTime: TimeInfo
        get() {
            val calendar1 = Calendar.getInstance()
            calendar1[Calendar.HOUR_OF_DAY] = 0
            calendar1[Calendar.MINUTE] = 0
            calendar1[Calendar.SECOND] = 0
            calendar1[Calendar.MILLISECOND] = 0
            val startDate = calendar1.time
            val startTime = startDate.time
            val calendar2 = Calendar.getInstance()
            calendar2[Calendar.HOUR_OF_DAY] = 23
            calendar2[Calendar.MINUTE] = 59
            calendar2[Calendar.SECOND] = 59
            calendar2[Calendar.MILLISECOND] = 999
            val endDate = calendar2.time
            val endTime = endDate.time
            val info = TimeInfo()
            info.startTime = startTime
            info.endTime = endTime
            return info
        }
    val beforeYesterdayStartAndEndTime: TimeInfo
        get() {
            val calendar1 = Calendar.getInstance()
            calendar1.add(Calendar.DATE, -2)
            calendar1[Calendar.HOUR_OF_DAY] = 0
            calendar1[Calendar.MINUTE] = 0
            calendar1[Calendar.SECOND] = 0
            calendar1[Calendar.MILLISECOND] = 0
            val startDate = calendar1.time
            val startTime = startDate.time
            val calendar2 = Calendar.getInstance()
            calendar2.add(Calendar.DATE, -2)
            calendar2[Calendar.HOUR_OF_DAY] = 23
            calendar2[Calendar.MINUTE] = 59
            calendar2[Calendar.SECOND] = 59
            calendar2[Calendar.MILLISECOND] = 999
            val endDate = calendar2.time
            val endTime = endDate.time
            val info = TimeInfo()
            info.startTime = startTime
            info.endTime = endTime
            return info
        }
    val currentMonthStartAndEndTime: TimeInfo
        /**
         * endtime为今天
         * @return
         */
        get() {
            val calendar1 = Calendar.getInstance()
            calendar1[Calendar.DATE] = 1
            calendar1[Calendar.HOUR_OF_DAY] = 0
            calendar1[Calendar.MINUTE] = 0
            calendar1[Calendar.SECOND] = 0
            calendar1[Calendar.MILLISECOND] = 0
            val startDate = calendar1.time
            val startTime = startDate.time
            val calendar2 = Calendar.getInstance()
            //		calendar2.set(Calendar.HOUR_OF_DAY, 23);
//		calendar2.set(Calendar.MINUTE, 59);
//		calendar2.set(Calendar.SECOND, 59);
//		calendar2.set(Calendar.MILLISECOND, 999);
            val endDate = calendar2.time
            val endTime = endDate.time
            val info = TimeInfo()
            info.startTime = startTime
            info.endTime = endTime
            return info
        }
    val lastMonthStartAndEndTime: TimeInfo
        get() {
            val calendar1 = Calendar.getInstance()
            calendar1.add(Calendar.MONTH, -1)
            calendar1[Calendar.DATE] = 1
            calendar1[Calendar.HOUR_OF_DAY] = 0
            calendar1[Calendar.MINUTE] = 0
            calendar1[Calendar.SECOND] = 0
            calendar1[Calendar.MILLISECOND] = 0
            val startDate = calendar1.time
            val startTime = startDate.time
            val calendar2 = Calendar.getInstance()
            calendar2.add(Calendar.MONTH, -1)
            calendar2[Calendar.DATE] = 1
            calendar2[Calendar.HOUR_OF_DAY] = 23
            calendar2[Calendar.MINUTE] = 59
            calendar2[Calendar.SECOND] = 59
            calendar2[Calendar.MILLISECOND] = 999
            calendar2.roll(Calendar.DATE, -1)
            val endDate = calendar2.time
            val endTime = endDate.time
            val info = TimeInfo()
            info.startTime = startTime
            info.endTime = endTime
            return info
        }
    val timestampStr: String
        get() = java.lang.Long.toString(System.currentTimeMillis())

    /**
     * 判断是否是24小时制
     * @param context
     * @return
     */
    fun is24HourFormat(context: Context?): Boolean {
        return android.text.format.DateFormat.is24HourFormat(context)
    }

    private fun isWithinOneMinute(msgTimestamp: Long): Boolean {
        val calendar = Calendar.getInstance(UTC)
        val current = calendar.time.time
        return current > msgTimestamp && current - msgTimestamp < 60 * 1000
    }

    private fun isWithinOneHour(msgTimestamp: Long): Boolean {
        val calendar = Calendar.getInstance(UTC)
        val current = calendar.time.time
        return current > msgTimestamp && current - msgTimestamp < 60 * 60 * 1000
    }

    private fun isWithin24Hour(msgTimestamp: Long): Boolean {
        val calendar = Calendar.getInstance(UTC)
        val current = calendar.time.time
        return current > msgTimestamp && current - msgTimestamp < 24 * 60 * 60 * 1000
    }

    private fun isSameWeek(msgTimestamp: Long): Boolean {
        return getWeekDifWithinSameMonth(msgTimestamp) == 0
    }

    private fun isSameMonth(msgTimestamp: Long): Boolean {
        return getMonthDifWithinSameYear(msgTimestamp) == 0
    }

    private fun isSameYear(msgTimestamp: Long): Boolean {
        return getYearDif(msgTimestamp) == 0
    }

    /**
     * The result may be negative number, you should consider this situation。
     * @param msgTimestamp
     * @return
     */
    private fun getMinuteDifWithinSameHour(msgTimestamp: Long): Int {
        if (!isWithinOneHour(msgTimestamp)) {
            return -1
        }
        val calendar = Calendar.getInstance(UTC)
        return Math.ceil(((calendar.time.time - msgTimestamp) * 1.0f / (60 * 1000)).toDouble())
            .toInt()
    }

    /**
     * If not within 24 hour, return -1.
     * @param msgTimestamp
     * @return
     */
    private fun getHourDifWithin24Hour(msgTimestamp: Long): Int {
        if (!isWithin24Hour(msgTimestamp)) {
            return -1
        }
        val calendar = Calendar.getInstance(UTC)
        return Math.ceil(((calendar.time.time - msgTimestamp) * 1.0f / (60 * 60 * 1000)).toDouble())
            .toInt()
    }

    /**
     * If not in the same week of year, return -1.
     * @param msgTimestamp
     * @return
     */
    private fun getDayDifWithinSameWeek(msgTimestamp: Long): Int {
        val calendar = Calendar.getInstance(UTC)
        val curYear = calendar[Calendar.YEAR]
        val curWeek = calendar[Calendar.WEEK_OF_YEAR]
        val curDayOfWeek = calendar[Calendar.DAY_OF_WEEK]
        calendar.time = Date(msgTimestamp)
        val year = calendar[Calendar.YEAR]
        val week = calendar[Calendar.WEEK_OF_YEAR]
        val dayOfWeek = calendar[Calendar.DAY_OF_WEEK]
        return if (curYear != year || curWeek != week) {
            -1
        } else curDayOfWeek - dayOfWeek
    }

    /**
     * If not in the same month, return -1.
     * @param msgTimestamp
     * @return
     */
    private fun getWeekDifWithinSameMonth(msgTimestamp: Long): Int {
        val calendar = Calendar.getInstance(UTC)
        val curYear = calendar[Calendar.YEAR]
        val curMonth = calendar[Calendar.MONTH]
        val curWeekOfMonth = calendar[Calendar.WEEK_OF_MONTH]
        calendar.time = Date(msgTimestamp)
        val year = calendar[Calendar.YEAR]
        val month = calendar[Calendar.MONTH]
        val weekOfMonth = calendar[Calendar.WEEK_OF_MONTH]
        return if (curYear != year || curMonth != month) {
            -1
        } else curWeekOfMonth - weekOfMonth
    }

    /**
     * If not in the same year, return -1.
     * @param msgTimestamp
     * @return
     */
    private fun getMonthDifWithinSameYear(msgTimestamp: Long): Int {
        val calendar = Calendar.getInstance(UTC)
        val curYear = calendar[Calendar.YEAR]
        val curMonth = calendar[Calendar.MONTH]
        calendar.time = Date(msgTimestamp)
        val year = calendar[Calendar.YEAR]
        val month = calendar[Calendar.MONTH]
        return if (curYear != year) {
            -1
        } else curMonth - month
    }

    private fun getYearDif(msgTimestamp: Long): Int {
        val calendar = Calendar.getInstance(UTC)
        val curYear = calendar[Calendar.YEAR]
        calendar.time = Date(msgTimestamp)
        val year = calendar[Calendar.YEAR]
        return curYear - year
    }
}