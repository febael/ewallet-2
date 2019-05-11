package com.bawer.tasks.revolut.ewallet.test

import com.bawer.tasks.revolut.ewallet.PippoApplicaton
import com.bawer.tasks.revolut.ewallet.STATUS_FAILED
import com.bawer.tasks.revolut.ewallet.STATUS_OK
import com.bawer.tasks.revolut.ewallet.controller.AccountController
import com.bawer.tasks.revolut.ewallet.model.Account
import com.bawer.tasks.revolut.ewallet.model.Currency
import com.bawer.tasks.revolut.ewallet.model.request.AccountRequest
import com.bawer.tasks.revolut.ewallet.service.AccountService
import io.ktor.http.HttpStatusCode
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test


internal class AccountControllerTests : BaseControllerTests() {

    @Test
    fun `when all accounts are requested, then a list will be received`() {
        runBlocking { with ( GET("/accounts") ) {
            verify { mockedAccountService.getAll() }
            assertEquals(status, HttpStatusCode.OK)
            val apiResponse = getApiResponse<List<Account>>()
            assertEquals(STATUS_OK, apiResponse.status)
        } }
    }

    @Test
    fun `when a valid account creation is requested, then account will be created`() {
        runBlocking { with ( POST("/accounts", accountRequest) ) {
            verify { mockedAccountService.create(any()) }
            assertEquals(status, HttpStatusCode.Created)
            val apiResponse = getApiResponse<Account>()
            assertEquals(STATUS_OK, apiResponse.status)
        } }
    }

    @Test
    fun `when an invalid account creation is requested, then InternalServerError is received`() {
        runBlocking { with ( POST("/accounts", ERRONEOUS_BODY) ) {
            assertEquals(status, HttpStatusCode.InternalServerError)
            val apiResponse = getApiResponse<Void>()
            assertEquals(STATUS_FAILED, apiResponse.status)
        } }
    }

    companion object {

        private const val CREATED_ACCOUNT_ID = 1
        private const val MISSING_ACCOUNT_ID = Integer.MAX_VALUE
        private const val ERRONEOUS_BODY = "ERRONEOUS"

        private val accountRequest = AccountRequest("Arvidas", "Testevicius", Currency.EUR)
        private val account = Account.from(accountRequest, CREATED_ACCOUNT_ID)

        // Mockking
        private val slot = slot<AccountRequest>()
        private val mockedAccountService = mockk<AccountService>().apply {
            every { create(any()) } returns account
            every { get(CREATED_ACCOUNT_ID) } returns account
            every { get(MISSING_ACCOUNT_ID) } returns null
            every { getAll() } returns emptyList()
        }

        @BeforeAll
        @JvmStatic fun setup() {
            PippoApplicaton(TEST_PORT, AccountController(mockedAccountService)).start()
        }
    }
}