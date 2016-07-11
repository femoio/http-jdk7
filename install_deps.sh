#!/usr/bin/env bash

git clone https://github.com/femoio/jdk7-support.git jdk7-support
cd jdk7-support
mvn install -Dmaven.javadoc.skip=true -DskipTests=true -B -V
cd ..

git clone https://github.com/femoio/dynamic-jdk7.git dynamic
cd dynamic
mvn install -Dmaven.javadoc.skip=true -DskipTests=true -B -V
cd ..

pip install --user gunicorn httpbin
gunicorn httpbin:app > /dev/null &