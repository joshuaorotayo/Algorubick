# Publishing Algorubick to Google Play

## One-time setup

### 1. Play Console app

1. Open [Google Play Console](https://play.google.com/console) and create/select the app (`com.jorotayo.algorubickrevamped`).
2. Complete store listing, content rating, target audience, and Data safety.
3. **Setup → App signing**: use Play App Signing. Keep an offline backup of your **upload** keystore (`.jks`).

### 2. Service account (for GitHub Actions uploads)

1. In Play Console: **Setup → API access** → link a Google Cloud project.
2. Create a service account in Google Cloud with a JSON key.
3. In Play Console, grant that service account permission to **release apps to testing tracks and production** (or Admin while setting up).
4. Accept the API access invite for the service account if prompted.
5. Add the full JSON as GitHub secret `PLAY_SERVICE_ACCOUNT_JSON`.

### 3. GitHub repository secrets

| Secret | Purpose |
|--------|---------|
| `KEYSTORE_BASE64` | Base64 of your upload `.jks` |
| `STORE_PASSWORD` | Keystore password |
| `KEY_ALIAS` | Key alias |
| `KEY_PASSWORD` | Key password |
| `PLAY_SERVICE_ACCOUNT_JSON` | Service account JSON (entire file) |

Encode the keystore (PowerShell):

```powershell
[Convert]::ToBase64String([IO.File]::ReadAllBytes('C:\path\to\Algorubick_key.jks')) | Set-Clipboard
```

### 4. Local signing (optional, for laptop builds)

Copy `signing.properties.example` → `signing.properties` (gitignored) and point `STORE_FILE` at your keystore.

```bat
scripts\build-play-bundle.bat
```

---

## Release workflow (GitHub Actions)

Workflow file: [`.github/workflows/play-release.yml`](../.github/workflows/play-release.yml)

### Internal testing release

Use this for builds you want testers to install quickly from **Internal testing**.

**Actions UI**

1. **Actions → Play Release → Run workflow**
2. `release_type` = `internal`
3. Set `version_name` (e.g. `1.5.0-internal.3`)
4. Leave `version_code` blank to use the run number (must always increase)
5. Run

**Tag shortcut**

```bash
git tag internal-v1.5.0-3
git push origin internal-v1.5.0-3
```

Result:

- Signed AAB uploaded to Play track **`internal`** with status **`completed`**
- Available to users on your internal testing list
- AAB also attached as a GitHub Release (prerelease) and workflow artifact

### Official review / production draft

Use this when you are ready to send a build through **Google Play review**.

**Actions UI**

1. **Actions → Play Release → Run workflow**
2. `release_type` = `review`
3. Set `version_name` (e.g. `1.5.0`)
4. Optionally set an explicit `version_code` (must be higher than any previous upload)
5. Run

**Tag shortcut**

```bash
git tag v1.5.0
git push origin v1.5.0
```

Result:

- Signed AAB uploaded to Play track **`production`** with status **`draft`**
- Open **Play Console → Production**, open the draft, complete any checklist items, then **Send for review**
- AAB attached to a GitHub Release

> Draft (not completed) is intentional: Play review requires you to confirm the release in the console.

### Dry run

In **Run workflow**, enable `dry_run` to build and upload the AAB artifact / GitHub Release **without** calling the Play API. Useful while secrets are being set up.

---

## What's New (Play Store)

Each release uploads an en-US **What's New** blurb (max ~500 characters).

Priority order:

1. Optional `release_notes` input on the workflow (manual override)
2. Curated file [`play-listing/en-US/whatsnew`](../play-listing/en-US/whatsnew)
3. Auto-friendly summary generated from recent git commits

Update the curated file whenever you ship user-facing changes, then run **Play Release**. The workflow refreshes the title to match the release `versionName`.

Generate locally:

```bash
bash scripts/generate-whatsnew.sh 1.5.0
```


---

## After upload

| Type | Where to look |
|------|----------------|
| Internal | Play Console → **Testing → Internal testing** |
| Review | Play Console → **Production** → draft release → **Send for review** |

You can also download the AAB from the workflow **Artifacts** tab or the GitHub Release and upload manually if needed.
