package com.dbi15.einsendeaufgabe.eventEmitter

import com.dbi15.einsendeaufgabe.repository.IpAddressRepository
import com.google.common.collect.Sets
import org.springframework.stereotype.Component
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

@Component
class IpAddressEmitter(private val ipAddressRepository: IpAddressRepository) : Runnable {
    private val emitters = Sets.newConcurrentHashSet<SseEmitter>()

    fun addEmitter(emitter: SseEmitter) = emitters.add(emitter)

    override fun run() {
        val lastIps = ipAddressRepository.getLast10Ips()
                .map { it["ip"] }
                .map { it.toString() }
                .map { it.slice(0 until it.length - 1) }
                .map { it + "X" }

        for (emitter in emitters) {
            try {
                val event = SseEmitter.event()
                        .name("last-visit-ips")
                        .data(lastIps)
                        .reconnectTime(120_000L)

                emitter.send(event)
            } catch (ex: Exception) {
                emitters.remove(emitter)
            }
        }
    }
}