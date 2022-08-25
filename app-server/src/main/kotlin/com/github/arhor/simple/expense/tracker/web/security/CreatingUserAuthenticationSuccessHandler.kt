package com.github.arhor.simple.expense.tracker.web.security

import com.github.arhor.simple.expense.tracker.service.UserService
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler
import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class CreatingUserAuthenticationSuccessHandler(
    private val service: UserService,
) : SavedRequestAwareAuthenticationSuccessHandler() {

    override fun onAuthenticationSuccess(req: HttpServletRequest, res: HttpServletResponse, auth: Authentication) {
        service.createNewUserIfNecessary(auth)
        super.onAuthenticationSuccess(req, res, auth)
    }
}
