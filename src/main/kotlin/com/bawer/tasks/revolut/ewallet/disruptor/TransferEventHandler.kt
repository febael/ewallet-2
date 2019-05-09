package com.bawer.tasks.revolut.ewallet.disruptor

import com.bawer.tasks.revolut.ewallet.model.Transfer
import com.bawer.tasks.revolut.ewallet.model.TransferStatus.PROCESSING
import com.bawer.tasks.revolut.ewallet.model.TransferType
import com.bawer.tasks.revolut.ewallet.repository.AccountRepository
import com.bawer.tasks.revolut.ewallet.repository.TransferRepository
import com.lmax.disruptor.EventHandler
import javax.inject.Inject


internal object TransferEventHandler : EventHandler<TransferEvent> {

    @Inject
    private lateinit var accountRepository: AccountRepository

    @Inject
    private lateinit var transferRepository: TransferRepository

    override fun onEvent(event: TransferEvent, sequence: Long, endOfBatch: Boolean) = when(event.request.type) {
        TransferType.WITHDRAW -> withdraw(event)
        TransferType.DEPOSIT -> deposit(event)
        TransferType.INTERNAL -> internalTransfer(event)
    }

    /**
     * If this keeps the system in fully consistent state depends on implementing class
     * (Note that concurrency issues are handled by lmax disruptor)
     */
    private fun internalTransfer(event: TransferEvent) {
        with(event.request) {
            val transfer = Transfer.from(event, PROCESSING)
            val (from, to) = accountRepository.getTwo(sourceId!!, targetId)
            if (from == null || to == null || from.balance < amount || from.currency != to.currency) {
                transfer.setFailedStatus()
            } else {
                from.balance -= amount
                to.balance += amount
                if (accountRepository.upsertAll(from, to)) transfer.setCompletedStatus()
                else transfer.setFailedStatus()
            }
            transferRepository.insert(transfer)
        }
    }

    private fun withdraw(event: TransferEvent) {
        with (event.request) {
            val transfer = Transfer.from(event, PROCESSING)
            accountRepository.get(targetId)?.takeIf { it.balance < amount }?.let { account ->
                account.balance -= amount
                account.transfers.add(transfer)
                if (accountRepository.upsert(account)) transfer.setCompletedStatus()
                else transfer.setFailedStatus()
            } ?: transfer.setFailedStatus()
            transferRepository.insert(transfer)
        }
    }

    private fun deposit(event: TransferEvent) {
        with(event.request) {
            val transfer = Transfer.from(event, PROCESSING)
            accountRepository.get(targetId)?.let { account ->
                account.balance += amount
                account.transfers.add(transfer)
                if (accountRepository.upsert(account)) transfer.setCompletedStatus()
                else transfer.setFailedStatus()
            } ?: transfer.setFailedStatus()
            transferRepository.insert(transfer)
        }
    }
}