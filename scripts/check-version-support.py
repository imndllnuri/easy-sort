#!/usr/bin/env python3
"""
Prints a table of Minecraft versions from 1.21.10 onwards, showing whether
Fabric and NeoForge have stable (non-alpha/beta/rc) builds for each, and
flags whether a newer Minecraft version has shipped past the latest one
in the table.

Handles both the legacy 1.21.x version scheme and the year.release scheme
Mojang moved to starting at 26.1 (NeoForge's own artifact versioning
differs between the two: 1.21.10 -> neoforge prefix "21.10.", but
26.1 -> neoforge prefix "26.1.0." since Mojang drops the trailing ".0"
that NeoForge's artifact version keeps).

Usage:
    scripts/check-version-support.py             # table from 1.21.10 onwards
    scripts/check-version-support.py 1.21.5       # table from a different floor

Exit code: 0 always (informational tool) unless a network call fails.
"""

import json
import re
import sys
import urllib.error
import urllib.request

TIMEOUT = 15
UA = {"User-Agent": "easy-sort-version-check"}

MOJANG_MANIFEST = "https://piston-meta.mojang.com/mc/game/version_manifest_v2.json"
FABRIC_GAME_VERSIONS = "https://meta.fabricmc.net/v2/versions/game"
NEOFORGE_METADATA = "https://maven.neoforged.net/releases/net/neoforged/neoforge/maven-metadata.xml"

FLOOR_DEFAULT = "1.21.10"
PRERELEASE_RE = re.compile(r"alpha|beta|-rc", re.IGNORECASE)


def http_json(url):
    req = urllib.request.Request(url, headers=UA)
    with urllib.request.urlopen(req, timeout=TIMEOUT) as resp:
        return json.load(resp)


def http_text(url):
    req = urllib.request.Request(url, headers=UA)
    with urllib.request.urlopen(req, timeout=TIMEOUT) as resp:
        return resp.read().decode()


def fetch_mc_releases():
    manifest = http_json(MOJANG_MANIFEST)
    releases = [v for v in manifest["versions"] if v["type"] == "release"]
    releases.sort(key=lambda v: v["releaseTime"])
    return releases, manifest["latest"]


def fetch_fabric_versions():
    data = http_json(FABRIC_GAME_VERSIONS)
    return {v["version"] for v in data}


def fetch_neoforge_versions():
    xml = http_text(NEOFORGE_METADATA)
    return re.findall(r"<version>([^<]+)</version>", xml)


def neoforge_prefix(mc_version):
    if mc_version.startswith("1.21."):
        return mc_version[2:] + "."          # 1.21.10 -> "21.10."
    parts = mc_version.split(".")
    if len(parts) == 2:
        parts.append("0")                    # 26.1 -> "26.1.0."
    return ".".join(parts) + "."


def neoforge_status(mc_version, all_versions):
    prefix = neoforge_prefix(mc_version)
    matches = [v for v in all_versions if v.startswith(prefix)]
    if not matches:
        return "MISSING", None
    stable = [v for v in matches if not PRERELEASE_RE.search(v)]
    if stable:
        return "OK", stable[-1]
    return "BETA ONLY", matches[-1]


def fabric_status(mc_version, fabric_versions):
    return "OK" if mc_version in fabric_versions else "MISSING"


def fmt_cell(text, width):
    return text + " " * (width - len(text))


def main():
    floor = sys.argv[1] if len(sys.argv) > 1 else FLOOR_DEFAULT

    print("Fetching Mojang version manifest...")
    releases, latest = fetch_mc_releases()
    ids = [v["id"] for v in releases]
    if floor not in ids:
        sys.exit(f"'{floor}' not found among Mojang release versions.")
    floor_idx = ids.index(floor)
    targets = releases[floor_idx:]

    print("Fetching Fabric game version list...")
    fabric_versions = fetch_fabric_versions()

    print("Fetching NeoForge maven metadata...")
    neoforge_versions = fetch_neoforge_versions()

    rows = []
    for v in targets:
        mc = v["id"]
        date = v["releaseTime"][:10]
        f_status = fabric_status(mc, fabric_versions)
        nf_status, nf_build = neoforge_status(mc, neoforge_versions)
        rows.append((mc, date, f_status, nf_status, nf_build))

    col1, col2, col3, col4 = 12, 12, 10, 10
    header = (
        fmt_cell("MC Version", col1)
        + fmt_cell("Released", col2)
        + fmt_cell("Fabric", col3)
        + fmt_cell("NeoForge", col4)
        + "NeoForge build"
    )
    print()
    print(header)
    print("-" * (col1 + col2 + col3 + col4 + 20))
    for mc, date, f_status, nf_status, nf_build in rows:
        print(
            fmt_cell(mc, col1)
            + fmt_cell(date, col2)
            + fmt_cell(f_status, col3)
            + fmt_cell(nf_status, col4)
            + (nf_build or "-")
        )

    print()
    print(f"Latest Mojang release: {latest['release']}")
    print(f"Latest Mojang snapshot: {latest['snapshot']}")
    newest_in_table = targets[-1]["id"]
    if latest["release"] != newest_in_table:
        print(
            f"NOTE: a newer stable Minecraft release ({latest['release']}) exists "
            f"past the newest version in this table ({newest_in_table}). "
            "Re-run with no arguments to pick it up, or check manifest freshness."
        )
    else:
        print(f"No new stable Minecraft release past {newest_in_table}.")
    if latest["snapshot"] != latest["release"]:
        print(
            f"A newer version is in development: {latest['snapshot']} "
            "(snapshot/pre-release, not yet a stable release)."
        )

    unsupported = [r for r in rows if r[2] == "MISSING" or r[3] != "OK"]
    if unsupported:
        print()
        print("Versions with a gap (Fabric missing, or NeoForge missing/beta-only):")
        for mc, date, f_status, nf_status, _ in unsupported:
            print(f"  {mc}: Fabric={f_status}, NeoForge={nf_status}")

    return 0


if __name__ == "__main__":
    try:
        sys.exit(main())
    except urllib.error.URLError as e:
        sys.exit(f"Network error: {e}")
