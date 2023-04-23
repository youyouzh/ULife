package com.uusama.common.util;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.chrono.Era;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;

/**
 * @author uusama
 */
public class DateTimeUtil {

    /**
     * 时区 - 默认
     */
    public static final String TIME_ZONE_DEFAULT = "GMT+8";

    /**
     * 秒转换成毫秒
     */
    public static final long SECOND_MILLIS = 1000;

    public static final String FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static final String FORMAT_HOUR_MINUTE_SECOND = "HH:mm:ss";

    /**
     * 获取两个日期的差，如果结束时间早于开始时间，获取结果为负。
     * <p>
     * 返回结果为时间差的long值
     *
     * @param startTimeInclude 开始时间（包括）
     * @param endTimeExclude   结束时间（不包括）
     * @param unit             时间差单位
     * @return 时间差
     */
    public static long between(Temporal startTimeInclude, Temporal endTimeExclude, ChronoUnit unit) {
        return unit.between(startTimeInclude, endTimeExclude);
    }

    /**
     * {@link TemporalAccessor}转换为 时间戳（从1970-01-01T00:00:00Z开始的毫秒数）<br>
     * 如果为{@link Month}，调用{@link Month#getValue()}
     *
     * @param temporalAccessor Date对象
     * @return {@link Instant}对象
     * @since 5.4.1
     */
    public static long toEpochMilli(TemporalAccessor temporalAccessor) {
        if(temporalAccessor instanceof Month){
            return ((Month) temporalAccessor).getValue();
        } else if(temporalAccessor instanceof DayOfWeek){
            return ((DayOfWeek) temporalAccessor).getValue();
        } else if(temporalAccessor instanceof Era){
            return ((Era) temporalAccessor).getValue();
        }
        return toInstant(temporalAccessor).toEpochMilli();
    }

    /**
     * {@link TemporalAccessor}转换为 {@link Instant}对象
     *
     * @param temporalAccessor Date对象
     * @return {@link Instant}对象
     * @since 5.3.10
     */
    public static Instant toInstant(TemporalAccessor temporalAccessor) {
        if (null == temporalAccessor) {
            return null;
        }

        Instant result;
        if (temporalAccessor instanceof Instant) {
            result = (Instant) temporalAccessor;
        } else if (temporalAccessor instanceof LocalDateTime) {
            result = ((LocalDateTime) temporalAccessor).atZone(ZoneId.systemDefault()).toInstant();
        } else if (temporalAccessor instanceof ZonedDateTime) {
            result = ((ZonedDateTime) temporalAccessor).toInstant();
        } else if (temporalAccessor instanceof OffsetDateTime) {
            result = ((OffsetDateTime) temporalAccessor).toInstant();
        } else if (temporalAccessor instanceof LocalDate) {
            result = ((LocalDate) temporalAccessor).atStartOfDay(ZoneId.systemDefault()).toInstant();
        } else if (temporalAccessor instanceof LocalTime) {
            // 指定本地时间转换 为Instant，取当天日期
            result = ((LocalTime) temporalAccessor).atDate(LocalDate.now()).atZone(ZoneId.systemDefault()).toInstant();
        } else if (temporalAccessor instanceof OffsetTime) {
            // 指定本地时间转换 为Instant，取当天日期
            result = ((OffsetTime) temporalAccessor).atDate(LocalDate.now()).toInstant();
        } else {
            // issue#1891@Github
            // Instant.from不能完成日期转换
            // result = Instant.from(temporalAccessor);
            result = toInstant(of(temporalAccessor));
        }

        return result;
    }

    /**
     * {@link TemporalAccessor}转{@link LocalDateTime}，使用默认时区
     *
     * @param temporalAccessor {@link TemporalAccessor}
     * @return {@link LocalDateTime}
     */
    public static LocalDateTime of(TemporalAccessor temporalAccessor) {
        if (null == temporalAccessor) {
            return null;
        }

        if (temporalAccessor instanceof LocalDate) {
            return ((LocalDate) temporalAccessor).atStartOfDay();
        } else if(temporalAccessor instanceof Instant){
            return LocalDateTime.ofInstant((Instant) temporalAccessor, ZoneId.systemDefault());
        } else if(temporalAccessor instanceof ZonedDateTime){
            return ((ZonedDateTime)temporalAccessor).toLocalDateTime();
        }

        return LocalDateTime.of(
            get(temporalAccessor, ChronoField.YEAR),
            get(temporalAccessor, ChronoField.MONTH_OF_YEAR),
            get(temporalAccessor, ChronoField.DAY_OF_MONTH),
            get(temporalAccessor, ChronoField.HOUR_OF_DAY),
            get(temporalAccessor, ChronoField.MINUTE_OF_HOUR),
            get(temporalAccessor, ChronoField.SECOND_OF_MINUTE),
            get(temporalAccessor, ChronoField.NANO_OF_SECOND)
        );
    }

    /**
     * 安全获取时间的某个属性，属性不存在返回最小值，一般为0<br>
     * 注意请谨慎使用此方法，某些{@link TemporalAccessor#isSupported(TemporalField)}为{@code false}的方法返回最小值
     *
     * @param temporalAccessor 需要获取的时间对象
     * @param field            需要获取的属性
     * @return 时间的值，如果无法获取则获取最小值，一般为0
     */
    public static int get(TemporalAccessor temporalAccessor, TemporalField field) {
        if (temporalAccessor.isSupported(field)) {
            return temporalAccessor.get(field);
        }

        return (int)field.range().getMinimum();
    }

    public static boolean isExpired(LocalDateTime time) {
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(time);
    }

    public static boolean isNotExpired(LocalDateTime time) {
        return !isExpired(time);
    }

    public static String formatToDate(LocalDateTime time) {
        return DATE_FORMATTER.format(time);
    }
}
