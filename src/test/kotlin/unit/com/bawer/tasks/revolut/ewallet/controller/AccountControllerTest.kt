package com.bawer.tasks.revolut.ewallet.controller

import com.bawer.tasks.revolut.ewallet.service.AccountService
import io.mockk.mockk

class AccountControllerTest {

    private val service: AccountService = mockk()
    private val controller = AccountController(service)

}