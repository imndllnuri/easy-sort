#!/usr/bin/env bash
# Runs pre-flight checks, builds, then launches a dev client for manual
# testing, on a single platform or both back-to-back.
#
# Usage: ./start.sh <fabric|neoforge|both> [gradle task]   (default task: runClient)
#
# Note: bare `./gradlew runClient` (no module prefix) now launches BOTH
# platforms at once, since fabric/ and neoforge/ each declare that task.
# This script always targets a module explicitly (:fabric:... / :neoforge:...)
# to avoid that.
set -uo pipefail

cd "$(dirname "${BASH_SOURCE[0]}")"

info()  { printf '\033[1;34m[start.sh]\033[0m %s\n' "$1"; }
ok()    { printf '\033[1;32m[ ok ]\033[0m %s\n' "$1"; }
warn()  { printf '\033[1;33m[warn]\033[0m %s\n' "$1"; }
fail()  { printf '\033[1;31m[fail]\033[0m %s\n' "$1"; }

usage() {
	echo "Usage: ./start.sh <fabric|neoforge|both> [gradle task]"
	echo "  fabric    launch the Fabric dev client   (default task: runClient)"
	echo "  neoforge  launch the NeoForge dev client (default task: runClient)"
	echo "  both      run fabric, then neoforge, sequentially"
	exit 1
}

PLATFORM="${1:-}"
TASK="${2:-runClient}"

if [ "$PLATFORM" = "both" ]; then
	"$0" fabric "$TASK"
	FABRIC_EXIT=$?
	"$0" neoforge "$TASK"
	NEOFORGE_EXIT=$?
	info "Summary: fabric exit=$FABRIC_EXIT, neoforge exit=$NEOFORGE_EXIT"
	exit $(( FABRIC_EXIT != 0 || NEOFORGE_EXIT != 0 ))
fi

if [ "$PLATFORM" != "fabric" ] && [ "$PLATFORM" != "neoforge" ]; then
	usage
fi

FAIL=0
LOG_FILE="$PLATFORM/run/logs/latest.log"

if [ "$PLATFORM" = "fabric" ]; then
	MOD_ID="easy-sort"
else
	MOD_ID="easysort"
fi

info "Running pre-flight checks ($PLATFORM)..."

# 1. Java version
if ! command -v java >/dev/null 2>&1; then
	fail "No 'java' on PATH."
	FAIL=1
else
	JAVA_VER="$(java -version 2>&1 | head -1 | grep -oE '"[0-9]+' | tr -d '"')"
	if [ "$JAVA_VER" = "21" ]; then
		ok "Java $JAVA_VER detected."
	else
		warn "Java $JAVA_VER detected, but this project targets Java 21 (gradle.properties / build.gradle toolchain)."
	fi
fi

# 2. gradlew present and executable
if [ ! -f "./gradlew" ]; then
	fail "gradlew not found in project root."
	FAIL=1
else
	chmod +x ./gradlew
	ok "gradlew is executable."
fi

# 3. platform metadata file present and well-formed
if [ "$PLATFORM" = "fabric" ]; then
	MOD_META="fabric/src/main/resources/fabric.mod.json"
	if [ ! -f "$MOD_META" ]; then
		fail "$MOD_META is missing."
		FAIL=1
	elif command -v python3 >/dev/null 2>&1; then
		if python3 -c "import json,sys; json.load(open('$MOD_META'))" 2>/tmp/easy_sort_meta_err; then
			ok "fabric.mod.json is valid JSON."
		else
			fail "fabric.mod.json is not valid JSON: $(cat /tmp/easy_sort_meta_err)"
			FAIL=1
		fi
	else
		warn "python3 not found, skipping fabric.mod.json JSON validation."
	fi
else
	MOD_META="neoforge/src/main/resources/META-INF/neoforge.mods.toml"
	if [ ! -f "$MOD_META" ]; then
		fail "$MOD_META is missing."
		FAIL=1
	else
		ok "neoforge.mods.toml present."
	fi
fi

if [ "$FAIL" -eq 1 ]; then
	fail "Pre-flight checks failed. Aborting before build."
	exit 1
fi

# 4. Compile-only build first, so a compile error surfaces fast without
#    waiting for the client to boot. Scoped to this module only.
info "Running './gradlew :$PLATFORM:build' as a fast compile check..."
if ! ./gradlew ":$PLATFORM:build" --no-daemon --console=plain; then
	fail "Build failed. See output above. Not launching the client."
	exit 1
fi
ok "Build succeeded."

# 5. Launch the requested task (default: runClient), scoped to this module
#    so it never accidentally launches the other platform's client too.
info "Launching './gradlew :$PLATFORM:$TASK' ..."
./gradlew ":$PLATFORM:$TASK" --no-daemon --console=plain
GRADLE_EXIT=$?

# 6. Post-run log summary, so you don't have to dig through run/logs yourself.
info "Run finished (gradle exit code $GRADLE_EXIT). Summarizing $LOG_FILE ..."
if [ -f "$LOG_FILE" ]; then
	if grep -q "$MOD_ID" "$LOG_FILE"; then
		ok "'$MOD_ID' mod entry found in the log."
	else
		warn "'$MOD_ID' was NOT found in $LOG_FILE - the mod may not have loaded."
	fi

	ERRORS="$(grep -iE '\[.*ERROR\]' "$LOG_FILE" | grep -viE 'telemetry|authlib|InvalidCredentialsException|Failed to fetch user properties|Failed to retrieve profile key pair|realms|X11: Standard cursor shape unavailable|GL ERROR|^\[.*\] \(Minecraft\) @ ' || true)"
	if [ -n "$ERRORS" ]; then
		warn "Errors found in the log (excluding known-harmless offline-account auth errors):"
		echo "$ERRORS" | tail -20
	else
		ok "No unexpected ERROR lines in the log."
	fi
else
	warn "$LOG_FILE not found - the game may have crashed before creating a log."
fi

exit "$GRADLE_EXIT"
