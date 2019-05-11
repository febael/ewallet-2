package com.bawer.tasks.revolut.ewallet.test

import com.bawer.tasks.revolut.ewallet.PippoApplicaton
import com.bawer.tasks.revolut.ewallet.STATUSTEXT_FAILED
import com.bawer.tasks.revolut.ewallet.STATUSTEXT_NOT_FOUND
import com.bawer.tasks.revolut.ewallet.STATUSTEXT_OK
import com.bawer.tasks.revolut.ewallet.controller.AccountController
import com.bawer.tasks.revolut.ewallet.model.Account
import com.bawer.tasks.revolut.ewallet.model.Currency
import com.bawer.tasks.revolut.ewallet.model.Transfer
import com.bawer.tasks.revolut.ewallet.model.request.AccountRequest
import com.bawer.tasks.revolut.ewallet.service.AccountService
import io.ktor.http.HttpStatusCode
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import ro.pippo.core.Pippo


internal class AccountControllerTests : BaseControllerTests() {

    @Test
    fun `when all accounts are requested, then a list will be received`() {
        runBlocking { with ( GET("/accounts") ) {
            verify { mockedAccountService.getAll() }
            assertEquals(HttpStatusCode.OK, status)
            val apiResponse = getApiResponse<List<Account>>()
            assertEquals(STATUSTEXT_OK, apiResponse.status)
        } }
    }

    @Test
    fun `when an existing account is requested, then it will be received`() {
        runBlocking { with ( GET("/accounts/$CREATED_ACCOUNT_ID") ) {
            verify { mockedAccountService.get(CREATED_ACCOUNT_ID) }
            assertEquals(HttpStatusCode.OK, status)
            val apiResponse = getApiResponse<Account>()
            assertEquals(STATUSTEXT_OK, apiResponse.status)
        } }
    }

    @Test
    fun `when a missing account is requested, then error will be received`() {
        runBlocking { with ( GET("/accounts/$MISSING_ACCOUNT_ID") ) {
            verify { mockedAccountService.get(MISSING_ACCOUNT_ID) }
            assertEquals(HttpStatusCode.NotFound, status)
            val apiResponse = getApiResponse<Void>()
            assertEquals(STATUSTEXT_NOT_FOUND, apiResponse.status)
        } }
    }

    @Test
    fun `when transfers are requested, then a list will be received (even if account doesn't exist)`() {
        runBlocking { with ( GET("/accounts/2/transfers") ) {
            verify { mockedAccountService.getTransfers(any(), any(), any(), any()) }
            assertEquals(HttpStatusCode.OK, status)
            val apiResponse = getApiResponse<List<Transfer>>()
            assertEquals(STATUSTEXT_OK, apiResponse.status)
        } }
    }

    @Test
    fun `when a valid account creation is requested, then account will be created and location will be received`() {
        runBlocking { with ( POST("/accounts", accountRequest) ) {
            verify { mockedAccountService.create(any()) }
            assertEquals(HttpStatusCode.Created, status)
            assertNotNull(this.headers[LOCATION_HEADER])
            val apiResponse = getApiResponse<Account>()
            assertEquals(STATUSTEXT_OK, apiResponse.status)
        } }
    }

    @Test
    fun `when an invalid account creation is requested, then bad request is received`() {
        runBlocking { with ( POST("/accounts", ERRONEOUS_BODY) ) {
            verify(inverse = true) { mockedAccountService.create(any()) }
            assertEquals(HttpStatusCode.BadRequest, status)
            val apiResponse = getApiResponse<Void>()
            assertEquals(STATUSTEXT_FAILED, apiResponse.status)
        } }
    }

    companion object {

        private const val CREATED_ACCOUNT_ID = 1
        private const val MISSING_ACCOUNT_ID = Integer.MAX_VALUE
        private const val ERRONEOUS_BODY = "ERRONEOUS"
        private const val LOCATION_HEADER = "location"

        private lateinit var pippo: Pippo

        private val accountRequest = AccountRequest("Arvidas", "Testevicius", Currency.EUR)
        private val account = Account.from(accountRequest, CREATED_ACCOUNT_ID)

        // Mockking
        private val mockedAccountService = mockk<AccountService>().apply {
            every { create(any()) } returns account
            every { get(CREATED_ACCOUNT_ID) } returns account
            every { get(MISSING_ACCOUNT_ID) } returns null
            every { getAll() } returns emptyList()
            every { getTransfers(any(), any(), any(), any()) } returns emptyList()
        }

        @BeforeAll
        @JvmStatic fun setup() {
            pippo = Pippo(PippoApplicaton(AccountController(mockedAccountService)))
            pippo.start(TEST_PORT)
        }

        @AfterAll
        @JvmStatic fun tearDown() {
            pippo.stop()
        }
    }
}