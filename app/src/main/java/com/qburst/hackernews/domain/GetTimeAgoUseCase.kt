package com.qburst.hackernews.domain

class GetTimeAgoUseCase(
    private val default: String = "sometime ago"
) {

    companion object {
        private const val SECOND_MILLIS = 1000
        private const val MINUTE_MILLIS = 60 * SECOND_MILLIS
        private const val HOUR_MILLIS = 60 * MINUTE_MILLIS
        private const val DAY_MILLIS = 24 * HOUR_MILLIS
    }

    operator fun invoke(t: Long?): String {

        t ?: return default

        val time = if (t < 1_000_000_000_000L) {
            t * 1000
        } else t

        val now = System.currentTimeMillis()
        if (time == 0L || time > now || time <= 0) {
            return default
        }

        val diff = now - time
        return when {
            diff < MINUTE_MILLIS -> {
                "just now"
            }
            diff < 2 * MINUTE_MILLIS -> {
                "a minute ago"
            }
            diff < 50 * MINUTE_MILLIS -> {
                "${diff / MINUTE_MILLIS} minutes ago"
            }
            diff < 90 * MINUTE_MILLIS -> {
                "an hour ago"
            }
            diff < 24 * HOUR_MILLIS -> {
                "${diff / HOUR_MILLIS} hours ago"
            }
            diff < 48 * HOUR_MILLIS -> {
                "yesterday"
            }
            else -> {
                "${diff / DAY_MILLIS} days ago"
            }
        }
    }
}