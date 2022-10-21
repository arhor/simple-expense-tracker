package com.github.arhor.simple.expense.tracker.web.controller

import com.github.arhor.simple.expense.tracker.config.ConfigureLocalization
import com.github.arhor.simple.expense.tracker.config.ConfigureWebSecurity
import com.github.arhor.simple.expense.tracker.config.props.ApplicationProps
import com.github.arhor.simple.expense.tracker.service.UserService
import com.ninjasquad.springmockk.MockkBean
import org.assertj.core.api.SoftAssertions.assertSoftly
import org.junit.jupiter.api.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Import
import org.springframework.security.core.Authentication
import org.springframework.test.web.servlet.MockMvc
import java.util.function.Consumer

@Tag("contract")
@Import(
    value = [
        ConfigureWebSecurity::class,
        ConfigureLocalization::class,
    ]
)
@EnableConfigurationProperties(
    value = [
        ApplicationProps::class,
        OAuth2ClientProperties::class,
    ]
)
internal abstract class BaseControllerTest {

    @Autowired
    protected lateinit var http: MockMvc
        private set

    @MockkBean
    protected lateinit var userService: UserService
        private set

    @Autowired
    protected lateinit var applicationProps: ApplicationProps
        private set

    protected val authenticatedUser = Consumer<Authentication> {
        assertSoftly { soft ->
            soft.assertThat(it.isAuthenticated)
                .describedAs("authenticated")
                .isTrue
            soft.assertThat(it.name)
                .describedAs("authentication name")
                .isEqualTo("user")
        }
    }
}
