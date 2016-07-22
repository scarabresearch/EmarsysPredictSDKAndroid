# Emarsys Predict SDK for Android

## Overview

This document describes the Emarsys Predict SDK for the Android platform. It is intended for Android mobile app developers who want to integrate Emarsys Web Extend or Predict Recommendations into native mobile app. The document describes the main Emarsys Predict Android SDK interface functions.

## Install

Minimum supported version is ***minSdkVersion 16***

- You will need a valid product catalog, described here: http://documentation.emarsys.com/resource/b2c-cloud/web-extend/implementing-web-extend/#2
- Add the SDK to your build.gradle
```
repositories {
    jcenter()
}
```
and 
```
compile 'com.scarabresearch:predictsdk:+'
```
- Implement data collection in the application
- Implement recommendations

## Getting started

The semantics of each SDK function is documented in our JavaScript API. There are some fundamental differences between the Javascript API and the Android SDK:

- ***Session Initialization*** You should call Session.initialize method with a Storage interface implementation in the application entry point.

- ***Storage*** Needs to implement two methods (sample code uses Android's SharedPreferences). 

- ***Click tracking:*** You should instantiate Transaction object with the Transaction(Item item) constructor, if the user selects a recommended item.

- ***Rendering recommendations:*** We recommend to use a ListView or a RecyclerView for displaying the recommendations, and append/make the results from the recommendation to the data source, then reload the data to it.

You may filter down the live events on [console](https://console.scarabresearch.com/#/liveevents) for events only coming from Android devices for developing/debugging purposes.

The ***Javascript API documentation*** lives here:

- [Data collection (Web Extend)](http://documentation.emarsys.com/resource/b2c-cloud/web-extend/javascript-api/)
- [Recommendations](http://documentation.emarsys.com/resource/b2c-cloud/predict/implementation/delivering-web-recommendations/)

## Sample Code

We also created example applications for you to make it easier to implement Emarsys Predict SDK in your e-commerce application. The samples an be found in [this repository](https://github.com/scarabresearch/EmarsysMobileSamplesAndroid).

We <3 open source software, that’s why we open sourced our Emarsys Predict SDK for Android implementation. That way you can be confident that the library what you include contains only the code necessary to deliver awesome recommendations and also manage the data collection. You can find the repository [here](https://github.com/scarabresearch/EmarsysPredictSDKAndroid).

## SDK Development

The following tasks may be useful during SDK development.

### Deploy to Maven Local
```sh
$ ./gradlew install
```

### Run Tests
```sh
$ ./gradlew cAT -i
```

### Generate JavaDoc
```sh
$ ./gradlew javaDoc
```
