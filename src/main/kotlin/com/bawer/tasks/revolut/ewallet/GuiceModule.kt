package com.bawer.tasks.revolut.ewallet

import com.bawer.tasks.revolut.ewallet.disruptor.TransferDisruptor
import com.bawer.tasks.revolut.ewallet.disruptor.TransferDisruptorBuilder
import com.bawer.tasks.revolut.ewallet.repository.AccountRepository
import com.bawer.tasks.revolut.ewallet.repository.TransferRepository
import com.bawer.tasks.revolut.ewallet.repository.cqengine.CQEngineAccountRepository
import com.bawer.tasks.revolut.ewallet.repository.cqengine.CQEngineTransferRepository
import com.bawer.tasks.revolut.ewallet.service.AccountService
import com.bawer.tasks.revolut.ewallet.service.TransferService
import com.bawer.tasks.revolut.ewallet.service.impl.AccountServiceImpl
import com.bawer.tasks.revolut.ewallet.service.impl.TransferServiceImpl
import com.google.inject.Binder
import com.google.inject.Module

class GuiceModule : Module {

    override fun configure(binder: Binder) {
        binder.bind(AccountService::class.java).to(AccountServiceImpl::class.java).asEagerSingleton()
        binder.bind(TransferService::class.java).to(TransferServiceImpl::class.java).asEagerSingleton()
        binder.bind(TransferDisruptor::class.java).toInstance(TransferDisruptorBuilder.buildDefault())
        binder.bind(AccountRepository::class.java).to(CQEngineAccountRepository::class.java).asEagerSingleton()
        binder.bind(TransferRepository::class.java).to(CQEngineTransferRepository::class.java).asEagerSingleton()
    }
}