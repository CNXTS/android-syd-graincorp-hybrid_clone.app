fastlane documentation
================
# Installation

Make sure you have the latest version of the Xcode command line tools installed:

```
xcode-select --install
```

Install _fastlane_ using
```
[sudo] gem install fastlane -NV
```
or alternatively using `brew install fastlane`

# Available Actions
## Android
### android test_only
```
fastlane android test_only
```
Run unit tests
### android test_and_build
```
fastlane android test_and_build
```
Run unit tests and create build for all build variants in config.json
### android build_only
```
fastlane android build_only
```
Create build for all build variants specified in config.json
### android build_and_upload
```
fastlane android build_and_upload
```
Create build for all build variants specified in config.json and upload to HockeyApp
### android test_build_and_upload
```
fastlane android test_build_and_upload
```
Run unit tests, create builds and upload to hockey
### android sonar_only
```
fastlane android sonar_only
```
Run SonarQube Analysis
### android functional_test_only
```
fastlane android functional_test_only
```
Run functional tests

----

This README.md is auto-generated and will be re-generated every time [_fastlane_](https://fastlane.tools) is run.
More information about fastlane can be found on [fastlane.tools](https://fastlane.tools).
The documentation of fastlane can be found on [docs.fastlane.tools](https://docs.fastlane.tools).
