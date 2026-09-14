#!/usr/bin/env bash
# Builds a Play Store "What's New" blurb (max ~500 chars).
# Prefer curated play-listing/en-US/whatsnew, then optional override text,
# then a friendly summary generated from recent git history.
set -euo pipefail

VERSION_NAME="${1:?version name required}"
OVERRIDE_NOTES="${2:-}"
OUT_FILE="${3:-play-listing/en-US/whatsnew}"
CURATED="play-listing/en-US/whatsnew"
MAX_CHARS=500

mkdir -p "$(dirname "$OUT_FILE")"

friendly_from_git() {
  local range commits
  if git describe --tags --abbrev=0 >/dev/null 2>&1; then
    range="$(git describe --tags --abbrev=0)..HEAD"
  else
    range="HEAD~30..HEAD"
  fi

  commits="$(
    git log --pretty=format:'%s' "$range" 2>/dev/null \
      | grep -viE '^(merge|wip|tmp|chore:|bump )' \
      | head -n 12 \
      || true
  )"

  {
    echo "What's new in Algorubick ${VERSION_NAME}"
    echo ""
    if [[ -n "${commits}" ]]; then
      while IFS= read -r line; do
        [[ -z "${line}" ]] && continue
        # Soften commit-ish prefixes into store-friendly bullets.
        cleaned="$(echo "${line}" | sed -E 's/^[A-Za-z0-9._-]+:[[:space:]]*//; s/\.$//')"
        echo "• ${cleaned}"
      done <<< "${commits}"
    else
      echo "• Performance and reliability improvements"
      echo "• UI polish across core screens"
      echo "• Bug fixes for a smoother solve session"
    fi
    echo ""
    echo "Thanks for cubing with us — keep chasing those PBs!"
  }
}

trim_to_limit() {
  local text="$1"
  if ((${#text} <= MAX_CHARS)); then
    printf '%s' "${text}"
    return
  fi
  printf '%s' "${text:0:MAX_CHARS-1}…"
}

NOTES=""

if [[ -n "${OVERRIDE_NOTES}" && "${OVERRIDE_NOTES}" != "Bug fixes and improvements." ]]; then
  NOTES="$(printf '%s\n\n%s\n' "What's new in Algorubick ${VERSION_NAME}" "${OVERRIDE_NOTES}")"
elif [[ -f "${CURATED}" ]] && [[ -s "${CURATED}" ]]; then
  # Refresh the title line to match this release version if needed.
  NOTES="$(sed -E "1s/^What's new in Algorubick .*/What's new in Algorubick ${VERSION_NAME}/" "${CURATED}")"
else
  NOTES="$(friendly_from_git)"
fi

# Normalize newlines and enforce Play Console length.
NOTES="$(printf '%s\n' "${NOTES}" | sed -E 's/[[:space:]]+$//')"
NOTES="$(trim_to_limit "${NOTES}")"

printf '%s\n' "${NOTES}" > "${OUT_FILE}"
echo "Wrote What's New (${#NOTES} chars) → ${OUT_FILE}"
echo "-----"
cat "${OUT_FILE}"
echo "-----"
