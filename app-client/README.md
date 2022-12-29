# 'app-client' module description

This module contains client-side application.

## State rules for the client-app

- component can have only state related to the UI: open/closed modal, form fields, etc.
- all state related to the domain model should be placed in mobx-stores
