# FIDO UAF (Universal Authentication Framework) client for Android

You can use the FIDO UAF client to authenticate logging in to Android apps without a password.

Use it to:

* register biometric details, such as fingerprint
* authorise against registered biometric details

We'll add the client to the public [Maven central repository](https://mvnrepository.com/repos/central) soon.

## Requirements

* Android Marshmallow or above
* Minimum SDK 21, target SDK 27 and above

## Installation

To use the client in your own application, you need to add a Maven reference to your build configuration.

## Getting started

You need to generate and reference the Maven artifacts for your own project until they are available in the Maven central repository.

### Generating Maven artifacts

To generate a new set of Maven artifacts once you have made a code change:

1. Update the pom version number in `fidoclient/build.gradle` if required.

2. Build and publish the artifacts:

    ```console
    ./gradlew uploadArchives
    ```

3. You now have a local Maven repository in the FIDO UAF client's `maven` folder. Reference this in your Android build configuration. For example:

    ```console
    allprojects {
      repositories {
        maven {
          "file:./path/to/fido/client/maven"
        }
      }
    }
    dependencies {
      implementation 'com.nhs.online.fidoclient:fidoclient:1.0.3'
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

The FIDO UAF (Universal Authentication Framework) client for Android is maintained by NHS Digital. [Email us](mailto:nhsapp@nhs.net) or open a GitHub issue.

### Reporting vulnerabilities

If you believe you've found a vulnerability or security concern in the client, please report it to us:

1. Submit a vulnerability report through [HackerOne's form](https://hackerone.com/2e6793b1-d580-4172-9ba3-04c98cdfb478/embedded_submissions/new).

2. Put "FAO NHS Digital's NHS App team" in the first line of the description.

## License

The codebase is released under the MIT License, unless stated otherwise. This covers both the codebase and any sample code in the documentation.

The FIDO UAF (Universal Authentication Framework) client for Android is based on an open source implementation created by [eBay](https://github.com/eBay/UAF). eBay's project has an [Apache 2.0 license](https://github.com/eBay/UAF/blob/master/LICENSE), which permits commercial use and modifications.

We use a subset of eBay's implementation. We converted those files we used from Java to Kotlin and we heavily modified most of them. The original copyright notices on each converted file are retained.
