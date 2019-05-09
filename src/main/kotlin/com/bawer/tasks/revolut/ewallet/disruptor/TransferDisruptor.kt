package com.bawer.tasks.revolut.ewallet.disruptor

import com.bawer.tasks.revolut.ewallet.model.request.TransferRequest
import com.bawer.tasks.revolut.ewallet.myLogger
import com.lmax.disruptor.EventFactory
import com.lmax.disruptor.EventTranslatorOneArg
import com.lmax.disruptor.RingBuffer
import com.lmax.disruptor.WaitStrategy
import com.lmax.disruptor.dsl.Disruptor
import com.lmax.disruptor.dsl.ProducerType
import java.util.concurrent.ThreadFactory


class TransferDisruptor(
        eventFactory: EventFactory<TransferEvent>,
        ringBufferSize: Int,
        threadFactory: ThreadFactory,
        producerType: ProducerType,
        waitStrategy: WaitStrategy
) : Disruptor<TransferEvent>(eventFactory, ringBufferSize, threadFactory, producerType, waitStrategy) {

    init {
        handleEventsWith(TransferEventHandler)
    }

    private val translator = EventTranslatorOneArg<TransferEvent, TransferRequest> { event, _, request ->
        event.updateFrom(request)
    }

    fun submit(request: TransferRequest) = publishEvent(translator, request)

    override fun start(): RingBuffer<TransferEvent> {
        logger.info("Starting to disrupt the finance world")
        return super.start()
    }

    companion object {
        private val logger = myLogger()
    }
}