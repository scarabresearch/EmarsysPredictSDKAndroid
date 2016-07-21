#!/bin/bash

if [ -n "$TRAVIS_TAG" ]; then
  ./gradlew uploadArchives -i
fi