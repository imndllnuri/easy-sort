#!/usr/bin/env python3
"""
Checks whether a newer Minecraft version is ready to build Easy Sort against,
for each platform separately. Fabric and NeoForge can target different
Minecraft versions (fabric_minecraft_version / neoforge_minecraft_version in
gradle.properties), so each one is checked against its own current version.

A newer version is "ready" for a platform when:
  Fabric    Fabric API has a build for it (Modrinth).
  NeoForge  NeoForge has a stable (non-alpha/beta/rc) build for it.

(Parchment mappings no longer exist from Minecraft 26.1 on, and the build no
longer uses Architectury Loom, so neither is checked any more. For a table
across a whole range of versions, see scripts/check-version-support.py.)

Usage:
    scripts/check-mc-update.py            # auto-detect candidate versions
    scripts/check-mc-update.py 26.3       # check one specific version, both platforms

Exit code: 0 if every platform is already current or its first candidate is
ready, 1 otherwise.
"""

import json
import re
import sys
import urllib.error
import urllib.request
from pathlib import Path

TIMEOUT = 15
GRADLE_PROPERTIES = Path(__file__).resolve().parent.parent / "gradle.properties"
FABRIC_API_PROJECT_ID = "P7dR8mSH"  # modrinth project id for "fabric-api"
NEOFORGE_METADATA = "https://maven.neoforged.net/releases/net/neoforged/neoforge/maven-metadata.xml"
PRERELEASE_RE = re.compile(r"alpha|beta|-rc", re.IGNORECASE)
UA = {"User-Agent": "easy-sort-update-check"}


def http_json(url):
    req = urllib.request.Request(url, headers=UA)
    with urllib.request.urlopen(req, timeout=TIMEOUT) as resp:
        return json.load(resp)


def http_text(url):
    req = urllib.request.Request(url, headers=UA)
    with urllib.request.urlopen(req, timeout=TIMEOUT) as resp:
        return resp.read().decode()


def read_current_versions():
    """Returns {"fabric": "26.2", "neoforge": "26.1.2"} from gradle.properties.

    Falls back to the old single minecraft_version key (still used on the
    frozen mc/* branches).
    """
    text = GRADLE_PROPERTIES.read_text()

    def get(key):
        m = re.search(rf"^{key}=(\S+)", text, re.MULTILINE)
        return m.group(1) if m else None

    legacy = get("minecraft_version")
    fabric = get("fabric_minecraft_version") or legacy
    neoforge = get("neoforge_minecraft_version") or legacy
    if not fabric or not neoforge:
        sys.exit("Could not find fabric_minecraft_version / neoforge_minecraft_version "
                 "(or minecraft_version) in gradle.properties")
    return {"fabric": fabric, "neoforge": neoforge}


def fetch_fabric_game_versions():
    # Newest first. Each entry: {"version": "26.2", "stable": true}
    return http_json("https://meta.fabricmc.net/v2/versions/game")


def pick_candidates(current, versions):
    idx = next((i for i, v in enumerate(versions) if v["version"] == current), None)
    if idx is None:
        print(f"WARNING: {current} not found in Fabric's version list (unusual - "
              f"double check gradle.properties).")
        return []
    newer_stable = [v["version"] for v in versions[:idx] if v["stable"]]
    if not newer_stable:
        return []
    immediate_next = newer_stable[-1]      # closest release after current
    latest_stable = newer_stable[0]        # most recent stable overall
    candidates = [immediate_next]
    if latest_stable != immediate_next:
        candidates.append(latest_stable)
    return candidates


def check_fabric_api(target):
    url = (f"https://api.modrinth.com/v2/project/{FABRIC_API_PROJECT_ID}/version"
           f"?game_versions=%5B%22{target}%22%5D")
    try:
        versions = http_json(url)
        return (len(versions) > 0,
                f"{len(versions)} Fabric API build(s) found" if versions else "no Fabric API builds yet")
    except Exception as e:
        return (None, f"check failed: {e}")


def neoforge_prefix(mc_version):
    if mc_version.startswith("1.21."):
        return mc_version[2:] + "."          # 1.21.10 -> "21.10."
    parts = mc_version.split(".")
    if len(parts) == 2:
        parts.append("0")                    # 26.1 -> "26.1.0."
    return ".".join(parts) + "."


def check_neoforge(target):
    try:
        xml = http_text(NEOFORGE_METADATA)
    except Exception as e:
        return (None, f"check failed: {e}")
    prefix = neoforge_prefix(target)
    matches = re.findall(rf"<version>({re.escape(prefix)}[^<]+)</version>", xml)
    if not matches:
        return (False, f"no NeoForge build prefixed '{prefix}' yet")
    stable = [v for v in matches if not PRERELEASE_RE.search(v)]
    if stable:
        return (True, f"stable build {stable[-1]}")
    return (False, f"beta only ({matches[-1]})")


def fmt(ok):
    return {True: "[ OK ]", False: "[BLOCKED]", None: "[ ?? ]"}[ok]


GATES = {
    "fabric": ("Fabric API", check_fabric_api),
    "neoforge": ("NeoForge (stable)", check_neoforge),
}


def check_platform(platform, target):
    name, check = GATES[platform]
    ok, detail = check(target)
    print(f"  {platform:9} {target:8} {fmt(ok):10} {name:18} {detail}")
    return ok


def main():
    current = read_current_versions()
    print("Current targets (gradle.properties):")
    print(f"  Fabric   {current['fabric']}")
    print(f"  NeoForge {current['neoforge']}")

    versions = None
    exit_code = 0
    for platform in ("fabric", "neoforge"):
        print(f"\n=== {platform} ===")
        if len(sys.argv) > 1:
            targets = [sys.argv[1]]
        else:
            if versions is None:
                versions = fetch_fabric_game_versions()
            targets = pick_candidates(current[platform], versions)
            if not targets:
                print(f"  No newer stable Minecraft version past {current[platform]}. Already current.")
                continue
        results = [check_platform(platform, t) for t in targets]
        if results[0] is not True:
            exit_code = 1
    return exit_code


if __name__ == "__main__":
    sys.exit(main())
