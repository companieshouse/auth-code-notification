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
| Method | Path                                                  | Description                             |
|:-------|:------------------------------------------------------|:----------------------------------------|
| POST   | internal/company/{companyNumber}/auth-code/send-email | Send email to user containing auth code |