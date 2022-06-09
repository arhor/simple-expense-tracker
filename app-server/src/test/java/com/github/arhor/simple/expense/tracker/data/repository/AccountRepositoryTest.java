//package com.github.arhor.simple.expense.tracker.data.repository;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.test.context.DynamicPropertyRegistry;
//import org.springframework.test.context.DynamicPropertySource;
//
//import com.github.arhor.simple.expense.tracker.data.model.InternalUser;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//class AccountRepositoryTest extends DatabaseIntegrationTest {
//
//    @Autowired
//    private UserRepository repository;
//
//    @DynamicPropertySource
//    static void registerProps(final DynamicPropertyRegistry registry) {
//        registerDynamicProps(registry);
//    }
//
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
//
//    //    @Test
//    //    fun `should softly delete account by id`(@RandomParameter account: Account) {
//    //        // given
//    //        account.id = null
//    //
//    //        var savedAccount = repository.save(account)
//    //        var accountId = savedAccount.id
//    //
//    //        // when
//    //        repository.deleteById(accountId!!)
//    //        var resultById = repository.findById(accountId)
//    //        var deletedAccountsIds = repository.findDeletedIds().toList()
//    //
//    //        // then
//    //        assertThat(accountId).isNotNull
//    //        assertThat(resultById).isEmpty
//    //        assertThat(deletedAccountsIds).contains(accountId)
//    //    }
//    //
//    //    @Test
//    //    fun `should find account by username`(@RandomParameter account: Account) {
//    //        // given
//    //        account.id = null
//    //
//    //        var savedAccount = repository.save(account)
//    //
//    //        // when
//    //        var optionalAccount = repository.findByUsername(savedAccount.username!!)
//    //
//    //        // then
//    //        assertThat(optionalAccount?.id)
//    //            .isNotNull
//    //            .isEqualTo(savedAccount.id)
//    //    }
//    //
//    //    @Test
//    //    fun `should properly audit entity on save`(@RandomParameter account: Account) {
//    //        // given
//    //        account.id = null
//    //
//    //        // when
//    //        var createdAccount = repository.save(account)
//    //        var updatedAccount = repository.save(
//    //            createdAccount.copy(email = "updated email").apply { copyBaseState(createdAccount) }
//    //        )
//    //
//    //        // then
//    //        assertThat(createdAccount.created)
//    //            .isNotNull
//    //        assertThat(createdAccount.updated)
//    //            .isNull()
//    //
//    //        assertThat(updatedAccount.created)
//    //            .isNotNull
//    //            .isEqualTo(createdAccount.created)
//    //        assertThat(updatedAccount.updated)
//    //            .isNotNull
//    //            .isAfter(updatedAccount.created)
//    //    }
//}
