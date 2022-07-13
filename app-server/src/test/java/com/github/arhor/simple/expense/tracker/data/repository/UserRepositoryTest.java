package com.github.arhor.simple.expense.tracker.data.repository;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.github.arhor.simple.expense.tracker.config.DatabaseConfig;
import com.github.arhor.simple.expense.tracker.data.model.InternalUser;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("integration")
@DataJdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers(disabledWithoutDocker = true)
@ContextConfiguration(classes = DatabaseConfig.class)
class UserRepositoryTest {

    @Container
    private static final JdbcDatabaseContainer<?> db = new PostgreSQLContainer<>("postgres:12");

    @Autowired
    private UserRepository repository;

    @DynamicPropertySource
    static void registerDynamicProperties(final DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", db::getJdbcUrl);
        registry.add("spring.datasource.username", db::getUsername);
        registry.add("spring.datasource.password", db::getPassword);
    }

    //    @Test
    //    void should_softly_delete_account() {
    //        // given
    //        var user = new InternalUser();
    //
    //        var savedUser = repository.save(user);
    //        var userId = savedUser.getId();
    //
    //        // when
    //        repository.delete(savedUser);
    //        var resultById = repository.findById(userId);
    //        var deletedAccountsIds = repository.findDeletedIds();
    //
    //        // then
    //        assertThat(userId)
    //            .isNotNull();
    //        assertThat(resultById)
    //            .isEmpty();
    //        assertThat(deletedAccountsIds)
    //            .contains(userId);
    //    }

    //    @Test
    //    fun `should softly delete account by id`(@RandomParameter account: Account) {
    //        // given
    //        account.id = null
    //
    //        var savedAccount = repository.save(account)
    //        var accountId = savedAccount.id
    //
    //        // when
    //        repository.deleteById(accountId!!)
    //        var resultById = repository.findById(accountId)
    //        var deletedAccountsIds = repository.findDeletedIds().toList()
    //
    //        // then
    //        assertThat(accountId).isNotNull
    //        assertThat(resultById).isEmpty
    //        assertThat(deletedAccountsIds).contains(accountId)
    //    }
    //
    @Test
    void should_find_internal_user_by_username() {
        // given
        var user = new InternalUser();

        user.setUsername("username");
        user.setPassword("password");
        user.setCurrency("USD");

        var expectedUser = repository.save(user);

        // when
        var actualUser = repository.findByUsername(expectedUser.getUsername());

        // then
        assertThat(actualUser)
            .isNotEmpty()
            .contains(expectedUser);
    }

    //    @Test
    //    fun `should properly audit entity on save`(@RandomParameter account: Account) {
    //        // given
    //        account.id = null
    //
    //        // when
    //        var createdAccount = repository.save(account)
    //        var updatedAccount = repository.save(
    //            createdAccount.copy(email = "updated email").apply { copyBaseState(createdAccount) }
    //        )
    //
    //        // then
    //        assertThat(createdAccount.created)
    //            .isNotNull
    //        assertThat(createdAccount.updated)
    //            .isNull()
    //
    //        assertThat(updatedAccount.created)
    //            .isNotNull
    //            .isEqualTo(createdAccount.created)
    //        assertThat(updatedAccount.updated)
    //            .isNotNull
    //            .isAfter(updatedAccount.created)
    //    }
}
