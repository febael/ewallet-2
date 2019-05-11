package com.bawer.tasks.revolut.ewallet.test

import com.bawer.tasks.revolut.ewallet.disruptor.TransferDisruptor
import com.bawer.tasks.revolut.ewallet.disruptor.TransferDisruptorBuilder
import com.bawer.tasks.revolut.ewallet.model.*
import com.bawer.tasks.revolut.ewallet.model.request.AccountRequest
import com.bawer.tasks.revolut.ewallet.model.request.TransferRequest
import com.bawer.tasks.revolut.ewallet.repository.AccountRepository
import com.bawer.tasks.revolut.ewallet.repository.TransferRepository
import com.bawer.tasks.revolut.ewallet.service.AccountService
import com.bawer.tasks.revolut.ewallet.service.TransferService
import com.bawer.tasks.revolut.ewallet.service.impl.AccountServiceImpl
import com.bawer.tasks.revolut.ewallet.service.impl.TransferServiceImpl
import com.mifmif.common.regex.Generex
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import java.math.BigDecimal


/**
 * Abstraction in order to enable multiple repo implementations and maybe in the future transaction-processing
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
internal abstract class BaseFunctionalTests {

    abstract val accountRepository: AccountRepository
    abstract val transferRepository: TransferRepository
    private lateinit var disruptor: TransferDisruptor

    private lateinit var accountService: AccountService
    private lateinit var transferService: TransferService

    @BeforeAll
    open fun setup() {
        disruptor = TransferDisruptorBuilder.buildDefault(accountRepository, transferRepository)
        accountService = AccountServiceImpl(this.accountRepository)
        transferService = TransferServiceImpl(transferRepository, disruptor)
        disruptor.start()
    }

    @AfterAll
    open fun tearDown() {
        disruptor.shutdown()
    }

    @Test
    @Order(1)
    fun `when a valid request arrives, then an account should be created`() {
        createAccountAndCheck(createAccountRequestObject())
    }

    @Test
    @Order(2)
    fun `when another valid request arrives, then another account should be created`() {
        createAccountAndCheck(createAccountRequestObject())
    }

    @Test
    @Order(3)
    fun `when a valid deposit request arrives, then a transferEvent should be submitted to the disruptor`() {
        createTransferAndCheck(depositFirst10)
    }

    @Test
    @Order(4)
    fun `given previous transfer request, when enough time passes, then transfer should be finalized successfully`() {
        checkTransferStatusCompleted(transferIds.last())
    }

    @Test
    @Order(5)
    fun `given a successful deposit, when account is requested, then correct balance should be seen`() {
        checkFirstAccountBalance(BigDecimal.TEN)
    }

    @Test
    @Order(6)
    fun `when a valid transfer request but with not enough balance arrives, then transfer should fail`() {
        createTransferAndCheck(transferFromSecondToFirstAccount10)
        checkTransferStatusFailed(transferIds.last())
    }

    @Test
    @Order(7)
    fun `when a valid withdraw request but with not enough balance arrives, then transfer should fail`() {
        createTransferAndCheck(withdrawFromSecond10)
        checkTransferStatusFailed(transferIds.last())
    }

    @Test
    @Order(8)
    fun `given previous 3 transfers, when accounts are requested, then only first account should have a transfer`() {
        val transfersOfFirst = getTransfersOfTheAccount(createdAccounts[0].id)
        assertEquals(1, transfersOfFirst.size)
        val onlyTransfer = transfersOfFirst[0]
        assertEquals(onlyTransfer.id, transferIds[0])
        val request = depositFirst10
        assertEquals(request.type, onlyTransfer.type)
        assertEquals(request.amount, onlyTransfer.amount)
        assertEquals(request.targetId, onlyTransfer.targetAccountId)
        val transfersOfSecond = getTransfersOfTheAccount(createdAccounts[1].id)
        assertEquals(0, transfersOfSecond.size)
    }

    private fun createTransferAndCheck(request: TransferRequest) {
        val transferId = transferService.create(request)
        val transferStatus = transferService.getStatus(transferId)
        assertNotNull(transferStatus)
        transferIds.add(transferId)
    }

    private fun createAccountAndCheck(request: AccountRequest) {
        val account = accountService.create(request)
        validateCreationRequest(account, request)
        createdAccounts.add(account)
    }

    private fun checkFirstAccountBalance(expectedBalance: BigDecimal) {
        val account = accountService.get(createdAccounts.first().id)
        assertNotNull(account)
        assertEquals(expectedBalance, account!!.balance)
    }

    private fun getTransfersOfTheAccount(id: Int): List<Transfer> {
        val account = accountService.get(id)
        assertNotNull(account)
        return account!!.transfers
    }

    private fun checkTransferStatusCompleted(id: Long) {
        repeat(30) {
            Thread.sleep(50)
            val transferStatus = transferService.getStatus(id)
            if (transferStatus == TransferStatus.COMPLETED) return
            assert(transferStatus != TransferStatus.FAILED)
        }
        fail<Void>("Couldn't observe status change in time")
    }

    private fun checkTransferStatusFailed(id: Long) {
        repeat(30) {
            Thread.sleep(50)
            val transferStatus = transferService.getStatus(id)
            if (transferStatus == TransferStatus.FAILED) return
            assert(transferStatus != TransferStatus.COMPLETED)
        }
        fail<Void>("Couldn't observe status change in time")
    }

    private fun validateCreationRequest(account: Account, request: AccountRequest) {
        assertEquals(account.holderName, request.holderName)
        assertEquals(account.holderSurname, request.holderSurname)
        assertEquals(account.currency, request.currency)
        assertEquals(account.balance, request.balance)
        assertEquals(0, account.transfers.size)
        val accountFromRepository = accountService.get(account.id)
        assertEquals(accountFromRepository, account)
    }

    companion object {

        private val createdAccounts = mutableListOf<Account>()

        private val transferIds = mutableListOf<Long>()

        private val nameGenerator = Generex("[A-Z][a-z]{2,8}")

        private fun createAccountRequestObject() = AccountRequest(
                holderName = nameGenerator.random(),
                holderSurname = nameGenerator.random(),
                currency = Currency.EUR)

        private val depositFirst10
            get() = if (createdAccounts.size > 0) {
                TransferRequest(TransferType.DEPOSIT, targetId = createdAccounts[0].id, amount = BigDecimal.TEN)
            } else throw Exception("Invalid test flow")

        private val withdrawFromSecond10
            get() = if (createdAccounts.size > 1) {
                TransferRequest(TransferType.WITHDRAW, targetId = createdAccounts[1].id, amount = BigDecimal.TEN)
            } else throw Exception("Invalid test flow")

        private val transferFromSecondToFirstAccount10
            get() = if (createdAccounts.size > 1) {
                TransferRequest(
                        TransferType.INTERNAL,
                        unvalidatedSourceId = createdAccounts[1].id,
                        targetId = createdAccounts[0].id,
                        amount = BigDecimal.TEN)
            } else throw Exception("Invalid test flow")
    }
}