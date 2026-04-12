# Zyvault — Android Setup Guide

## Quick Start (Copy & Paste Method)

### Step 1: Create a New Project in Android Studio

1. Open Android Studio
2. Click **File → New → New Project**
3. Select **"Empty Activity"** (the Compose one, NOT the "Empty Views Activity")
4. Set these values:
   - **Name:** `Zyvault`
   - **Package name:** `com.zyvault.app`
   - **Minimum SDK:** API 26 (Android 8.0)
   - **Build configuration language:** Kotlin DSL
5. Click **Finish** and wait for Gradle sync to complete

### Step 2: Replace the Files

Once the project is created, you'll replace the auto-generated files with the Zyvault files.

**IMPORTANT:** The base path for your source files is:
```
app/src/main/java/com/zyvault/app/
```

#### 2a. Create the folder structure
Right-click on `com.zyvault.app` in the Project panel and create these packages:
- `ui.theme`
- `ui.components`
- `ui.screens`
- `ui.navigation`

#### 2b. Replace/Create these files (in order):

| File | Location |
|------|----------|
| `Color.kt` | `ui/theme/` |
| `Theme.kt` | `ui/theme/` — **DELETE** the auto-generated `Theme.kt` first |
| `ZyvaultLogo.kt` | `ui/components/` |
| `SharedComponents.kt` | `ui/components/` |
| `SplashScreen.kt` | `ui/screens/` |
| `HomeScreen.kt` | `ui/screens/` |
| `VaultScreen.kt` | `ui/screens/` |
| `FinanceScreen.kt` | `ui/screens/` |
| `BillsScreen.kt` | `ui/screens/` |
| `ProfileScreen.kt` | `ui/screens/` |
| `BottomNavBar.kt` | `ui/navigation/` |
| `MainActivity.kt` | root of `com.zyvault.app` — **REPLACE** the existing one |

#### 2c. Update build files
- Replace `app/build.gradle.kts` with the provided version
- Replace root `build.gradle.kts` with the provided version
- Replace `app/src/main/res/values/themes.xml` with the provided version

#### 2d. Delete auto-generated files you don't need
- Delete `ui/theme/Type.kt` (if it exists)
- Delete any auto-generated `ui.theme.Color.kt` (use ours instead)
- Delete any `Greeting()` composable preview files

### Step 3: Sync & Run

1. Click **File → Sync Project with Gradle Files** (or the elephant icon)
2. Wait for sync to complete (may take 1-2 minutes first time)
3. Select a device/emulator (Pixel 6 API 33+ recommended)
4. Click the green **Run** ▶ button

### Troubleshooting

**"Unresolved reference" errors:**
- Make sure every file starts with `package com.zyvault.app.ui.xxxxx` matching its folder
- Do a full rebuild: **Build → Clean Project**, then **Build → Rebuild Project**

**Gradle sync fails:**
- Make sure you're on Android Studio Hedgehog (2023.1.1) or newer
- Check that your Kotlin version matches: `2.1.0`
- Check that AGP version matches: `8.7.3`

**Icons not found (QrCode2, Inventory2, etc.):**
- Make sure `material-icons-extended` is in your dependencies (it's in the provided build.gradle.kts)

**"dependencyResolution" error in settings.gradle.kts:**
- If your Android Studio version is older, rename `dependencyResolution` to `dependencyResolutionManagement`

## Project Structure

```
com.zyvault.app/
├── MainActivity.kt              ← App entry point
├── ui/
│   ├── theme/
│   │   ├── Color.kt             ← All Zyvault colors
│   │   └── Theme.kt             ← Material3 dark theme
│   ├── components/
│   │   ├── ZyvaultLogo.kt       ← Gravity core logo (Canvas)
│   │   └── SharedComponents.kt  ← BrandText, AlertBanner, etc.
│   ├── screens/
│   │   ├── SplashScreen.kt      ← Onboarding / Get Started
│   │   ├── HomeScreen.kt        ← Dashboard with stats & activity
│   │   ├── VaultScreen.kt       ← Document storage with filters
│   │   ├── FinanceScreen.kt     ← Bank accounts & credit cards
│   │   ├── BillsScreen.kt       ← Bill tracking & payments
│   │   └── ProfileScreen.kt     ← User profile & settings
│   └── navigation/
│       └── BottomNavBar.kt      ← Tab bar with orange indicators
```
