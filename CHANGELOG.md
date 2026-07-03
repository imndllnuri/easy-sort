# Changelog

All notable changes to this project are documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/).

## [Unreleased]

### Added

- Initial project scaffold: Gradle/Loom build, package skeleton, CI workflow,
  repository documentation.
- Core sort engine (`core.sort.SortEngine`): merges partial stacks (capped at
  each item's max stack size), sorts via a configurable `SortConfig`
  (mod id / item id / count), deterministic and idempotent by construction.
  Not yet wired into any in-game container - unit-tested only.
