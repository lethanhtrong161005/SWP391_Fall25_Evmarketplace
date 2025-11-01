package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.utils;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.exception.CustomBusinessException;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.function.Supplier;

public final class TimeUtils {

    /** Múi giờ ứng dụng (VN) */
    public static final ZoneId APP_TZ = ZoneId.of("Asia/Ho_Chi_Minh");

    /** Clock mặc định; có thể override trong unit test */
    private static volatile Clock CLOCK = Clock.system(APP_TZ);

    /** Formatter chuẩn để log/thông báo */
    public static final DateTimeFormatter TS_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private TimeUtils() {}

    /* =========================
       Clock helpers
     ========================= */

    /** Giờ hiện tại (LocalDateTime) theo APP_TZ */
    public static LocalDateTime now() {
        return LocalDateTime.now(CLOCK);
    }

    /** Instant hiện tại (UTC) lấy từ CLOCK */
    public static Instant nowInstant() {
        return Instant.now(CLOCK);
    }

    /** ZoneId đang dùng (luôn APP_TZ, đưa ra để đồng bộ nơi khác nếu cần) */
    public static ZoneId appZone() {
        return APP_TZ;
    }

    /** Chỉ dùng trong unit test để kiểm soát thời gian */
    public static void setClock(Clock testClock) {
        CLOCK = Objects.requireNonNull(testClock, "testClock");
    }

    /** Trả clock về mặc định (theo APP_TZ) — dùng cuối test */
    public static void resetClock() {
        CLOCK = Clock.system(APP_TZ);
    }

    /* =========================
       Expiration helpers
     ========================= */

    /**
     * Kiểm tra hết hạn theo quy tắc: expired nếu exp != null và exp <= now().
     * (tức là KHÔNG còn "isAfter(now)")
     */
    public static boolean isExpired(LocalDateTime exp) {
        if (exp == null) return false;
        return !exp.isAfter(now());
    }

    /**
     * Ném CustomBusinessException nếu đã hết hạn.
     * message nếu không truyền sẽ là "Expired at yyyy-MM-dd HH:mm:ss".
     */
    public static void assertNotExpired(LocalDateTime exp, Supplier<String> messageSupplier) {
        if (isExpired(exp)) {
            String msg = (messageSupplier != null)
                    ? messageSupplier.get()
                    : "Expired at " + formatTs(exp);
            throw new CustomBusinessException(msg);
        }
    }

    /** Overload tiện dụng với message mặc định */
    public static void assertNotExpired(LocalDateTime exp) {
        assertNotExpired(exp, null);
    }

    /* =========================
       Formatting helpers
     ========================= */

    /** Format LocalDateTime theo TS_FMT (yyyy-MM-dd HH:mm:ss) */
    public static String formatTs(LocalDateTime dt) {
        if (dt == null) return null;
        return dt.format(TS_FMT);
    }

    /** Chuyển LocalDateTime (theo APP_TZ) sang Instant (UTC) */
    public static Instant toInstant(LocalDateTime dt) {
        if (dt == null) return null;
        return dt.atZone(APP_TZ).toInstant();
    }

    /** Từ Instant về LocalDateTime theo APP_TZ */
    public static LocalDateTime toLocalDateTime(Instant instant) {
        if (instant == null) return null;
        return LocalDateTime.ofInstant(instant, APP_TZ);
    }




}
