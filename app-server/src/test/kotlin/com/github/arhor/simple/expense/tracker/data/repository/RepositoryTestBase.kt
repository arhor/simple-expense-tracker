package com.github.arhor.simple.expense.tracker.data.repository

import com.github.arhor.simple.expense.tracker.config.DatabaseConfig
import com.github.arhor.simple.expense.tracker.data.model.Expense
import com.github.arhor.simple.expense.tracker.data.model.ExpenseItem
import com.github.arhor.simple.expense.tracker.data.model.InternalUser
import org.junit.jupiter.api.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*
import java.util.stream.Stream

@DataJdbcTest
@DirtiesContext
@Tag("integration")
@Testcontainers(disabledWithoutDocker = true)
@ContextConfiguration(classes = [DatabaseConfig::class])
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
internal abstract class RepositoryTestBase {

    @Autowired
    protected lateinit var expenseRepository: ExpenseRepository

    @Autowired
    protected lateinit var expenseItemRepository: ExpenseItemRepository

    @Autowired
    protected lateinit var userRepository: InternalUserRepository

    @Autowired
    protected lateinit var notificationRepository: NotificationRepository

    protected fun createTestUser(number: Number = 0): InternalUser {
        return InternalUser.builder()
            .username("test-user-username-$number")
            .password("test-user-password-$number")
            .currency("USD")
            .externalId(UUID.randomUUID().toString())
            .externalProvider("test")
            .build()
    }

    protected fun createTestExpense(userId: Long?, number: Number = 0) = Expense(
        userId = userId!!,
        name = "test-name-$number",
        icon = "test-icon-$number",
        color = "success",
    )

    protected fun createPersistedTestUser(number: Number = 0): InternalUser {
        return userRepository.save(createTestUser(number))
    }

    protected fun createPersistedTestExpense(userId: Long?, number: Number = 0): Expense {
        return expenseRepository.save(createTestExpense(userId, number))
    }

    protected fun createPersistedTestExpenseItems(
        number: Int,
        expenseId: Long?,
        currency: String?,
        amount: BigDecimal?,
        date: LocalDate?
    ): List<ExpenseItem> {
        val expenseItemsToCreate = Stream.generate {
            ExpenseItem.builder()
                .expenseId(expenseId)
                .currency(currency)
                .amount(amount)
                .date(date)
                .build()
        }
            .limit(number.toLong())
            .toList()
        val result = ArrayList<ExpenseItem>(number)
        for (expenseItem in expenseItemRepository.saveAll(expenseItemsToCreate)) {
            result.add(expenseItem)
        }
        return result
    }

    companion object {
        @JvmStatic
        @Container
        private val db = PostgreSQLContainer("postgres:12")

        @JvmStatic
        @DynamicPropertySource
        fun registerDynamicProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", db::getJdbcUrl)
            registry.add("spring.datasource.username", db::getUsername)
            registry.add("spring.datasource.password", db::getPassword)
        }
    }
}
