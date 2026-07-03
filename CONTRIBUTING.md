# Contributing

Thanks for considering contributing to Easy Sort.

## Reporting bugs / requesting features

Open a [GitHub Issue](https://github.com/imndllnuri/easy-sort/issues) using
the appropriate template. For general questions or ideas that aren't a
concrete bug/feature yet, use
[GitHub Discussions](https://github.com/imndllnuri/easy-sort/discussions)
instead.

## Submitting changes

1. Fork the repo and create a branch off `main`.
2. Keep pull requests small and focused — one logical change per PR.
3. Follow the existing package boundaries (see [ARCHITECTURE.md](ARCHITECTURE.md));
   code under `core/` must stay free of Minecraft/Fabric imports.
4. Add or update tests for any behavior change.
5. Use [Conventional Commits](https://www.conventionalcommits.org/) style
   commit messages (`feat:`, `fix:`, `refactor:`, `docs:`, `test:`, `chore:`,
   `perf:`).
6. Update `CHANGELOG.md` under the `Unreleased` section.
7. Open the PR against `main` and fill in the PR template checklist.

## Code style

- Prefer composition over inheritance for sorting strategies (comparators).
- No new abstractions without a concrete second use case.
- Keep `core/` (sorting, transfers, config) dependency-free and unit-testable.

See [DEVELOPMENT.md](DEVELOPMENT.md) for local environment setup.
