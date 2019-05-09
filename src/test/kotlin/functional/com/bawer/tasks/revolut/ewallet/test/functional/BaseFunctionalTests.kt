package com.bawer.tasks.revolut.ewallet.test.functional

import com.bawer.tasks.revolut.ewallet.disruptor.TransferDisruptor
import com.bawer.tasks.revolut.ewallet.disruptor.TransferDisruptorBuilder
import com.bawer.tasks.revolut.ewallet.model.Account
import com.bawer.tasks.revolut.ewallet.model.Currency
import com.bawer.tasks.revolut.ewallet.model.TransferStatus
import com.bawer.tasks.revolut.ewallet.model.TransferType
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
abstract class BaseFunctionalTests {

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
        val request = createAnAccount()
        val account = accountService.create(request)
        validateCreationRequest(account, request)
        createdAccounts.add(account)
    }

    @Test
    @Order(2)
    fun `when another valid request arrives, then another account should be created`() {
        val request = createAnAccount()
        val account = accountService.create(request)
        validateCreationRequest(account, request)
        createdAccounts.add(account)
    }

    @Test
    @Order(3)
    fun `when a valid request arrives, then a transferEvent should be submitted to the disruptor`() {
        val transferId = transferService.create(depositFirstCreatedAccountBy10)
        val transferStatus = transferService.getStatus(transferId)
        assertNotNull(transferStatus)
        transferIds.add(transferId)
    }

    @Test
    @Order(4)
    fun `given previous transfer request, when enough time passes, then transfer should be finalized successfully`() {
        val id = transferIds.last()
        repeat(30) {
            Thread.sleep(50)
            val transferStatus = transferService.getStatus(id)
            if (transferStatus == TransferStatus.COMPLETED) return
            assert(transferStatus != TransferStatus.FAILED)
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

        private fun createAnAccount() = AccountRequest(
                holderName = nameGenerator.random(),
                holderSurname = nameGenerator.random(),
                currency = Currency.EUR)

        private val depositFirstCreatedAccountBy10
            get() = if (createdAccounts.size > 1) {
                TransferRequest(TransferType.DEPOSIT, targetId = createdAccounts[0].id, amount = BigDecimal.TEN)
            } else throw Exception("Invalid test flow")
    }
}