#!/usr/bin/env python3
"""Print the next Play versionCode: (max across known tracks) + 1.

If no versionCodes exist, prints DEFAULT_START (160).
Requires PLAY_SERVICE_ACCOUNT_JSON in the environment.
"""

from __future__ import annotations

import json
import os
import sys

PACKAGE_NAME = "com.jorotayo.algorubickrevamped"
TRACKS = ("internal", "production", "alpha", "beta")
DEFAULT_START = 170
SCOPE = "https://www.googleapis.com/auth/androidpublisher"


def max_version_code_on_track(service, package_name: str, track: str, edit_id: str) -> int | None:
    from googleapiclient.errors import HttpError

    try:
        track_info = (
            service.edits()
            .tracks()
            .get(packageName=package_name, editId=edit_id, track=track)
            .execute()
        )
    except HttpError as exc:
        if exc.resp is not None and exc.resp.status == 404:
            return None
        raise

    codes: list[int] = []
    for release in track_info.get("releases") or []:
        for raw in release.get("versionCodes") or []:
            try:
                codes.append(int(raw))
            except (TypeError, ValueError):
                continue
    return max(codes) if codes else None


def main() -> int:
    raw = os.environ.get("PLAY_SERVICE_ACCOUNT_JSON", "").strip()
    if not raw:
        print("PLAY_SERVICE_ACCOUNT_JSON is missing", file=sys.stderr)
        return 1

    try:
        info = json.loads(raw)
    except json.JSONDecodeError as exc:
        print(f"Invalid PLAY_SERVICE_ACCOUNT_JSON: {exc}", file=sys.stderr)
        return 1

    try:
        from google.oauth2 import service_account
        from googleapiclient.discovery import build
    except ImportError:
        print(
            "Missing google-api-python-client / google-auth. "
            "Install with: pip install google-api-python-client google-auth",
            file=sys.stderr,
        )
        return 1

    credentials = service_account.Credentials.from_service_account_info(
        info, scopes=[SCOPE]
    )
    service = build("androidpublisher", "v3", credentials=credentials, cache_discovery=False)
    edit = service.edits().insert(body={}, packageName=PACKAGE_NAME).execute()
    edit_id = edit["id"]
    try:
        found: list[int] = []
        for track in TRACKS:
            code = max_version_code_on_track(service, PACKAGE_NAME, track, edit_id)
            if code is not None:
                found.append(code)
                print(f"Track '{track}' max versionCode={code}", file=sys.stderr)
    finally:
        try:
            service.edits().delete(packageName=PACKAGE_NAME, editId=edit_id).execute()
        except Exception:  # noqa: BLE001
            pass

    if not found:
        nxt = DEFAULT_START
        print(f"No versionCodes on Play tracks; using default {nxt}", file=sys.stderr)
    else:
        last = max(found)
        nxt = last + 1
        print(f"Highest Play versionCode={last}; next={nxt}", file=sys.stderr)
    print(nxt)
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
