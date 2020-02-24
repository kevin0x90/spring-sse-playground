package com.dbi15.einsendeaufgabe.controller

import com.dbi15.einsendeaufgabe.repository.IpAddressRepository
import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Queue
import javax.servlet.http.HttpServletRequest

@Controller
@RequestMapping("/aufgabe1")
class Aufgabe1(
        private val ipAddressRepository: IpAddressRepository,
        private val counterQueue: Queue<Int>) {

    companion object {
        private const val VIEW_NAME = "html/Aufgabe1"

        private const val ACTION_INCREMENT_COUNTER = "increment_counter"
        private const val ACTION_DISPLAY_CURRENT_TIME = "display_current_time"

        private const val FIELD_CURRENT_TIME = "current_time"
        private const val FIELD_PREVIOUS_TIME_VALUE = "previous_time_value"

        const val FIELD_COUNTER_VALUE = "counter_value"
        const val FIELD_PREVIOUS_COUNTER_VALUE = "previous_counter_value"
    }

    @PostMapping(
            consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE],
            produces = [MediaType.TEXT_HTML_VALUE],
            params = [ACTION_INCREMENT_COUNTER])
    fun incrementCounter(
            @RequestParam(name = FIELD_PREVIOUS_COUNTER_VALUE, defaultValue = "0") previousCounterValue: Int,
            @RequestParam(name = FIELD_PREVIOUS_TIME_VALUE, required = false) previousTimeValueString: String?,
            model: Model): String {

        val newCounterValue = previousCounterValue + 1
        if (previousTimeValueString != null) {
            model[FIELD_CURRENT_TIME] = LocalTime.parse(previousTimeValueString)
        }
        model[FIELD_COUNTER_VALUE] = newCounterValue
        counterQueue.add(newCounterValue)

        return VIEW_NAME
    }

    @PostMapping(
            consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE],
            produces = [MediaType.TEXT_HTML_VALUE],
            params = [ACTION_DISPLAY_CURRENT_TIME]
    )
    fun displayCurrentTime(
            @RequestParam(name = FIELD_PREVIOUS_COUNTER_VALUE, defaultValue = "0") previousCounterValue: Int,
            model: Model): String {

        model[FIELD_CURRENT_TIME] = LocalTime.now().format(DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM))
        model[FIELD_COUNTER_VALUE] = previousCounterValue

        return VIEW_NAME
    }

    @GetMapping(
            produces = [MediaType.TEXT_HTML_VALUE])
    fun getHomeView(model: Model, request: HttpServletRequest): String {
        model[FIELD_COUNTER_VALUE] = 0

        ipAddressRepository.addIpIfNotExists(request.remoteAddr)

        return VIEW_NAME
    }
}