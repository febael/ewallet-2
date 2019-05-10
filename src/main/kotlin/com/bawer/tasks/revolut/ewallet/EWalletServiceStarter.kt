package com.bawer.tasks.revolut.ewallet

import com.google.inject.Guice


fun main() = PippoApplicaton( Guice.createInjector(EWalletServiceModule()) ).start()