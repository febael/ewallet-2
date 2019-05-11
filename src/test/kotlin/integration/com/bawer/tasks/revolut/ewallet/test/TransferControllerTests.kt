package com.bawer.tasks.revolut.ewallet.test

import com.bawer.tasks.revolut.ewallet.*
import com.bawer.tasks.revolut.ewallet.controller.TransferController
import com.bawer.tasks.revolut.ewallet.model.Account
import com.bawer.tasks.revolut.ewallet.model.TransferStatus
import com.bawer.tasks.revolut.ewallet.model.TransferType
import com.bawer.tasks.revolut.ewallet.model.request.TransferRequest
import com.bawer.tasks.revolut.ewallet.model.response.TransferResponse
import com.bawer.tasks.revolut.ewallet.service.TransferService
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
import java.math.BigDecimal


internal class TransferControllerTests : BaseControllerTests() {

    @Test
    fun `when all transfers are requested, then a list will be received`() {
        runBlocking { with ( GET("/transfers") ) {
            verify { mockedTransferService.getAll() }
            assertEquals(HttpStatusCode.OK, status)
            val apiResponse = getApiResponse<List<Account>>()
            assertEquals(STATUSTEXT_OK, apiResponse.status)
        } }
    }

    @Test
    fun `when a valid transfer request arrives, then transfer will be put into buffer and location will be received`() {
        runBlocking { with ( POST("/transfers", transferRequest) ) {
            verify { mockedTransferService.create(any()) }
            assertEquals(HttpStatusCode.Accepted, status)
            assertNotNull(this.headers[LOCATION_HEADER])
            val apiResponse = getApiResponse<Void>()
            assertEquals(STATUSTEXT_OK, apiResponse.status)
        } }
    }

    @Test
    fun `when an invalid transfer request arrives, then bad request is received`() {
        runBlocking { with ( POST("/transfers", ERRONEOUS_BODY) ) {
            verify(inverse = true) { mockedTransferService.create(any()) }
            assertEquals(HttpStatusCode.BadRequest, status)
            val apiResponse = getApiResponse<Void>()
            assertEquals(STATUSTEXT_FAILED, apiResponse.status)
        } }
    }

    @Test
    fun `when status of an existing transfer is requested, then it will be received`() {
        runBlocking { with ( GET("/transfers/$CREATED_TRANSFER_ID/status") ) {
            verify { mockedTransferService.getStatus(CREATED_TRANSFER_ID) }
            assertEquals(HttpStatusCode.OK, status)
            val apiResponse = getApiResponse<TransferResponse>()
            assertEquals(STATUSTEXT_OK, apiResponse.status)
        } }
    }

    @Test
    fun `when status of an missing transfer is requested, then error will be received`() {
        runBlocking { with ( GET("/transfers/$MISSING_TRANSFER_ID/status") ) {
            verify { mockedTransferService.getStatus(MISSING_TRANSFER_ID) }
            assertEquals(HttpStatusCode.NotFound, status)
            val apiResponse = getApiResponse<Void>()
            assertEquals(STATUSTEXT_NOT_FOUND, apiResponse.status)
        } }
    }

    @Test
    fun `when a cancellable transfer is requested to be cancelled, then it will be cancelled`() {
        runBlocking { with ( DELETE("/transfers/$CANCELLABLE_TRANSFER_ID") ) {
            verify { mockedTransferService.cancel(CANCELLABLE_TRANSFER_ID) }
            assertEquals(HttpStatusCode.Accepted, status)
            val apiResponse = getApiResponse<TransferResponse>()
            assertEquals(STATUSTEXT_OK, apiResponse.status)
        } }
    }

    @Test
    fun `when a not-cancellable transfer is requested to be cancelled, then bad request error will be received`() {
        runBlocking { with ( DELETE("/transfers/$NOT_CANCELLABLE_TRANSFER_ID") ) {
            verify { mockedTransferService.cancel(NOT_CANCELLABLE_TRANSFER_ID) }
            assertEquals(HttpStatusCode.BadRequest, status)
            val apiResponse = getApiResponse<TransferStatus>()
            assertEquals(STATUSTEXT_NOT_CANCELLABLE, apiResponse.status)
        } }
    }

    @Test
    fun `when a missing transfer is requested to be cancelled, then not found error will be received`() {
        runBlocking { with ( DELETE("/transfers/$MISSING_TRANSFER_ID") ) {
            verify { mockedTransferService.cancel(MISSING_TRANSFER_ID) }
            assertEquals(HttpStatusCode.NotFound, status)
            val apiResponse = getApiResponse<Void>()
            assertEquals(STATUSTEXT_NOT_FOUND, apiResponse.status)
        } }
    }

    companion object {

        private const val CREATED_TRANSFER_ID = 1L
        private const val MISSING_TRANSFER_ID = Long.MAX_VALUE
        private const val CANCELLABLE_TRANSFER_ID = 2L
        private const val NOT_CANCELLABLE_TRANSFER_ID = Long.MAX_VALUE - 1
        private const val ERRONEOUS_BODY = "ERRONEOUS"
        private const val LOCATION_HEADER = "location"

        private lateinit var pippo: Pippo

        private val transferRequest = TransferRequest(TransferType.DEPOSIT, targetId = 1, amount = BigDecimal.TEN)

        // Mockking
        private val mockedTransferService = mockk<TransferService>().apply {
            every { create(any()) } returns CREATED_TRANSFER_ID
            every { getAll() } returns emptyList()
            every { getStatus(CREATED_TRANSFER_ID) } returns TransferStatus.COMPLETED
            every { getStatus(MISSING_TRANSFER_ID) } returns null
            every { cancel(CANCELLABLE_TRANSFER_ID) } returns TransferStatus.CANCELLED
            every { cancel(NOT_CANCELLABLE_TRANSFER_ID) } returns TransferStatus.COMPLETED
            every { cancel(MISSING_TRANSFER_ID) } returns null
        }

        @BeforeAll
        @JvmStatic fun setup() {
            pippo = Pippo(PippoApplicaton(TransferController(mockedTransferService)))
            pippo.start(TEST_PORT)
        }

        @AfterAll
        @JvmStatic fun tearDown() {
            pippo.stop()
        }
    }
}