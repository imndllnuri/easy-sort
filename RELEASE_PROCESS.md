# Release Process

1. Ensure `main` is green: `./gradlew build` passes, tests pass.
2. Move the `[Unreleased]` section of `CHANGELOG.md` into a new dated version
   section, following [Keep a Changelog](https://keepachangelog.com/en/1.1.0/).
3. Bump `mod_version` in `gradle.properties` per [VERSIONING.md](VERSIONING.md).
4. Run the manual QA checklist in [TESTING.md](TESTING.md).
5. Commit as `chore: release vX.Y.Z`.
6. Tag the commit (`vX.Y.Z`) and push the tag — this triggers the release
   GitHub Actions workflow, which builds the jar and publishes to Modrinth and
   CurseForge.
7. Create a GitHub Release from the tag, with:
   - Version number
   - Supported Minecraft version(s) and loader(s)
   - Summary of new features / bug fixes
   - Known issues
   - Breaking changes and migration notes, if any (copy from CHANGELOG)

## Cadence

Small, frequent minor releases (roughly every 2-4 weeks once past v1.0) are
preferred over large infrequent ones — this lowers regression risk per release.
