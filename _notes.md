

# Notes

## Using https://jules.google.com/

## Download latest APK
https://github.com/jmasalma/AdhanAlarm2/releases/latest


## Increment tag

```bash

git pull --prune
latest_tag=$(git describe --abbrev=0 --tags)
new_tag=$(echo "$latest_tag" | awk -F'.' -v OFS='.' '{$NF++; print $0}')
echo Moving tag from ${latest_tag} to ${new_tag}

git tag ${new_tag}
git push origin --tags

```



## ToDo

### Fixs
Done - when the app first starts after being installed or data cleared, the today tab is empty.  It populates after I go to settings an click get GPS Location

### Fix notifications
Done - Remove it
Done - Ask AI to add it again
Done ? - seems to be working now! - Not working, could be because latest android does not show notifications form sideloaded app, try on an older android...

### Fix widgets
Done - Remove it and then ask AI to add it again.
Done - Make sure widget is updated when something changes in the app setting, e.g. GPS location, calculation method, etc.
Done - Make widget style/color to match app
? - Adjust size of widgets so the text shrinks and grows based on size of the widget.
Done - Make smaller widget match app style and make it a 1x1 if possible
Done - Make larger widget default to 4x4
- Make widget narrower
- reformat 1x1 widget

### Fix automatic calculation method by region
Done - Make ISNA the default calculation method
- Fix so the right calculation method is used per region

### Remove localization, english only for now...
Done - Can add it later...
- start with arabic, etc.

### Harden
- Add tests


## Add the following setup scripts

https://jules.google.com/repo/github/jmasalma/AdhanAlarm2/config
```bash
#!/bin/bash

# Exit immediately if a command exits with a non-zero status.
set -e

# --- 1. Set up constants and directory structure ---
ANDROID_HOME="$HOME/android-sdk"
CMDLINE_TOOLS_URL="https://dl.google.com/android/repository/commandlinetools-linux-13114758_latest.zip"
TOOLS_ZIP="commandlinetools.zip"

echo "Creating Android SDK directory at $ANDROID_HOME..."
mkdir -p "$ANDROID_HOME"
mkdir -p "$ANDROID_HOME/cmdline-tools"
cd "$ANDROID_HOME"

# --- 2. Download and extract command-line tools ---
echo "Downloading Android SDK Command-line Tools..."
wget --output-document="$TOOLS_ZIP" "$CMDLINE_TOOLS_URL"
unzip "$TOOLS_ZIP" -d cmdline-tools/latest
rm "$TOOLS_ZIP"

# --- 3. Configure the command-line tool structure ---
# Required directory structure: .../cmdline-tools/latest/bin
echo "Configuring command-line tool directory structure..."
mv "$ANDROID_HOME/cmdline-tools/latest/cmdline-tools/"* "$ANDROID_HOME/cmdline-tools/latest/"
rm -rf "$ANDROID_HOME/cmdline-tools/latest/cmdline-tools"




# --- 5. Accept licenses ---
echo "Accepting Android SDK licenses..."
yes | "$ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager" --licenses







# --- 4. Install SDK packages (using separate commands) ---
echo "Installing Android SDK packages individually..."
# Ensure the sdkmanager is executable
chmod +x "$ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager"

# Install platform-tools separately
echo "Installing platform-tools..."
"$ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager" "platform-tools"

"$ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager" "platform-tools" "platforms;android-33"


# Install build-tools
echo "Installing build-tools;33.0.0..."
"$ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager" "build-tools;33.0.0"

# Install a recent Android platform
echo "Installing platforms;android-34..."
"$ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager" "platforms;android-34"

# Install build-tools
echo "Installing build-tools;34.0.0..."
"$ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager" "build-tools;34.0.0"


# --- 6. Set environment variables ---
echo "Setting environment variables..."
echo "export ANDROID_HOME=$ANDROID_HOME" >> "$HOME/.bashrc"
echo "export PATH=\$PATH:\$ANDROID_HOME/cmdline-tools/latest/bin:\$ANDROID_HOME/platform-tools" >> "$HOME/.bashrc"

# Refresh the shell's environment variables
source "$HOME/.bashrc"








# --- 6.5. My stuff
cat "$HOME/.bashrc"
echo $ANDROID_HOME
ls -latrh $ANDROID_HOME
ls -latrh $ANDROID_HOME/cmdline-tools
ls -latrh $ANDROID_HOME/cmdline-tools/latest
ls -latrh $ANDROID_HOME/cmdline-tools/latest/*


# --- 7. Verification ---
echo "Android SDK setup complete. Verifying installation..."
"$ANDROID_HOME/platform-tools/adb" --version
echo "Verification complete. adb command should be available in new shells."


```


