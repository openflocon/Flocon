---
title: Install on macOS
description: Install Flocon Desktop on macOS with Homebrew or the DMG, and authorize it with Gatekeeper.
---

# 🍏 Install on macOS

## 🍺 Homebrew (recommended)

Installing with Homebrew skips the Gatekeeper prompt entirely:

```bash
brew install --cask openflocon/tap/flocon
```

Update with `brew upgrade --cask flocon`.

---

## 💿 DMG

Download the DMG of your Mac from the [releases page](https://github.com/openflocon/Flocon/releases), open it and drag `Flocon.app` to `/Applications`:

| Mac | File |
|---|---|
| Apple silicon (M1 and later) | `Flocon-macos-arm64.dmg` |
| Intel | `Flocon-macos-x64.dmg` |

Because the DMG comes from GitHub Releases and not the Mac App Store, macOS Gatekeeper shows a security prompt on the first launch. Authorize the app with one of the two options below.

### ⚡ Quick Fix (Terminal)

Remove the quarantine flag:

```bash
xattr -cr /Applications/Flocon.app
```

### 🛠️ Manual Authorization (System Settings)

If you prefer to authorize through the macOS UI:

1. **Attempt Launch**: Move `Flocon.app` to your `/Applications` folder and open it. macOS will warn that it cannot be opened.
   
    <img src="https://github.com/user-attachments/assets/5317fa6c-4cdc-4582-bc3d-059e66b0713f" width="380" style="border-radius: 6px;"/>

2. **Open Help**: Click the **`?`** icon in the dialog.

    <img src="https://github.com/user-attachments/assets/9d0e2f08-69a1-4510-9228-cdd6f9a77dd4" width="380" style="border-radius: 6px;"/>

3. **Open Privacy Settings**: Click the link **"Open Privacy & Security for me"**.

    <img src="https://github.com/user-attachments/assets/aa2a40f7-2fd8-4243-be5e-8982c9260d1f" width="380" style="border-radius: 6px;"/>

4. **Click Open Anyway**: Scroll down to the **Security** section and click **Open Anyway**.

    <img src="https://github.com/user-attachments/assets/b5ba299c-4534-449b-9926-4075be8ba351" width="380" style="border-radius: 6px;"/>

5. **Confirm Launch**: Click **Open Anyway** in the confirmation dialog and authenticate with your password or Touch ID.

    <img src="https://github.com/user-attachments/assets/d5fb369d-3301-4489-8fc2-1ef6b0a1f52b" width="380" style="border-radius: 6px;"/>

Flocon Desktop is now permanently authorized on your Mac!

---

## 📱 Android: adb

To reach Android emulators and USB devices, Flocon Desktop needs `adb` in your `PATH`. It comes with the Android SDK platform-tools (installed with Android Studio), or with Homebrew:

```bash
brew install --cask android-platform-tools
```

Next: [add the SDK to your app](setup.md#2-add-the-sdk-to-your-app).
