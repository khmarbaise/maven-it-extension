#!/bin/bash
 mvn clean deploy -DskipTests -DaltDeploymentRepository=github-maven-it-extension::https://maven.pkg.github.com/khmarbaise/maven-it-extension -Dtoken=<TOKEN> -Dcheckstyle.skip=true 
