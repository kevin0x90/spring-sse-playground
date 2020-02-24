package com.dbi15.einsendeaufgabe.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.Queue
import java.util.concurrent.ConcurrentLinkedQueue

@Configuration
class CounterQueueConfiguration {

    @Bean
    fun counterQueue(): Queue<Int> = ConcurrentLinkedQueue()
}