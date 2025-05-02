# auth-code-notification

### Overview

Service used to send email notifications when new auth code requested.

### Requirements

In order to run the service locally you will need the following:

- [Java 21](https://www.oracle.com/java/technologies/downloads/#java21)
- [Maven](https://maven.apache.org/download.cgi)
- [Git](https://git-scm.com/downloads)

### Getting started

To check out and build the service:

1. Clone [Docker CHS Development](https://github.com/companieshouse/docker-chs-development) and follow the steps in the README.
2. Run ./bin/chs-dev modules enable overseas-entities
3. Run ./bin/chs-dev development enable auth-code-notification (this will allow you to make changes).
4. Run docker using "chs-dev up" in the docker-chs-development directory.
5. Run "chs-dev status" to display running status of the enabled services - wait for auth-code-notification to become green.
6. The service should be accessible using this base url: http://api.chs.local:4001/

These instructions are for a local docker environment.

### Endpoints

| Method | Path                                          | Description                             |
|:-------|:----------------------------------------------|:----------------------------------------|
| GET    | /auth-code-notification/health       | Spring actuator health check endpoint   |
| POST   | /company/{companyNumber}/auth-code/send-email | Send email to user containing auth code |

## Terraform ECS

### What does this code do?

The code present in this repository is used to define and deploy a dockerised container in AWS ECS.
This is done by calling a [module](https://github.com/companieshouse/terraform-modules/tree/main/aws/ecs) from terraform-modules. Application specific attributes are injected and the service is then deployed using Terraform via the CICD platform 'Concourse'.

Application specific attributes | Value                                | Description
:---------|:-----------------------------------------------------------------------------|:-----------
**ECS Cluster**        |search-service                                     | ECS cluster (stack) the service belongs to
**Load balancer**      |chs-internalapi                                           | The load balancer that sits in front of the service
**Concourse pipeline**     |[Pipeline link](https://ci-platform.companieshouse.gov.uk/teams/team-development/pipelines/auth-code-notification) <br> [Pipeline code](https://github.com/companieshouse/ci-pipelines/blob/master/pipelines/ssplatform/team-development/auth-code-notification)                                  | Concourse pipeline link in shared services

### Contributing

- Please refer to the [ECS Development and Infrastructure Documentation](https://companieshouse.atlassian.net/wiki/spaces/DEVOPS/pages/4390649858/Copy+of+ECS+Development+and+Infrastructure+Documentation+Updated) for detailed information on the infrastructure being deployed.

### Testing

- Ensure the terraform runner local plan executes without issues. For information on terraform runners please see the [Terraform Runner Quickstart guide](https://companieshouse.atlassian.net/wiki/spaces/DEVOPS/pages/1694236886/Terraform+Runner+Quickstart).
- If you encounter any issues or have questions, reach out to the team on the **#platform** slack channel.

### Vault Configuration Updates

- Any secrets required for this service will be stored in Vault. For any updates to the Vault configuration, please consult with the **#platform** team and submit a workflow request.

### Useful Links

- [ECS service config dev repository](https://github.com/companieshouse/ecs-service-configs-dev)
- [ECS service config production repository](https://github.com/companieshouse/ecs-service-configs-production)

