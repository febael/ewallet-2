package com.bawer.tasks.revolut.ewallet

import com.bawer.tasks.revolut.ewallet.controller.AccountController
import com.bawer.tasks.revolut.ewallet.controller.TransferController
import com.bawer.tasks.revolut.ewallet.model.exception.EWalletException
import com.bawer.tasks.revolut.ewallet.model.response.ApiResponse
import com.google.inject.Guice
import com.google.inject.Injector
import ro.pippo.controller.ControllerApplication
import ro.pippo.core.Pippo
import ro.pippo.core.route.RouteContext
import ro.pippo.gson.GsonEngine
import ro.pippo.undertow.UndertowServer


/**
 * TODO : automatic error-free registration of controllers
 */
class PippoApplicaton(private val injector: Injector) : ControllerApplication() {

    override fun onInit() {
        addControllers(
                injector.getInstance(AccountController::class.java),
                injector.getInstance(TransferController::class.java)
        )
        registerContentTypeEngine(GsonEngine::class.java)
        errorHandler.run {
            setExceptionHandler(EWalletException::class.java) { exception: Exception, context: RouteContext ->
                exception as EWalletException
                context.status(exception.status)
                context.send(ApiResponse.failed(exception.message))
            }
            setExceptionHandler(Exception::class.java) { exception: Exception, context: RouteContext ->
                context.status(500)
                context.send(ApiResponse.failed(exception.message))
            }
        }
    }

    internal fun start() {
        val pippo = Pippo(this)
        pippo.server = UndertowServer()
        pippo.start()
    }
}