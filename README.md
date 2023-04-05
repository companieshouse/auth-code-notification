# auth-code-notification

### Overview
Service used to send email notifications when new auth code requested.

### Requirements
In order to run the service locally you will need the following:
- [Java 11](https://www.oracle.com/java/technologies/downloads/#java11)
- [Maven](https://maven.apache.org/download.cgi)
- [Git](https://git-scm.com/downloads)

### Getting started
To check out and build the service:
1. Clone [Docker CHS Development](https://github.com/companieshouse/docker-chs-development) and follow the steps in the README.
2. Run ./bin/chs-dev modules enable overseas-entities
3. Run ./bin/chs-dev development enable auth-code-notification (this will allow you to make changes).
4. Run docker using "tilt up" in the docker-chs-development directory.
5. Use space-bar in the command line to open tilt window - wait for auth-code-notification to become green.
7. The service should be accessible using this url: http://api.chs.local/auth-code-notification

These instructions are for a local docker environment.

### Endpoints
| Method | Path                                    | Description                           |
|:-------|:----------------------------------------|:--------------------------------------|
| GET    | /auth-code-notification/actuator/health | Spring actuator health check endpoint |