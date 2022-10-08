package com.github.arhor.simple.expense.tracker.web.controller

import com.github.arhor.simple.expense.tracker.config.ConfigureLocalization
import com.github.arhor.simple.expense.tracker.config.ConfigureWebSecurity
import com.github.arhor.simple.expense.tracker.config.props.ApplicationProps
import com.ninjasquad.springmockk.MockkBean
import org.assertj.core.api.SoftAssertions.assertSoftly
import org.junit.jupiter.api.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Import
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.test.web.servlet.MockMvc
import java.util.function.Consumer

@Tag("contract")
@EnableConfigurationProperties(ApplicationProps::class)
@Import(ConfigureWebSecurity::class, ConfigureLocalization::class)
internal abstract class BaseControllerTest {

    @Autowired
    protected lateinit var http: MockMvc

    @MockkBean
    protected lateinit var authenticationSuccessHandler: AuthenticationSuccessHandler

    @MockkBean
    protected lateinit var userDetailsService: UserDetailsService

    @MockkBean
    protected lateinit var passwordEncoder: PasswordEncoder

    @MockkBean
    protected lateinit var clientRegistrationRepository: ClientRegistrationRepository

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
