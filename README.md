# Overview

[![build_status](https://travis-ci.org/appulse-projects/epmd-java.svg?branch=master)](https://travis-ci.org/appulse-projects/epmd-java)
[![maven_central](https://maven-badges.herokuapp.com/maven-central/io.appulse/epmd-java/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.appulse/epmd-java)
[![License](http://img.shields.io/:license-apache-brightgreen.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)

This is the set of projects, which implements EPMD server and its client.

- [Core description](./core/README.md)
- [Client description](./client/README.md)
- [Server description](./server/README.md)

## Development

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites

For building the project you need only a [Java compiler](http://www.oracle.com/technetwork/java/javase/downloads/index.html).

> **IMPORTANT:** the project requires Java version starting from **8**

And, of course, you need to clone the project from GitHub:

```bash
$> git clone https://github.com/appulse-projects/epmd-java
$> cd epmd-java
```

### Building

For building routine automation, I am using [maven](https://maven.apache.org).

To build the project, do the following:

```bash
$> mvn clean compile
...
[INFO] ------------------------------------------------------------------------
[INFO] Reactor Summary:
[INFO]
[INFO] [EPMD] Parent ...................................... SUCCESS [  0.091 s]
[INFO] [EPMD] Core ........................................ SUCCESS [ 11.966 s]
[INFO] [EPMD] Client ...................................... SUCCESS [  5.504 s]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 18.277 s
[INFO] Finished at: 2019-01-29T10:23:34+03:00
[INFO] Final Memory: 49M/584M
[INFO] ------------------------------------------------------------------------
```

### Running the tests

To run the project's test, do the following:

```bash
$> mvn clean test
...
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
...
[INFO]
[INFO] Results:
[INFO]
[INFO] Tests run: 30, Failures: 0, Errors: 0, Skipped: 0
[INFO]
...
```

Also, if you do `package` or `install` goals, the tests launch automatically.

## Built With

* [Java](http://www.oracle.com/technetwork/java/javase) - is a systems and applications programming language

* [Lombok](https://projectlombok.org) - is a java library that spicing up your java

* [Junit](http://junit.org/junit4/) - is a simple framework to write repeatable tests

* [AssertJ](http://joel-costigliola.github.io/assertj/) - AssertJ provides a rich set of assertions, truly helpful error messages, improves test code readability

* [Maven](https://maven.apache.org) - is a software project management and comprehension tool

## Changelog

To see what has changed in recent versions of the project, see the [changelog](./CHANGELOG.md) file.

## Contributing

Please read [contributing](./CONTRIBUTING.md) file for details on my code of conduct, and the process for submitting pull requests to me.

## Versioning

We use [SemVer](http://semver.org/) for versioning. For the versions available, see the [tags on this repository](https://github.com/appulse-projects/epmd-java/tags).

## Authors

* **[Artem Labazin](https://github.com/xxlabaza)** - creator and the main developer

## License

This project is licensed under the Apache License 2.0 License - see the [license](./LICENSE) file for details
