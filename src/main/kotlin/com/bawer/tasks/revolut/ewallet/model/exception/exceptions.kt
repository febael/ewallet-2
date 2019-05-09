package com.bawer.tasks.revolut.ewallet.model.exception

import com.bawer.tasks.revolut.ewallet.ERROR_NO_DETAILS
import java.lang.RuntimeException



abstract class EWalletException(message: String = ERROR_NO_DETAILS, val status: Int) : RuntimeException(message)

class ResourceNotFoundException(message: String = ERROR_NO_DETAILS) : EWalletException(message, 404)

class InvalidRequestException(message: String = ERROR_NO_DETAILS, status: Int = 400) : EWalletException(message, status)

class NotImplementedException(message: String = ERROR_NO_DETAILS) : EWalletException(message, 501)

class InternalServerException(message: String = ERROR_NO_DETAILS, status: Int = 500) : EWalletException(message, status)