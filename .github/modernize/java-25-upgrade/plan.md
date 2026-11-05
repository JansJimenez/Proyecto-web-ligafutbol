# Modernization Plan: Java 25 LTS Runtime Upgrade

**Project**: liga-copa-peru

## Technical Framework

- **Language**: Java 21 (assessment and Maven configuration)
- **Framework**: Spring Boot 4.1.2 (retained)
- **Build Tool**: Maven Wrapper
- **Scope**: Java runtime only; no framework, cloud-readiness, or security remediation

## Overview

Upgrade the application's Java runtime from Java 21 to Java 25, the latest LTS
identified by the assessment. Preserve the existing application behavior and
Spring Boot framework version. Validate the upgraded project with its Maven
build and existing unit tests.

## Migration Impact Summary

| Application | Current runtime | Target runtime | Impact |
|---|---|---|---|
| liga-copa-peru | Java 21 | Java 25 LTS | Runtime upgrade only |

## Assessment Scope

This plan addresses only **“Java Version is not the latest LTS”**
(`azure-java-version-02000`). The finding is confirmed by the current
`pom.xml`, which sets `java.version` to `21`; the assessment lists Java 25
(`openjdk25`) as available. Other cloud-readiness and security findings in the
assessment are intentionally excluded.

## Verification

Confirm the project builds and its existing unit tests pass using Java 25.
Keep the Spring Boot 4.1.2 version and unrelated dependencies/configuration
outside the scope of this runtime-only plan.
