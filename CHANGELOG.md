# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/), and this project adheres
to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

Releases prior to v0.6.0 are only documented on
the [GitHub Release Page](https://github.com/rwth-acis/las2peer-ActivityTracker/releases)

## [Unreleased]

## [0.8.0] - 2021-09-08

### Added

- Added new testcases [#43](https://github.com/rwth-acis/las2peer-ActivityTracker/pull/43)
- Add returned data models to swagger annotations [#46](https://github.com/rwth-acis/las2peer-ActivityTracker/pull/46)

### Changed

- Updated dependencies (las2peer 1.1.2 and thereby requiring Java
    14) [#41](https://github.com/rwth-acis/las2peer-ActivityTracker/pull/41)
        , [#43](https://github.com/rwth-acis/las2peer-ActivityTracker/pull/43),
        [#49](https://github.com/rwth-acis/las2peer-ActivityTracker/pull/49)
- Changed buildsystem to gradle [#43](https://github.com/rwth-acis/las2peer-ActivityTracker/pull/43)
- Automatically generate jooq code from migration files at build
  time [#43](https://github.com/rwth-acis/las2peer-ActivityTracker/pull/43)
- Move to multi-staged docker build for slimmer target
  image [#43](https://github.com/rwth-acis/las2peer-ActivityTracker/pull/43)
- Replaced vtor with Java Bean Validation [#44](https://github.com/rwth-acis/las2peer-ActivityTracker/pull/44)
- Remove static code from data classes and generate getter/setters and builder with
  lombok. [#46](https://github.com/rwth-acis/las2peer-ActivityTracker/pull/46)
- Switch default backend database to postgres [#48](https://github.com/rwth-acis/las2peer-ActivityTracker/pull/48)

### Removed

- `additionalObject` filtering capabilities in `GET /activities`
  endpoint [#44](https://github.com/rwth-acis/las2peer-ActivityTracker/pull/44)

## [0.6.0] - 2019-02-19

See GH Releases
