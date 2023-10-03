# 'app-schema' module description

This module defines the model used for the communication process between client and server. The main idea is to create a
single source of truth describing the data structures, also making it possible to track any issues caused by the data
structure updates at the build time rather than runtime.

There are two main gradle tasks used for code-generation:

- `generateJsonSchema2Pojo` - generates Java classes corresponding to the JSON-schema files
- `generateJsonSchema2TypeScript` - generates Typescript type definitions corresponding to the JSON-schema files

> **_NOTE:_** it is worth to mention that `build` task runs both of the tasks above, also compiling generated Java
> sources and packaging them into the JAR-file, so this module could be used as a simple dependency for the other
> JVM-based module.
