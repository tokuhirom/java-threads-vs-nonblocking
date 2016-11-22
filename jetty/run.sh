#!/bin/bash
mvn clean package
java -jar target/jetty-1.0-SNAPSHOT.jar
