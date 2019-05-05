package com.bawer.tasks.revolut.ewallet

import org.slf4j.LoggerFactory

inline fun <reified T : Any> T.myLogger() =
        LoggerFactory.getLogger(this::class.java.name.substringBefore("\$Companion"))