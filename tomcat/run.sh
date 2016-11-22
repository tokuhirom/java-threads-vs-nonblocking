#!/bin/bash
mvn clean package
java -jar target/tomcat-1.0-SNAPSHOT.jar
