package com.dbi15.einsendeaufgabe.eventEmitter

import com.google.common.collect.Sets
import org.springframework.stereotype.Component
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.util.Queue
import java.util.concurrent.atomic.AtomicInteger

@Component
class CounterIncreaseEmitter(private val counterQueue: Queue<Int>) : Runnable {
    private val lastCounterValue = AtomicInteger(0)
    private val emitters = Sets.newConcurrentHashSet<SseEmitter>()

    fun addEmitter(emitter: SseEmitter) = emitters.add(emitter)

    override fun run() {
        val newCounterValue = counterQueue.poll() ?: lastCounterValue.get()
        lastCounterValue.set(newCounterValue)

        for (emitter in emitters) {
            val event = SseEmitter.event()
                    .id("counter-id-${newCounterValue}")
                    .name("counter-increase")
                    .data(newCounterValue)
                    .reconnectTime(120_000L)

            try {
                emitter.send(event)
            } catch (ex: Exception) {
                emitters.remove(emitter)
            }
        }
    }
}