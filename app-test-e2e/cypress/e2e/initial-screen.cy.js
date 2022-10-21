describe('initial screen', () => {
    it('should go to the sign-in page for the unauthenticated user', () => {
        // given
        cy.intercept('GET', '/api/users/current').as('currentUserRequest')

        // when
        cy.visit('/')

        //then
        cy.wait('@currentUserRequest').its('response.statusCode').should('equal', 401)
        cy.location('pathname').should('equal', '/sign-in')
    })
})
