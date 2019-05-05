package com.bawer.tasks.revolut.ewallet.disruptor

import com.bawer.tasks.revolut.ewallet.request.TransferRequest
import com.lmax.disruptor.EventFactory
import com.lmax.disruptor.EventTranslatorOneArg
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

    private val translator = EventTranslatorOneArg<TransferEvent, TransferRequest> { event, _, request ->
        event.updateFrom(request)
    }

    fun submit(request: TransferRequest) = publishEvent(translator, request)
}