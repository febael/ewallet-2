package com.bawer.tasks.revolut.ewallet

import com.bawer.tasks.revolut.ewallet.controller.AccountController
import com.bawer.tasks.revolut.ewallet.controller.TransferController
import com.google.inject.Guice
import ro.pippo.core.Pippo


/**
 * TODO : automatic error-free registration of controllers
 */
fun main() = Guice.createInjector(EWalletServiceModule()).run {
    val pippo = Pippo( PippoApplicaton(
            getInstance(AccountController::class.java),
            getInstance(TransferController::class.java)
    ) )
    Runtime.getRuntime().addShutdownHook( Thread( Runnable {
        System.err.println("shutting down Pippo")
        pippo.stop()
        System.err.println("Pippo shut down")
    } ) )
    System.out.println("starting Pippo")
    pippo.start(DEFAULT_PIPPO_PORT)
}