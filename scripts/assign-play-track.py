#!/usr/bin/env python3
"""Assign an already-uploaded versionCode to a Play track (no AAB re-upload).

Usage:
  assign-play-track.py <track> <versionCode> [status] [releaseName]

Requires PLAY_SERVICE_ACCOUNT_JSON in the environment.
"""

from __future__ import annotations

import json
import os
import sys

PACKAGE_NAME = "com.jorotayo.algorubickrevamped"
SCOPE = "https://www.googleapis.com/auth/androidpublisher"


def main() -> int:
    if len(sys.argv) < 3:
        print(
            "Usage: assign-play-track.py <track> <versionCode> [status] [releaseName]",
            file=sys.stderr,
        )
        return 1

    track = sys.argv[1]
    version_code = sys.argv[2]
    status = sys.argv[3] if len(sys.argv) > 3 else "completed"
    release_name = sys.argv[4] if len(sys.argv) > 4 else f"{version_code}"

    raw = os.environ.get("PLAY_SERVICE_ACCOUNT_JSON", "").strip()
    if not raw:
        print("PLAY_SERVICE_ACCOUNT_JSON is missing", file=sys.stderr)
        return 1

    info = json.loads(raw)
    from google.oauth2 import service_account
    from googleapiclient.discovery import build

    credentials = service_account.Credentials.from_service_account_info(
        info, scopes=[SCOPE]
    )
    service = build("androidpublisher", "v3", credentials=credentials, cache_discovery=False)

    edit = service.edits().insert(body={}, packageName=PACKAGE_NAME).execute()
    edit_id = edit["id"]
    try:
        body = {
            "track": track,
            "releases": [
                {
                    "name": release_name,
                    "status": status,
                    "versionCodes": [str(version_code)],
                }
            ],
        }
        service.edits().tracks().update(
            packageName=PACKAGE_NAME,
            editId=edit_id,
            track=track,
            body=body,
        ).execute()
        service.edits().commit(
            packageName=PACKAGE_NAME,
            editId=edit_id,
            changesNotSentForReview=True,
        ).execute()
    except Exception:
        try:
            service.edits().delete(packageName=PACKAGE_NAME, editId=edit_id).execute()
        except Exception:  # noqa: BLE001
            pass
        raise

    print(
        f"Assigned versionCode {version_code} to track '{track}' "
        f"with status '{status}' (name={release_name})"
    )
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
