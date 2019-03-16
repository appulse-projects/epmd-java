# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/)
and this project adheres to [Semantic Versioning](http://semver.org/spec/v2.0.0.html).

[Tags on this repository](https://github.com/appulse-projects/epmd-java/tags)

## [Unreleased]

- Add more unit and integration tests.

## [2.0.0](https://github.com/appulse-projects/epmd-java/releases/tag/2.0.0) - 2019-03-17

### Added

- More documentation;

### Changed

- Make the client async;
- Refactored the server, simplify it;
- Rewritten the code to make it more readable.

### Removed

- Netty support.

## [1.0.2](https://github.com/appulse-projects/epmd-java/releases/tag/1.0.2) - 2018-06-06

Code refactoring.

### Added

- JavaDoc comments.
- `README.md` content.

## [1.0.1](https://github.com/appulse-projects/epmd-java/releases/tag/1.0.1) - 2018-03-23

Small refactoring.

### Changed

- Server generates `creation` from 1 to 3 now.
- Small bug fixes.
- Updated dependencies.

## [1.0.0](https://github.com/appulse-projects/epmd-java/releases/tag/1.0.0) - 2018-03-16

Code style refactoring.

### Changed

- PMD, FindBugs and Checkstyle fixes;
- Updated dependencies.

## [0.4.2](https://github.com/appulse-projects/epmd-java/releases/tag/0.4.2) - 2018-03-05

Removed lookup cache, which was full of bugs

### Removed

- Lookup cache of node infos.

## [0.4.1](https://github.com/appulse-projects/epmd-java/releases/tag/0.4.1) - 2018-02-16

Minor bug fixes

### Changed

- EPMD address and port are public now.
- Lookup cache doesn't cache not found items any more.

## [0.4.0](https://github.com/appulse-projects/epmd-java/releases/tag/0.4.0) - 2018-02-16

Introducing NIO server.

### Added

- Netty server instead of simple IO-based.

### Changed

- The way of serialization/deserialization of EPMD messages.

## [0.3.3](https://github.com/appulse-projects/epmd-java/releases/tag/0.3.3) - 2018-02-09

Minor fix, using unsigned numbers where they are needed

## [0.3.2](https://github.com/appulse-projects/epmd-java/releases/tag/0.3.2) - 2018-01-31

### Added

- EPMD server sub-project.
- Server test.

### Changed

- [Core]: registration result returns creation 0 in case of failure.
- [Client]: set connection read timeout (connection_timeout * 2).

## [0.2.2](https://github.com/appulse-projects/epmd-java/releases/tag/0.2.2) - 2018-01-29

### Added

- EPMD client sub-project.
- Serialization/Deserialization exceptions in `Core`.

### Changed

- Fixed `RegistrationResult` length (it doesn't have this by spec).

## [0.0.2](https://github.com/appulse-projects/epmd-java/releases/tag/0.0.2) - 2018-01-28

### Changed

- Simplified serialiation/deserialization mechanism.

## [0.0.1](https://github.com/appulse-projects/epmd-java/releases/tag/0.0.1) - 2018-01-26

Initial release. Core serialized/deserialized POJOs for communication between EPMD client and server.

### Added

- Serialized/deserialized POJOs.
