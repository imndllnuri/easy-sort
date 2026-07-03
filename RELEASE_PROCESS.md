# Release Process

1. Ensure `main` is green: `./gradlew build` passes, tests pass.
2. Move the `[Unreleased]` section of `CHANGELOG.md` into a new dated version
   section, following [Keep a Changelog](https://keepachangelog.com/en/1.1.0/).
3. Bump `mod_version` in `gradle.properties` per [VERSIONING.md](VERSIONING.md).
4. Run the manual QA checklist in [TESTING.md](TESTING.md).
5. Commit as `chore: release vX.Y.Z`.
6. Tag the commit (`vX.Y.Z`) and push the tag — this triggers
   `.github/workflows/release.yml`, which builds the jar and creates a GitHub
   Release with it attached.
7. Modrinth/CurseForge publishing from that same workflow is currently
   commented out pending project creation on both platforms - see
   `PUBLISHING_CHECKLIST.md`. Once both projects exist, uncomment the publish
   step and fill in the two project IDs; from then on every tag push
   publishes to all three places automatically.
8. Until Modrinth/CurseForge automation is active, upload the built jar to
   each platform manually from the same `build/libs/*.jar` the workflow
   produces.

## Cadence

Small, frequent minor releases (roughly every 2-4 weeks once past v1.0) are
preferred over large infrequent ones — this lowers regression risk per release.
