artifact_name       := auth-code-notification
version             := "unversioned"

dependency_check_base_suppressions:=common_suppressions_spring_6.xml

# dependency_check_suppressions_repo_branch
# The branch of the dependency-check-suppressions repository to use
# as the source of the suppressions file.
# This should point to "main" branch when being used for release,
# but can point to a different branch for experimentation/development.
dependency_check_suppressions_repo_branch:=main

dependency_check_minimum_cvss := 4
dependency_check_assembly_analyzer_enabled := false
dependency_check_suppressions_repo_url:=git@github.com:companieshouse/dependency-check-suppressions.git
suppressions_file := target/suppressions.xml

.PHONY: all
all: build

.PHONY: clean
clean:
	mvn clean
	rm -f ./$(artifact_name).jar
	rm -f ./$(artifact_name)-*.zip
	rm -rf ./build-*
	rm -f ./build.log

.PHONY: build
build:
	mvn versions:set -DnewVersion=$(version) -DgenerateBackupPoms=false
	mvn package -DskipTests=true
	cp ./target/$(artifact_name)-$(version).jar ./$(artifact_name).jar

.PHONY: test
test: test-unit

.PHONY: test-unit
test-unit: clean
	mvn test

.PHONY: package
package:
ifndef version
	$(error No version given. Aborting)
endif
	$(info Packaging version: $(version))
	mvn versions:set -DnewVersion=$(version) -DgenerateBackupPoms=false
	mvn package -DskipTests=true
	$(eval tmpdir:=$(shell mktemp -d build-XXXXXXXXXX))
	cp ./target/$(artifact_name)-$(version).jar $(tmpdir)/$(artifact_name).jar
	cd $(tmpdir); zip -r ../$(artifact_name)-$(version).zip *
	rm -rf $(tmpdir)

.PHONY: dist
dist: clean build package

.PHONY: sonar
sonar:
	mvn sonar:sonar

.PHONY: sonar-pr-analysis
sonar-pr-analysis:
	mvn sonar:sonar -P sonar-pr-analysis

.PHONY: dependency-check
dependency-check: build package
	mvn install -DskipTests
	dependency-check-runner --repo-name=auth-code-notification

.PHONY: dependency-check-local
dependency-check-local: build package
	mvn install -DskipTests
	docker run --rm -e DEPENDENCY_CHECK_SUPPRESSIONS_HOME=/opt -v "$$(pwd)":/app -w /app 416670754337.dkr.ecr.eu-west-2.amazonaws.com/dependency-check-runner --repo-name=auth-code-notification