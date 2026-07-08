#!/usr/bin/env python3
"""
Checks whether Easy Sort's toolchain (Architectury Loom, Fabric API,
NeoForge, Parchment) supports a newer Minecraft version than the one
currently in gradle.properties.

Usage:
    scripts/check-mc-update.py            # auto-detect candidate versions
    scripts/check-mc-update.py 26.1       # check one specific version

Exit code: 0 if the primary candidate is fully ready to build against,
1 otherwise. Run it whenever you want an update - no need to ask Claude.
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
LOOM_REPO = "architectury/architectury-loom"


def http_json(url):
    req = urllib.request.Request(url, headers={"User-Agent": "easy-sort-update-check"})
    with urllib.request.urlopen(req, timeout=TIMEOUT) as resp:
        return json.load(resp)


def http_status(url):
    req = urllib.request.Request(url, headers={"User-Agent": "easy-sort-update-check"})
    try:
        with urllib.request.urlopen(req, timeout=TIMEOUT) as resp:
            return resp.status
    except urllib.error.HTTPError as e:
        return e.code


def read_current_version():
    text = GRADLE_PROPERTIES.read_text()
    m = re.search(r"^minecraft_version=(\S+)", text, re.MULTILINE)
    if not m:
        sys.exit("Could not find minecraft_version in gradle.properties")
    return m.group(1)


def fetch_fabric_game_versions():
    return http_json("https://meta.fabricmc.net/v2/versions/game")


def pick_candidates(current, versions):
    idx = next((i for i, v in enumerate(versions) if v["version"] == current), None)
    if idx is None:
        print(f"WARNING: {current} not found in Fabric's version list (unusual - "
              f"double check minecraft_version in gradle.properties).")
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
    url = f"https://api.modrinth.com/v2/project/{FABRIC_API_PROJECT_ID}/version?game_versions=%5B%22{target}%22%5D"
    try:
        versions = http_json(url)
        return (len(versions) > 0, f"{len(versions)} build(s) found" if versions else "no builds yet")
    except Exception as e:
        return (None, f"check failed: {e}")


def check_neoforge(target):
    # NeoForge versions drop a leading "1." from the MC version, e.g. 1.21.10 -> 21.10.x.y
    prefix = target[2:] if target.startswith("1.") else target
    url = "https://maven.neoforged.net/releases/net/neoforged/neoforge/maven-metadata.xml"
    try:
        req = urllib.request.Request(url, headers={"User-Agent": "easy-sort-update-check"})
        with urllib.request.urlopen(req, timeout=TIMEOUT) as resp:
            xml = resp.read().decode()
        found = re.findall(rf"<version>({re.escape(prefix)}\.[^<]+)</version>", xml)
        return (len(found) > 0, found[-1] if found else f"no version prefixed '{prefix}.' yet")
    except Exception as e:
        return (None, f"check failed: {e}")


def check_parchment(target):
    url = f"https://maven.parchmentmc.org/org/parchmentmc/data/parchment-{target}/maven-metadata.xml"
    status = http_status(url)
    if status == 200:
        return (True, "mappings data available")
    if status == 404:
        return (False, "no mappings data yet")
    return (None, f"unexpected HTTP {status}")


def check_architectury_loom(target):
    query = f'repo:{LOOM_REPO}+in:title+"{target}+Support"'
    url = f"https://api.github.com/search/issues?q={query}"
    try:
        result = http_json(url)
        items = result.get("items", [])
        if not items:
            return (None, "no tracking issue found - check "
                          f"https://github.com/{LOOM_REPO}/issues manually")
        issue = items[0]
        if issue["state"] == "closed":
            return (True, f"tracking issue #{issue['number']} closed - {issue['html_url']}")
        return (False, f"tracking issue #{issue['number']} still open - {issue['html_url']}")
    except urllib.error.HTTPError as e:
        if e.code == 403:
            return (None, "GitHub API rate-limited (unauthenticated, 60 req/hr) - try again later")
        return (None, f"check failed: HTTP {e.code}")
    except Exception as e:
        return (None, f"check failed: {e}")


def fmt(ok):
    return {True: "[ OK ]", False: "[BLOCKED]", None: "[ ?? ]"}[ok]


def check_version(target):
    print(f"\n=== {target} ===")
    gates = {
        "Fabric API":       check_fabric_api(target),
        "NeoForge":         check_neoforge(target),
        "Parchment":        check_parchment(target),
        "Architectury Loom": check_architectury_loom(target),
    }
    for name, (ok, detail) in gates.items():
        print(f"  {fmt(ok):10} {name:20} {detail}")

    statuses = [ok for ok, _ in gates.values()]
    if all(s is True for s in statuses):
        print(f"  => READY: all toolchain gates support {target}.")
        return True
    if any(s is False for s in statuses):
        print(f"  => NOT READY: at least one gate blocks {target}.")
        return False
    print(f"  => UNKNOWN: could not fully determine readiness for {target}, check manually.")
    return None


def main():
    current = read_current_version()
    print(f"Current minecraft_version (gradle.properties): {current}")

    if len(sys.argv) > 1:
        targets = [sys.argv[1]]
    else:
        versions = fetch_fabric_game_versions()
        targets = pick_candidates(current, versions)
        if not targets:
            print(f"No newer stable Minecraft version found past {current}. Already current.")
            return 0

    results = [check_version(t) for t in targets]
    return 0 if results and results[0] is True else 1


if __name__ == "__main__":
    sys.exit(main())
