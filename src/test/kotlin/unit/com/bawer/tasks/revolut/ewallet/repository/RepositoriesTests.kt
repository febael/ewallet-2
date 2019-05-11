package com.bawer.tasks.revolut.ewallet.repository

import com.bawer.tasks.revolut.ewallet.disruptor.TransferEvent
import com.bawer.tasks.revolut.ewallet.model.*
import com.bawer.tasks.revolut.ewallet.repository.cqengine.CQEngineAccountRepository
import com.bawer.tasks.revolut.ewallet.repository.cqengine.CQEngineTransferRepository
import com.bawer.tasks.revolut.ewallet.repository.redis.RedisAccountRepository
import com.bawer.tasks.revolut.ewallet.repository.redis.RedisWrapper
import com.bawer.tasks.revolut.ewallet.repository.redis.RedisTransferRepository
import com.bawer.tasks.revolut.ewallet.model.request.TransferRequest
import com.google.gson.Gson
import com.mifmif.common.regex.Generex
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.math.BigDecimal
import java.util.stream.Stream


@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RepositoriesTests {

    @ParameterizedTest
    @MethodSource("given it's empty, when provided with the first object")
    @Order(1)
    fun <T, I> `then repository should be able to save initial object`(
            repository: Repository<T, I>, objectToSave: T
    ) = assertTrue(repository.insert(objectToSave))

    @ParameterizedTest
    @MethodSource("given it holds some object, when provided with that object")
    @Order(2)
    fun <T, I> `then repository shouldn't let saving over existing objects`(
            repository: Repository<T, I>, objectToSave: T
    ) = assertFalse(repository.insert(objectToSave))

    @ParameterizedTest
    @MethodSource("given it holds some object, when provided with that object and its id")
    @Order(3)
    fun <T, I> `then repository should return the same object when queried by provided id`(
            repository: Repository<T, I>, expectedObject: T, expectedObjectId: I
    ) = assertTrue(repository.get(expectedObjectId) == expectedObject)

    @ParameterizedTest
    @MethodSource("given it holds some object, when provided with an id it doesn't hold")
    @Order(4)
    fun <T, I> `then repository should return null after querying by id`(
            repository: Repository<T, I>, expectedObjectId: I
    ) = assertNull(repository.get(expectedObjectId))

    @ParameterizedTest
    @MethodSource("given it holds some object, when provided with next two new objects")
    @Order(5)
    fun <T, I> `then repository should be able to save multiple objects at once and return correct size`(
            repository: Repository<T, I>, nextObject: T, nextNextObject: T, expectedSize: Int
    ) {
        assertTrue(repository.insertAll(nextObject, nextNextObject))
        assertEquals(expectedSize, repository.count())
    }

    companion object {

        @JvmStatic private val jedisPool by lazy { RedisWrapper.jedisPool }

        @AfterAll
        @JvmStatic fun tearDown() {
            RedisWrapper.tearDown()
        }

        private val nameGenerator = Generex("[A-Z][a-z]{2,8}")

        private val firstAccount = nextAccount()
        private val firstTransfer = nextTransfer()

        private var accountId = 0
        private var transferId = 0L

        private fun nextAccount() = Account(
                ++accountId,
                nameGenerator.random(),
                nameGenerator.random(),
                Currency.EUR
        )

        private fun nextTransfer() =
                Transfer.from(
                        TransferEvent().updateFrom(
                                TransferRequest(TransferType.DEPOSIT, targetId = 1, amount = BigDecimal.TEN).apply {
                                    id = ++transferId
                                }
                        ), TransferStatus.COMPLETED)

        private val gson by lazy { Gson() }

        private val repositoriesPerModel = arrayOf(
                arrayOf( // CQEngine
                        CQEngineAccountRepository(),
                        CQEngineTransferRepository()),
                arrayOf( // Redis
                        RedisAccountRepository(jedisPool!!, gson),
                        RedisTransferRepository(jedisPool!!, gson))
        )

        /**
         * Providers with given and when clauses
         */
        @JvmStatic private fun `given it's empty, when provided with the first object`() =
                createStreamOfArguments(firstAccount, firstTransfer)

        @JvmStatic private fun `given it holds some object, when provided with that object`() =
                createStreamOfArguments(firstAccount, firstTransfer)

        @JvmStatic private fun `given it holds some object, when provided with that object and its id`() =
                createStreamOfArguments(Pair(firstAccount, firstAccount.id), Pair(firstTransfer, firstTransfer.id))

        @JvmStatic private fun `given it holds some object, when provided with an id it doesn't hold`() =
                createStreamOfArguments(accountId + 1, transferId + 1)

        @JvmStatic private fun `given it holds some object, when provided with next two new objects`() =
                createStreamOfArguments(
                        Triple(nextAccount(), nextAccount(), accountId),
                        Triple(nextTransfer(), nextTransfer(), transferId.toInt())
                )

        private fun createStreamOfArguments(vararg parameters: Any): Stream<Arguments> {
            val tempList = mutableListOf<Pair<Any, Any>>()
            for (repositories in repositoriesPerModel) {
                if (parameters.size != repositories.size) RuntimeException("Invalid test setup")
                tempList.addAll(repositories.zip(parameters))
            }
            return tempList.map { Arguments.of(it.first, it.second) }.stream()
        }

        private fun createStreamOfArguments(vararg parameters: Pair<Any, Any>): Stream<Arguments> {
            val tempList = mutableListOf<Pair<Any, Pair<Any, Any>>>()
            for (repositories in repositoriesPerModel) {
                if (parameters.size != repositories.size) RuntimeException("Invalid test setup")
                tempList.addAll(repositories.zip(parameters))
            }
            return tempList.map { Arguments.of(it.first, it.second.first, it.second.second) }.stream()
        }

        private fun createStreamOfArguments(vararg parameters: Triple<Any, Any, Any>): Stream<Arguments> {
            val tempList = mutableListOf<Pair<Any, Triple<Any, Any, Any>>>()
            for (repositories in repositoriesPerModel) {
                if (parameters.size != repositories.size) RuntimeException("Invalid test setup")
                tempList.addAll(repositories.zip(parameters))
            }
            return tempList.map { Arguments.of(it.first, it.second.first, it.second.second, it.second.third) }.stream()
        }

    }
}