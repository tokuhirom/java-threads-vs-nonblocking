#!/bin/bash
mvn clean package
java -jar target/undertow-demo-1.0-SNAPSHOT.jar
