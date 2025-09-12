package com.mzdon.wheelpickercompose

import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.mzdon.wheelpickercompose.core.DefaultWheelDurationPicker
import com.mzdon.wheelpickercompose.core.DurationFormat
import com.mzdon.wheelpickercompose.core.SelectorProperties
import com.mzdon.wheelpickercompose.core.WheelPickerDefaults
import kotlin.time.Duration

@Composable
fun WheelDurationPicker(
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
    onSnappedDuration: (snappedDuration: Duration) -> Unit = {},
) {
    DefaultWheelDurationPicker(
        modifier,
        startDuration,
        minDuration,
        maxDuration,
        durationFormat,
        size,
        rowCount,
        textStyle,
        textColor,
        selectorProperties,
        onSnappedDuration = { snappedTime, _ ->
            onSnappedDuration(snappedTime.snappedDuration)
            snappedTime.snappedIndex
        }
    )
}
