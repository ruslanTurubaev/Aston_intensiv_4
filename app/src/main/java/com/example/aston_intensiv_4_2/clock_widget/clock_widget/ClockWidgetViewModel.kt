package com.example.aston_intensiv_4_2.clock_widget.clock_widget

import com.example.aston_intensiv_4_2.clock_widget.interfaces.Model
import com.example.aston_intensiv_4_2.clock_widget.support_class.ColorResource

class ClockWidgetViewModel(
    val secondsPointerRadius : Float,
    val minutesPointerRadius : Float,
    val hoursPointerRadius : Float,
    val secondsPointerColor : ColorResource,
    val minutesPointerColor : ColorResource,
    val hoursPointerColor : ColorResource,
) : Model {
}