package com.bawer.tasks.revolut.ewallet.service.impl

import com.bawer.tasks.revolut.ewallet.disruptor.TransferDisruptor
import com.bawer.tasks.revolut.ewallet.model.TransferStatus
import com.bawer.tasks.revolut.ewallet.model.request.TransferRequest
import com.bawer.tasks.revolut.ewallet.repository.TransferRepository
import com.bawer.tasks.revolut.ewallet.service.TransferService
import java.util.concurrent.atomic.AtomicLong
import javax.inject.Inject

class TransferServiceImpl @Inject constructor(
        private val repository: TransferRepository,
        private val disruptor: TransferDisruptor
) : TransferService {

    private val idGenerator = AtomicLong(0)

    /**
     * For keeping statuses of not-yet resolved or future transfers
     * TODO : Eviction not implemented
     */
    private val statusLookupTable = HashMap<Long, TransferRequest>(64 * 1024)

    override fun get(id: Long) = repository.get(id)

    override fun getStatus(id: Long) = repository.get(id)?.status ?: statusLookupTable[id]?.status

    /**
     * TODO : Not fully correct behaviour
     * Currently there are 2 sources of transfers, lookupTable for waiting transfers and repository for
     * completed ones. We ignore lookup table here
     */
    override fun getAll(status: TransferStatus?) = status?.let { repository.getAll(status) } ?: repository.getAll()

    override fun create(request: TransferRequest) =
            if (request.immediate) immediateAction(request) else deferredAction()

    private fun immediateAction(request: TransferRequest): Long {
        request.id = idGenerator.incrementAndGet()
        disruptor.submit(request)
        statusLookupTable[request.id] = request
        return request.id
    }

    private fun deferredAction(): Long = TODO("not implemented")

    override fun cancel(id: Long) = TODO("not implemented")
}