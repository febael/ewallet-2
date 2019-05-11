package com.bawer.tasks.revolut.ewallet


/**
 * TODO : creating enums may be a better approach to const Strings (unless i18n)
 */
const val ERROR_NO_DETAILS = "no details"
const val ERROR_SOURCE_ID_REQUIRED = "sourceId is required for an internal transfer"
const val ERROR_SOURCE_EQUALS_TARGET = "why would you send money to yourself?"
const val ERROR_INVALID_AMOUNT = "amount is not within expected limits"
const val ERROR_INVALID_TARGET_DATE = "target date is not valid"

const val STATUS_OK = "OK"
const val STATUS_NOT_FOUND = "NOT_FOUND"
const val STATUS_NOT_CANCELLABLE = "NOT_CANCELLABLE"
const val STATUS_FAILED = "FAILED"

const val DEFAULT_PIPPO_PORT = 8080