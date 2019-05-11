package com.bawer.tasks.revolut.ewallet


/**
 * TODO : creating enums may be a better approach to const Strings (unless i18n)
 */
const val ERROR_NO_DETAILS = "no details"
const val ERROR_SOURCE_ID_REQUIRED = "sourceId is required for an internal transfer"
const val ERROR_SOURCE_EQUALS_TARGET = "why would you send money to yourself?"
const val ERROR_INVALID_AMOUNT = "amount is not within expected limits"
const val ERROR_INVALID_TARGET_DATE = "target date is not valid"

const val STATUSTEXT_OK = "OK"
const val STATUSTEXT_NOT_FOUND = "NOT_FOUND"
const val STATUSTEXT_NOT_CANCELLABLE = "NOT_CANCELLABLE"
const val STATUSTEXT_FAILED = "FAILED"

const val DEFAULT_PIPPO_PORT = 8080

const val STATUS_OK = 200
const val STATUS_CREATED = 201
const val STATUS_ACCEPTED = 202
const val STATUS_BAD_REQUEST = 400
const val STATUS_NOT_FOUND = 404
const val STATUS_INTERNAL_SERVER_ERROR = 500
