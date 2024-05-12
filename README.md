# SureSIM
**LSPosed module to spoof eSIM support**

This module makes every app it's enabled for think eSIM is supported
and copies the activation code to the clipboard when the app is preparing
to install a profile.

## Building
1. Download the latest .aar file from [libxposed/api Actions runs](https://github.com/libxposed/api/actions) and save it as `app/libs/api-100.aar`
2. Use Android Studio (or gradle directly) to generate an APK file