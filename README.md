# Algorubick

**One stop hub for all things cubing.**

Android app for Rubik’s Cube (and related puzzles) — save and study algorithms, run timed solves, browse 3×3 notation, and build multi-step solution guides. Everything stays on-device.

**Package:** `com.jorotayo.algorubickrevamped` · **Version:** 1.6.1 (versionCode 161)

## Features

- **Algorithm library** — create and edit algs with name, moves, description, category, optional case image, favourites, and learnt flags; search and sort on Home
- **Learn & Practice** — quiz yourself on move sequences with a custom notation keyboard; track correct vs practiced counts; multi-select algs from Home for a session
- **Timer** — scramble, tap to start/stop, save / +2 / DNF / delete; live averages; stats by cube size (best, worst, mean, Ao5–Ao100)
- **Cube sizes** — 3×3 through 9×9, Megaminx, Pyraminx, plus custom sizes you add
- **Notation guide** — 3×3 reference: intro, faces, moves, doubles, two-layer, slices, rotations, and algorithms
- **Solution guides** — author multi-step methods (name, creator, description, icons, per-step algs and images)
- **Local storage** — ObjectBox persistence; Material 3 UI with system / light / dark theme via Settings
- **Settings** — display theme previews, practice sounds & vibration packs, auto-learnt accuracy threshold

Default algorithm categories include Cross, F2L, OLL, PLL, EOLL, Triggers, and Default.

## Requirements

- Android 6.0+ (API 23)
- For development: Android Studio (or JDK 17 + Android SDK), compileSdk 36

## Tech stack

- Kotlin · Jetpack Compose · Material 3 · Navigation Compose
- ObjectBox (local database)
- Coil / Glide (images)
- Android Gradle Plugin 8.9 · Kotlin 2.0 · Compose BOM 2026.01

## Project layout (high level)

```
app/src/main/java/.../   Compose UI (home, timer, notation, solutions, study)
docs/                    Publishing guide
play-listing/            Play Store What’s New
scripts/                 Local release helpers
.github/workflows/       Play Release CI
```

## License

No license file is included yet. All rights reserved by the author unless otherwise stated.
