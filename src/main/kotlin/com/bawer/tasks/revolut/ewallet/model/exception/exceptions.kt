package com.bawer.tasks.revolut.ewallet.model.exception

import java.lang.RuntimeException


private const val NO_MESSAGE = "No details given"

abstract class EWalletException(message: String = NO_MESSAGE, val status: Int) : RuntimeException(message)

class ResourceNotFoundException(message: String = NO_MESSAGE) : EWalletException(message, 404)

class InvalidRequestException(message: String = NO_MESSAGE, status: Int = 400) : EWalletException(message, status)

class NotImplementedException(message: String = NO_MESSAGE) : EWalletException(message, 501)

class InternalServerException(message: String = NO_MESSAGE, status: Int = 500) : EWalletException(message, status)