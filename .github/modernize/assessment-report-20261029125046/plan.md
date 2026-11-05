# Modernization Plan: Azure modernization for Liga Copa Peru

**Project**: liga-copa-peru

## Technical Framework

- **Language**: Java 21
- **Framework**: Spring Boot 4.1.2
- **Build Tool**: Maven Wrapper
- **Database**: PostgreSQL by default, SQL Server profile, H2 local profile
- **Key Dependencies**: Spring Data JPA, Spring Security, Flyway

## Overview

This modernization addresses the selected findings in assessment report
`report-20261029125046`. It prepares the application for Azure by securing its
database connections, moving user authentication to Microsoft Entra ID,
centralizing credentials, externalizing uploaded-file storage, and preparing
the application for container deployment. The application currently includes
separate PostgreSQL and SQL Server configurations, local file storage, and
form-based authentication.

The modernization will:

- Upgrade the runtime to Java 25 LTS while preserving application behavior.
- Enable Azure identity-based access for both configured database targets.
- Replace local authentication and credential handling with Azure identity
  and secret-management capabilities.
- Prepare persistence, uploaded-file storage, and container packaging for
  Azure operation.

Infrastructure provisioning and deployment are not included in this plan.

## Migration Impact Summary

| App | From | To | Authentication | Impact |
|---|---|---|---|---|
| Liga | Java 21 | Java 25 | — | Runtime |
| Liga | SQL Server | Azure SQL | Managed Identity | Database |
| Liga | PostgreSQL | Azure PostgreSQL | Managed Identity | Database |
| Liga | Local login | Entra ID | Entra ID | User auth |
| Liga | Config secrets | Key Vault | Managed Identity | Secrets |
| Liga | Local uploads | Azure File Share | Managed Identity | Storage |
| Liga | Jakarta JPA | Azure-compatible JPA | — | Persistence |
| Liga | No Dockerfile | Container image | — | Readiness |

## Assessment Scope

The plan covers the selected findings from `report-20261029125046`:

- Microsoft SQL database found
- Embedded framework - Spring Security
- PostgreSQL database found
- Password found in configuration file
- File system - Java NIO
- No Dockerfile found
- Detects usage of Jakarta Persistence (JPA) APIs
- CRA: Hard-coded password in Java source code
- Java Version is not the latest LTS

The SQL Server and PostgreSQL findings are treated as separate configured
database targets; this plan does not choose one or consolidate their profiles.
The existing Java 25 upgrade plan is not modified.

## Open Questions & Questionnaire

- [x] Q: Should the plan provision new Azure infrastructure? → A: No; use the
  repository's existing configuration and do not add provisioning tasks.
- [x] Q: Should integration tests be included, and in what mode? → A: Yes;
  use mock mode because no Azure test environment is configured.
- [x] Q: Should security scanning and CVE remediation be included? → A: Yes,
  using the workflow default.
- [x] Q: What deployment target should the plan use? → A: No deployment;
  the request selected migration findings and did not request deployment.
- [x] Q: Should containerization be included? → A: Yes; it is a selected
  assessment category.

## Verification

Verify the Java 25 build and existing unit tests, confirm the Azure identity
and secret-management changes, exercise the migrated services using the
selected mock integration-test mode, and confirm the application can be
packaged as a container. Complete dependency CVE remediation and re-verify
the build and unit tests. Deployment and Azure resource provisioning are
outside this plan.
