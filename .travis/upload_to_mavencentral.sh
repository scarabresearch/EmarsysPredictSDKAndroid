#!/bin/bash

if [ -n "$TRAVIS_TAG" ]; then
  ./gradlew uploadArchives closeAndPromoteRepository -i
fi