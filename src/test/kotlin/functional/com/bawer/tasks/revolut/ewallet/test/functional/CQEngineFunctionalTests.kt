package com.bawer.tasks.revolut.ewallet.test.functional

import com.bawer.tasks.revolut.ewallet.repository.cqengine.CQEngineAccountRepository
import com.bawer.tasks.revolut.ewallet.repository.cqengine.CQEngineTransferRepository


object CQEngineFunctionalTests : BaseFunctionalTests() {

    override val accountRepository = CQEngineAccountRepository()
    override val transferRepository = CQEngineTransferRepository()
}