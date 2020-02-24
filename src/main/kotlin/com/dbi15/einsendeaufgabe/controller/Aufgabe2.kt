package com.dbi15.einsendeaufgabe.controller;

import com.dbi15.einsendeaufgabe.eventEmitter.CounterIncreaseEmitter
import com.dbi15.einsendeaufgabe.eventEmitter.IpAddressEmitter
import com.google.common.util.concurrent.ThreadFactoryBuilder
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.util.concurrent.Executors


@Controller
@RequestMapping("/aufgabe2/events")
class Aufgabe2(
        private val counterIncreaseEmitter: CounterIncreaseEmitter,
        private val ipAddressEmitter: IpAddressEmitter) {

    private companion object Js {
        private const val VIEW_NAME = "js/SSEClient.js"

        private const val SSE_URL = "/aufgabe2/events/"
        private const val SSE_COUNTER_INCREASE_EVENT = "counter-increase"
        private const val SSE_LAST_VISIT_IPS_EVENT = "last-visit-ips"
    }

    private val eventExecutor = Executors.newSingleThreadExecutor(ThreadFactoryBuilder()
            .setNameFormat("sse-executor-%d")
            .build())

    @GetMapping("/")
    fun sseEvents(): SseEmitter {
        val newEmitter = SseEmitter(0L)

        counterIncreaseEmitter.addEmitter(newEmitter)
        ipAddressEmitter.addEmitter(newEmitter)

        eventExecutor.execute(counterIncreaseEmitter)
        eventExecutor.execute(ipAddressEmitter)

        return newEmitter
    }

    @GetMapping(
            value = ["/client.js"],
            produces = ["text/javascript"]
    )
    fun getSSEClientJs(model: Model): String {

        model["SSE_URL"] = SSE_URL
        model["SSE_COUNTER_INCREASE_EVENT"] = SSE_COUNTER_INCREASE_EVENT
        model["SSE_LAST_VISIT_IPS_EVENT"] = SSE_LAST_VISIT_IPS_EVENT
        model["FIELD_PREVIOUS_COUNTER_VALUE"] = Aufgabe1.FIELD_PREVIOUS_COUNTER_VALUE
        model["FIELD_COUNTER_VALUE"] = Aufgabe1.FIELD_COUNTER_VALUE

        return VIEW_NAME
    }
}
