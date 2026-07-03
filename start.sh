#!/usr/bin/env bash
# Runs pre-flight checks, builds, then launches the dev client for manual testing.
# Usage: ./start.sh [gradle task]   (default task: runClient)
set -uo pipefail

cd "$(dirname "${BASH_SOURCE[0]}")"

TASK="${1:-runClient}"
LOG_FILE="fabric/run/logs/latest.log"
FAIL=0

info()  { printf '\033[1;34m[start.sh]\033[0m %s\n' "$1"; }
ok()    { printf '\033[1;32m[ ok ]\033[0m %s\n' "$1"; }
warn()  { printf '\033[1;33m[warn]\033[0m %s\n' "$1"; }
fail()  { printf '\033[1;31m[fail]\033[0m %s\n' "$1"; FAIL=1; }

info "Running pre-flight checks..."

# 1. Java version
if ! command -v java >/dev/null 2>&1; then
	fail "No 'java' on PATH."
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
else
	chmod +x ./gradlew
	ok "gradlew is executable."
fi

# 3. fabric.mod.json is valid JSON
MOD_JSON="fabric/src/main/resources/fabric.mod.json"
if [ ! -f "$MOD_JSON" ]; then
	fail "$MOD_JSON is missing."
elif command -v python3 >/dev/null 2>&1; then
	if python3 -c "import json,sys; json.load(open('$MOD_JSON'))" 2>/tmp/fabric_mod_json_err; then
		ok "fabric.mod.json is valid JSON."
	else
		fail "fabric.mod.json is not valid JSON: $(cat /tmp/fabric_mod_json_err)"
	fi
else
	warn "python3 not found, skipping fabric.mod.json JSON validation."
fi

if [ "$FAIL" -eq 1 ]; then
	fail "Pre-flight checks failed. Aborting before build."
	exit 1
fi

# 4. Compile-only build first, so a compile error surfaces fast without
#    waiting for the client to boot.
info "Running './gradlew build' as a fast compile check..."
if ! ./gradlew build --no-daemon --console=plain; then
	fail "Build failed. See output above. Not launching the client."
	exit 1
fi
ok "Build succeeded."

# 5. Launch the requested task (default: runClient). This blocks until the
#    game window is closed.
info "Launching './gradlew $TASK' ..."
./gradlew "$TASK" --no-daemon --console=plain
GRADLE_EXIT=$?

# 6. Post-run log summary, so you don't have to dig through run/logs yourself.
info "Run finished (gradle exit code $GRADLE_EXIT). Summarizing $LOG_FILE ..."
if [ -f "$LOG_FILE" ]; then
	if grep -q "easy-sort" "$LOG_FILE"; then
		ok "'easy-sort' mod entry found in the log."
	else
		warn "'easy-sort' was NOT found in $LOG_FILE - the mod may not have loaded."
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
