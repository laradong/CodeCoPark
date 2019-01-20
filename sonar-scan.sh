#!/usr/bin/env bash
mvn sonar:sonar \
  -Dsonar.projectKey=laradong_CodeCoPark \
  -Dsonar.organization=laradong-github \
  -Dsonar.host.url=https://sonarcloud.io \
  -Dsonar.login=c25e554641112f21a7efff2b81cedf496866f921