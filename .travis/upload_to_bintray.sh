#!/bin/bash

if [ -n "$TRAVIS_TAG" ]; then
  ./gradlew bintrayUpload -i
fi
