################################################
## point JAVA_HOME to the root folder of graalvm
################################################
export JAVA_HOME=/home/christian/bin/graalvm

## Install graal native-image if needed
#"$JAVA_HOME"/bin/gu install native-image


################################################
## Build standalone native binary and copy it to the output folder:
################################################
./mvnw -Pnative -DskipTests package
cp target/practicedates bin/practicedates


################################################
## Build a docker image that contains the native binary 
## and copy it to the output folder
################################################
./mvnw spring-boot:build-image
docker save practicedates | gzip > bin/docker/practicedates.tgz


