# FIDO UAF (Universal Authentication Framework) client for Android

You can use the FIDO UAF client to authenticate logging in to Android apps without a password.

Use it to:

* register biometric details, such as fingerprint
* authorise against registered biometric details

## Releases

[![Maven central](https://img.shields.io/maven-central/v/io.github.nhsconnect/fido-uaf-client-android?style=flat-square)](https://mvnrepository.com/artifact/io.github.nhsconnect/fido-uaf-client-android)

## Requirements

* Android Marshmallow or above
* Minimum SDK 21, target SDK 27 and above

## Installation

To use the client in your own application, you need to add a Maven reference to your build configuration.

Releases can be found on [Maven Central](https://mvnrepository.com/artifact/io.github.nhsconnect/fido-uaf-client-android), or see the instructions below for making changes and generating your own artifacts locally.

### Generating Maven artifacts

To generate a new set of Maven artifacts once you have made a code change:

1. Update the build version in `fidoclient/build.gradle`

    ```groovy
    ext.build_version = '1.0.3-SNAPSHOT'
    ```
    Ensure to remove the `-SNAPSHOT` suffix for publishing to the releases repository.

2. Build and publish the artifacts to the local Maven repository:

    ```console
    $ ./gradlew clean build publishAllPublicationsToLocalMavenRepository
    ```

3. There are separate local repositories configured for snapshot and release builds. Reference the local snapshot Maven repository in your Android project.

    ```groovy
    allprojects {
      repositories {
        maven {
          "file:./path/to/nhsapp-fido-client-android/fidoclient/build/maven/snapshots"
        }
      }
    }

    dependencies {
      implementation 'io.github.nhsconnect:fido-uaf-client-android:1.0.3-SNAPSHOT'
    }
    ```

### Error handling

The client throws the following errors:

```java
FidoAssertionException
FidoInvalidSignatureException
GenericFidoException
```

## Contribute

We appreciate contributions and there are several ways you can help. For more information, see our [contributing guidelines](/CONTRIBUTING.md).

## Get in touch

The FIDO UAF (Universal Authentication Framework) client for Android is maintained by NHS Digital. [Email us](mailto:nhsapp@nhs.net) or open a [GitHub issue](https://github.com/nhsconnect/nhsapp-fido-client-android/issues/new).

### Reporting vulnerabilities
If you believe you've found a vulnerability or security concern in the client, please report it to us:

1. Submit a vulnerability report through [HackerOne's form](https://hackerone.com/2e6793b1-d580-4172-9ba3-04c98cdfb478/embedded_submissions/new).

2. Put "FAO NHS Digital's NHS App team" in the first line of the description.

## License

The codebase is released under the MIT License, unless stated otherwise. This covers both the codebase and any sample code in the documentation.

The FIDO UAF (Universal Authentication Framework) client for Android is based on an open source implementation created by [eBay](https://github.com/eBay/UAF). eBay's project has an [Apache 2.0 license](https://github.com/eBay/UAF/blob/master/LICENSE), which permits commercial use and modifications.

We use a subset of eBay's implementation. We converted those files we used from Java to Kotlin and we heavily modified most of them. The original copyright notices on each converted file are retained.
