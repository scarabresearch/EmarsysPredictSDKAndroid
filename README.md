# Emarsys Predict SDK for Android

This library makes it possible to use the Emarsys Predict services from Android applications.

### Requirements
- Android Studio 2

### Build Instructions
```sh
$ git clone https://github.com/scarabresearch/EmarsysPredictSDKAndroid
$ cd EmarsysPredictSDKAndroid
$ ./gradlew install
```

* Open the sample in Android Studio and press Run

### Deploy to Maven Local
```sh
$ ./gradlew install
```

### Run Tests
```sh
$ ./gradlew cAT -i
```

### Deploy to Bintray
```sh
$ ./gradlew bintrayUploadOnTag
```

### Generate JavaDoc
```sh
$ ./gradlew javaDoc
```