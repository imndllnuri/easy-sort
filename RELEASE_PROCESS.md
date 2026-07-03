# Release Process

1. Ensure `main` is green: `./gradlew build` passes, tests pass.
2. Move the `[Unreleased]` section of `CHANGELOG.md` into a new dated version
   section, following [Keep a Changelog](https://keepachangelog.com/en/1.1.0/).
   Avoid publishing a version with an empty/non-functional changelog entry
   (e.g. "no changes, just republishing") where avoidable - CurseForge's fair
   play rules flag re-uploads without meaningful changes.
3. Bump `mod_version` in `gradle.properties` per [VERSIONING.md](VERSIONING.md).
4. Run the manual QA checklist in [TESTING.md](TESTING.md).
5. Commit as `chore: release vX.Y.Z`.
6. Tag the commit (`vX.Y.Z`) and push the tag — this triggers
   `.github/workflows/release.yml`, which builds the jar, creates a GitHub
   Release with it attached, and publishes to Modrinth + CurseForge
   automatically (confirmed working end to end since `v0.2.0-alpha`).
7. The workflow's auto-generated GitHub Release notes are minimal (just a
   compare link) when there's no prior tag to diff against usefully; replace
   them with the actual CHANGELOG section for that version:
   `gh release edit vX.Y.Z --notes-file <(changelog section) --prerelease`
   (pre-release flag is inferred from the tag containing `-alpha`/`-beta`,
   but double check it landed correctly).

## Cadence

Small, frequent minor releases (roughly every 2-4 weeks once past v1.0) are
preferred over large infrequent ones — this lowers regression risk per release.
