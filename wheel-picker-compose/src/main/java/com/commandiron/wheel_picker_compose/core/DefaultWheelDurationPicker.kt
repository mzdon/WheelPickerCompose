package com.commandiron.wheel_picker_compose.core

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@Composable
internal fun DefaultWheelDurationPicker(
    modifier: Modifier = Modifier,
    startDuration: Duration = Duration.ZERO,
    minDuration: Duration = Duration.ZERO,
    maxDuration: Duration = Duration.INFINITE,
    durationFormat: DurationFormat = DurationFormat.HOURS_MINUTES_SECONDS,
    size: DpSize = DpSize(128.dp, 128.dp),
    rowCount: Int = 3,
    textStyle: TextStyle = MaterialTheme.typography.titleMedium,
    textColor: Color = LocalContentColor.current,
    selectorProperties: SelectorProperties = WheelPickerDefaults.selectorProperties(),
    onSnappedDuration: (snappedDuration: SnappedDuration, durationFormat: DurationFormat) -> Int? = { _, _ -> null },
) {

    var snappedDuration by remember { mutableStateOf(startDuration) }

    val days =
        (minDuration.minDays.coerceAtLeast(0)..maxDuration.maxDays.coerceAtMost(100)).mapIndexed { idx, it ->
            D(
                text = it.toString(),
                value = it,
                index = idx
            )
        }

    val hours =
        (minDuration.minHours.coerceAtLeast(0)..maxDuration.maxHours.coerceAtMost(23)).mapIndexed { idx, it ->
            H(
                text = it.toString().padStart(2, '0'),
                value = it,
                index = idx
            )
        }

    val minutes =
        (minDuration.minMinutes.coerceAtLeast(0)..maxDuration.maxMinutes.coerceAtMost(59)).mapIndexed { idx, it ->
            M(
                text = it.toString().padStart(2, '0'),
                value = it,
                index = idx
            )
        }

    val seconds =
        (minDuration.minSeconds.coerceAtLeast(0)..maxDuration.maxSeconds.coerceAtMost(59)).mapIndexed { idx, it ->
            S(
                text = it.toString().padStart(2, '0'),
                value = it,
                index = idx
            )
        }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        if (selectorProperties.enabled().value) {
            Surface(
                modifier = Modifier.size(size.width, size.height / rowCount),
                shape = selectorProperties.shape().value,
                color = selectorProperties.color().value,
                border = selectorProperties.border().value
            ) {}
        }
        Row {
            if (SHOW_DAYS.contains(durationFormat)) {
                //Days
                WheelTextPicker(
                    size = DpSize(
                        width = size.width / getDivisor(durationFormat),
                        height = size.height
                    ),
                    texts = days.map { it.text },
                    rowCount = rowCount,
                    style = textStyle,
                    color = textColor,
                    focusedIndex = days.indexOfFirst { it.value == snappedDuration.days }
                        .coerceAtLeast(0),
                    selectorProperties = WheelPickerDefaults.selectorProperties(
                        enabled = false
                    ),
                    onScrollFinished = { snappedIndex ->
                        val newDay = days.find { it.index == snappedIndex }?.value

                        newDay?.let {
                            snappedDuration =
                                snappedDuration.toComponents { days, hours, minutes, seconds, _ ->
                                    newDay.toDuration(DurationUnit.DAYS) +
                                            hours.toDuration(DurationUnit.HOURS) +
                                            minutes.toDuration(DurationUnit.MINUTES) +
                                            seconds.toDuration(DurationUnit.SECONDS)
                                }.coerceIn(minDuration, maxDuration)

                            val coercedDay = snappedDuration.days
                            val newIndex = days.find { it.value == coercedDay }?.index

                            newIndex?.let {
                                onSnappedDuration(
                                    SnappedDuration.Days(
                                        duration = snappedDuration,
                                        index = newIndex
                                    ),
                                    durationFormat
                                )?.let { return@WheelTextPicker it }
                            }
                        }

                        return@WheelTextPicker days.find { it.value == snappedDuration.days }?.index
                            ?: 0
                    }
                )
            }

            if (SHOW_HOURS.contains(durationFormat)) {
                //Hour
                WheelTextPicker(
                    size = DpSize(
                        width = size.width / getDivisor(durationFormat),
                        height = size.height
                    ),
                    texts = hours.map { it.text },
                    rowCount = rowCount,
                    style = textStyle,
                    color = textColor,
                    focusedIndex = hours.indexOfFirst { it.value == snappedDuration.hours }
                        .coerceAtLeast(0),
                    selectorProperties = WheelPickerDefaults.selectorProperties(
                        enabled = false
                    ),
                    onScrollFinished = { snappedIndex ->
                        val newHour = hours.find { it.index == snappedIndex }?.value

                        newHour?.let {
                            snappedDuration =
                                snappedDuration.toComponents { days, hours, minutes, seconds, _ ->
                                    days.toDuration(DurationUnit.DAYS) +
                                            newHour.toDuration(DurationUnit.HOURS) +
                                            minutes.toDuration(DurationUnit.MINUTES) +
                                            seconds.toDuration(DurationUnit.SECONDS)
                                }.coerceIn(minDuration, maxDuration)

                            val coercedHour = if (snappedDuration.days > 0) {
                                snappedDuration.hours.coerceAtMost(23)
                            } else {
                                snappedDuration.hours
                            }
                            val newIndex = hours.find { it.value == coercedHour }?.index

                            newIndex?.let {
                                onSnappedDuration(
                                    SnappedDuration.Hours(
                                        duration = snappedDuration,
                                        index = newIndex
                                    ),
                                    durationFormat
                                )?.let { return@WheelTextPicker it }
                            }
                        }

                        return@WheelTextPicker hours.find { it.value == snappedDuration.hours }?.index
                            ?: 0
                    }
                )
            }

            //Minute
            if (SHOW_MINUTES.contains(durationFormat)) {
                WheelTextPicker(
                    size = DpSize(
                        width = size.width / getDivisor(durationFormat),
                        height = size.height
                    ),
                    texts = minutes.map { it.text },
                    rowCount = rowCount,
                    style = textStyle,
                    color = textColor,
                    focusedIndex = minutes.indexOfFirst { it.value == snappedDuration.minutes }
                        .coerceAtLeast(0),
                    selectorProperties = WheelPickerDefaults.selectorProperties(
                        enabled = false
                    ),
                    onScrollFinished = { snappedIndex ->
                        val newMinute = minutes.find { it.index == snappedIndex }?.value

                        newMinute?.let {
                            snappedDuration =
                                snappedDuration.toComponents { days, hours, minutes, seconds, _ ->
                                    days.toDuration(DurationUnit.DAYS) +
                                            hours.toDuration(DurationUnit.HOURS) +
                                            newMinute.toDuration(DurationUnit.MINUTES) +
                                            seconds.toDuration(DurationUnit.SECONDS)
                                }.coerceIn(minDuration, maxDuration)

                            val coercedMinute = snappedDuration.minutes
                            val newIndex = minutes.find { it.value == coercedMinute }?.index

                            newIndex?.let {
                                onSnappedDuration(
                                    SnappedDuration.Minutes(
                                        duration = snappedDuration,
                                        index = newIndex
                                    ),
                                    durationFormat
                                )?.let { return@WheelTextPicker it }
                            }
                        }

                        return@WheelTextPicker minutes.find { it.value == snappedDuration.minutes }?.index
                    }
                )
            }

            //Second
            if (SHOW_SECONDS.contains(durationFormat)) {
                WheelTextPicker(
                    size = DpSize(
                        width = size.width / getDivisor(durationFormat),
                        height = size.height
                    ),
                    texts = seconds.map { it.text },
                    rowCount = rowCount,
                    style = textStyle,
                    color = textColor,
                    focusedIndex = seconds.indexOfFirst { it.value == snappedDuration.seconds }
                        .coerceAtLeast(0),
                    selectorProperties = WheelPickerDefaults.selectorProperties(
                        enabled = false
                    ),
                    onScrollFinished = { snappedIndex ->
                        val newSecond = seconds.find { it.index == snappedIndex }?.value

                        newSecond?.let {
                            snappedDuration =
                                snappedDuration.toComponents { days, hours, minutes, seconds, _ ->
                                    days.toDuration(DurationUnit.DAYS) +
                                            hours.toDuration(DurationUnit.HOURS) +
                                            minutes.toDuration(DurationUnit.MINUTES) +
                                            newSecond.toDuration(DurationUnit.SECONDS)
                                }.coerceIn(minDuration, maxDuration)

                            val coercedSecond = snappedDuration.seconds
                            val newIndex = seconds.find { it.value == coercedSecond }?.index

                            newIndex?.let {
                                onSnappedDuration(
                                    SnappedDuration.Seconds(
                                        duration = snappedDuration,
                                        index = newIndex
                                    ),
                                    durationFormat
                                )?.let { return@WheelTextPicker it }
                            }
                        }

                        return@WheelTextPicker seconds.find { it.value == snappedDuration.seconds }?.index
                    }
                )
            }
        }
        if (SHOW_HOURS.contains(durationFormat) && durationFormat != DurationFormat.HOURS) {
            // Add a colon between hours and minutes/seconds
            addColon(
                size = size,
                widthMultiplier = getWidthMultiplier(durationFormat, "hours"),
                textStyle = textStyle,
                textColor = textColor
            )
        }

        if (SHOW_MINUTES.contains(durationFormat) && durationFormat != DurationFormat.MINUTES) {
            // Add a colon between minutes and seconds
            addColon(
                size = size,
                widthMultiplier = getWidthMultiplier(durationFormat, "minutes"),
                textStyle = textStyle,
                textColor = textColor
            )
        }
    }
}

@Composable
fun BoxScope.addColon(
    size: DpSize,
    widthMultiplier: Float,
    textStyle: TextStyle,
    textColor: Color
) {
    Text(
        text = ":",
        style = textStyle,
        color = textColor,
        modifier = Modifier
            .size(size.width * widthMultiplier, size.height * 0.25f)
            .align(Alignment.CenterEnd)
    )
}

fun getWidthMultiplier(
    durationFormat: DurationFormat,
    unit: String
): Float {
    return when (durationFormat) {
        DurationFormat.DAYS_HOURS_MINUTES_SECONDS -> if (unit == "hours") 0.5f else 0.25f
        DurationFormat.HOURS_MINUTES_SECONDS -> if (unit == "hours") 1f / 3f else 2f / 3f
        DurationFormat.HOURS_MINUTES -> 0.5f
        DurationFormat.MINUTES_SECONDS -> 0.5f
        DurationFormat.HOURS -> 1f
        DurationFormat.MINUTES -> 1f
        DurationFormat.SECONDS -> 1f
    }
}

enum class DurationFormat {
    DAYS_HOURS_MINUTES_SECONDS,
    HOURS_MINUTES_SECONDS,
    HOURS_MINUTES,
    MINUTES_SECONDS,
    HOURS,
    MINUTES,
    SECONDS
}

val SHOW_DAYS = setOf(
    DurationFormat.DAYS_HOURS_MINUTES_SECONDS
)

val SHOW_HOURS = setOf(
    DurationFormat.DAYS_HOURS_MINUTES_SECONDS,
    DurationFormat.HOURS_MINUTES_SECONDS,
    DurationFormat.HOURS_MINUTES,
    DurationFormat.HOURS
)

val SHOW_MINUTES = setOf(
    DurationFormat.DAYS_HOURS_MINUTES_SECONDS,
    DurationFormat.HOURS_MINUTES_SECONDS,
    DurationFormat.HOURS_MINUTES,
    DurationFormat.MINUTES_SECONDS,
    DurationFormat.HOURS,
    DurationFormat.MINUTES
)

val SHOW_SECONDS = setOf(
    DurationFormat.DAYS_HOURS_MINUTES_SECONDS,
    DurationFormat.HOURS_MINUTES_SECONDS,
    DurationFormat.MINUTES_SECONDS,
    DurationFormat.SECONDS
)

private fun getDivisor(timeFormat: DurationFormat): Int {
    return when (timeFormat) {
        DurationFormat.DAYS_HOURS_MINUTES_SECONDS -> 4
        DurationFormat.HOURS_MINUTES_SECONDS -> 3
        DurationFormat.HOURS_MINUTES -> 2
        DurationFormat.MINUTES_SECONDS -> 2
        DurationFormat.HOURS -> 1
        DurationFormat.MINUTES -> 1
        DurationFormat.SECONDS -> 1
    }
}

private data class D(
    val text: String,
    val value: Int,
    val index: Int
)

private data class H(
    val text: String,
    val value: Int,
    val index: Int
)

private data class M(
    val text: String,
    val value: Int,
    val index: Int
)

private data class S(
    val text: String,
    val value: Int,
    val index: Int
)

val Duration.days: Int
    get() = this.toComponents { days, _, _, _, _ -> days.toInt() }

val Duration.maxDays: Int
    get() = this.toComponents { days, _, _, _, _ -> days.toInt() }

val Duration.minDays: Int
    get() = this.toComponents { days, _, _, _, _ -> days.toInt() }

val Duration.hours: Int
    get() = this.toComponents { _, hours, _, _, _ -> hours }

val Duration.maxHours: Int
    get() = this.toComponents { days, hours, _, _, _ -> if (days > 0) 24 else hours }

val Duration.minHours: Int
    get() = this.toComponents { days, hours, _, _, _ -> if (days > 0) 0 else hours }

val Duration.minutes: Int
    get() = this.toComponents { _, _, minutes, _, _ -> minutes }

val Duration.maxMinutes: Int
    get() = this.toComponents { days, hours, minutes, _, _ -> if (days > 0 || hours > 0) 59 else minutes }

val Duration.minMinutes: Int
    get() = this.toComponents { days, hours, minutes, _, _ -> if (days > 0 || hours > 0) 0 else minutes }

val Duration.seconds: Int
    get() = this.toComponents { _, _, _, seconds, _ -> seconds }

val Duration.maxSeconds: Int
    get() = this.toComponents { days, hours, minutes, seconds, _ -> if (days > 0 || hours > 0 || minutes > 0) 59 else seconds }

val Duration.minSeconds: Int
    get() = this.toComponents { days, hours, minutes, seconds, _ -> if (days > 0 || hours > 0 || minutes > 0) 0 else seconds }
