# 'app-client' module description

This module contains client-side application.

## Tasks to use during local development

The following commands should be run from `app-client` directory:

- `../gradlew :apps:server:bootRun` - starts back-end server in the dev mode
- `npm run serve`                  - starts front-end server in the dev mode

## State rules for the client-app

- component can have only state related to the UI: open/closed modal, form fields, etc.
- all state related to the domain model should be placed in mobx-stores
