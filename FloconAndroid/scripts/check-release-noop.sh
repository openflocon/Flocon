#!/usr/bin/env bash
# Checks that flocon-no-op turns Flocon off in the release build of the Android sample:
# R8 must have removed the Flocon implementation, and the merged manifest must not contain
# what the debug modules add.
# Run from FloconAndroid/ after `./gradlew :sample:android:assembleRelease`.
set -euo pipefail

build="sample/android/build"
mapping="$build/outputs/mapping/release/mapping.txt"
manifest="$build/intermediates/merged_manifest/release/processReleaseMainManifest/AndroidManifest.xml"

for f in "$mapping" "$manifest"; do
  if [[ ! -f "$f" ]]; then
    echo "Missing $f: run ./gradlew :sample:android:assembleRelease first (with R8 on)"
    exit 1
  fi
done

failed=0

# Flocon classes R8 kept: the plugin implementations, the connection and the websocket must be gone
kept=$(grep -E '^io\.github\.openflocon\.flocon\.[^ ]+ -> ' "$mapping" | sed 's/ -> .*//' \
  | grep -E 'PluginImpl|\.flocon\.client\.|\.flocon\.websocket\.|FloconBroadcastReceiver' || true)
if [[ -n "$kept" ]]; then
  echo "R8 kept Flocon implementation classes in the release build:"
  echo "$kept"
  failed=1
fi

# The switch itself must be folded away by the -assumevalues rule: if R8 keeps FloconRuntime.isEnabled(),
# some call was not folded and the code behind it may still be in the app
switch=$(awk '/^io\.github\.openflocon\.flocon\.FloconRuntime -> /{p=1; next} p && /^[^ ]/{p=0} p && /isEnabled/' "$mapping")
if [[ -n "$switch" ]]; then
  echo "R8 kept FloconRuntime.isEnabled(): the -assumevalues rule of flocon-no-op did not fold every call:"
  echo "$switch"
  failed=1
fi

# Manifest entries of the debug modules
leaked=$(grep -E 'FloconBroadcastReceiver|WRITE_SECURE_SETTINGS|com\.google\.mlkit\.vision\.DEPENDENCIES' "$manifest" || true)
if [[ -n "$leaked" ]]; then
  echo "The release manifest contains entries of the Flocon debug modules:"
  echo "$leaked"
  failed=1
fi

if [[ $failed -eq 0 ]]; then
  echo "OK: Flocon is turned off in the release build"
fi
exit $failed
