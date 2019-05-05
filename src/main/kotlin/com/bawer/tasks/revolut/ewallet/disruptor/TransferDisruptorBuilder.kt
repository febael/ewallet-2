package com.bawer.tasks.revolut.ewallet.disruptor

import LongIdGenerator
import com.bawer.tasks.revolut.ewallet.myLogger
import com.bawer.tasks.revolut.ewallet.request.TransferRequest
import com.lmax.disruptor.BusySpinWaitStrategy
import com.lmax.disruptor.EventFactory
import com.lmax.disruptor.EventTranslatorOneArg
import com.lmax.disruptor.WaitStrategy
import com.lmax.disruptor.dsl.ProducerType
import java.util.concurrent.ThreadFactory


object TransferDisruptorBuilder {

    private val DEFAULT_EVENT_FACTORY: EventFactory<TransferEvent> by lazy {
        EventFactory { TransferEvent() }
    }

    private val DEFAULT_THREAD_FACTORY: ThreadFactory by lazy {
        ThreadFactory { runnable -> Thread(runnable) }
    }

    private val DEFAULT_WAIT_STRATEGY: WaitStrategy by lazy { BusySpinWaitStrategy() }

    private val DEFAULT_PRODUCER_TYPE: ProducerType by lazy { ProducerType.MULTI }

    private const val DEFAULT_RING_BUFFER_SIZE = 64 * 1024

    fun buildDefault() = TransferDisruptor(
            DEFAULT_EVENT_FACTORY,
            DEFAULT_RING_BUFFER_SIZE,
            DEFAULT_THREAD_FACTORY,
            DEFAULT_PRODUCER_TYPE,
            DEFAULT_WAIT_STRATEGY)

    fun buildCustom(
            eventFactory: EventFactory<TransferEvent> = DEFAULT_EVENT_FACTORY,
            ringBufferSize: Int = DEFAULT_RING_BUFFER_SIZE,
            threadFactory: ThreadFactory = DEFAULT_THREAD_FACTORY,
            producerType: ProducerType = DEFAULT_PRODUCER_TYPE,
            waitStrategy: WaitStrategy = DEFAULT_WAIT_STRATEGY
    ) = TransferDisruptor(eventFactory, ringBufferSize, threadFactory, producerType, waitStrategy)
}