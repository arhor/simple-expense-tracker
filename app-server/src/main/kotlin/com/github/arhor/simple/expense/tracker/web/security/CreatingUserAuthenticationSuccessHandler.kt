package com.github.arhor.simple.expense.tracker.web.security

import com.github.arhor.simple.expense.tracker.service.UserService
import org.springframework.security.core.Authentication
import org.springframework.security.web.DefaultRedirectStrategy
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler
import org.springframework.stereotype.Component
import org.springframework.web.util.UriComponentsBuilder
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class CreatingUserAuthenticationSuccessHandler(
    private val service: UserService,
) : SavedRequestAwareAuthenticationSuccessHandler() {

    init {
        this.setUseReferer(true)
        this.redirectStrategy = object : DefaultRedirectStrategy() {
            override fun calculateRedirectUrl(contextPath: String?, url: String?): String {
                return super.calculateRedirectUrl(contextPath, url).let { path ->
                    if (path.endsWith("/sign-in")) {// TODO: use tha same constant as for WebSecurityConfig
                        path.let(UriComponentsBuilder::fromUriString)
                            .queryParam("success")
                            .build()
                            .toString()
                    } else {
                        path
                    }
                }
            }
        }
    }

    override fun onAuthenticationSuccess(req: HttpServletRequest, res: HttpServletResponse, auth: Authentication) {
        service.createNewUserIfNecessary(auth)
        super.onAuthenticationSuccess(req, res, auth)
    }
}
