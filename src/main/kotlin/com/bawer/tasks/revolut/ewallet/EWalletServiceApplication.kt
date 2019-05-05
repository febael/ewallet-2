package com.bawer.tasks.revolut.ewallet

import com.bawer.tasks.revolut.ewallet.controller.AccountController
import com.bawer.tasks.revolut.ewallet.controller.TransferController
import com.google.inject.Guice
import ro.pippo.controller.ControllerApplication
import ro.pippo.core.Pippo
import ro.pippo.gson.GsonEngine
import ro.pippo.undertow.UndertowServer

private val pippoApplication = object : ControllerApplication() {
    override fun onInit() {
        val injector = Guice.createInjector(GuiceModule())
        addControllers(
                injector.getInstance(AccountController::class.java),
                injector.getInstance(TransferController::class.java)
        )
        registerContentTypeEngine(GsonEngine::class.java)
    }
}

fun main() {
    val pippo = Pippo(pippoApplication)
    pippo.server = UndertowServer()
    pippo.start()
}