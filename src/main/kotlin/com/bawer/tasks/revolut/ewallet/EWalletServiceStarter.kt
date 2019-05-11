package com.bawer.tasks.revolut.ewallet

import com.bawer.tasks.revolut.ewallet.controller.AccountController
import com.bawer.tasks.revolut.ewallet.controller.TransferController
import com.google.inject.Guice


/**
 * TODO : automatic error-free registration of controllers
 */
fun main() = Guice.createInjector(EWalletServiceModule()).run {
    PippoApplicaton(
            DEFAULT_PIPPO_PORT,
            getInstance(AccountController::class.java),
            getInstance(TransferController::class.java)
    ).start()
}