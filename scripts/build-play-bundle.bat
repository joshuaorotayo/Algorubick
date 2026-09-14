@echo off
setlocal

REM Builds a signed Play App Bundle (AAB) using local signing.properties
REM Output: app\build\outputs\bundle\release\app-release.aab

if not exist "signing.properties" (
  echo Missing signing.properties
  echo Copy signing.properties.example to signing.properties and fill in your keystore details.
  exit /b 1
)

call gradlew.bat :app:bundleRelease --stacktrace
if errorlevel 1 exit /b 1

echo.
echo AAB ready:
dir /b app\build\outputs\bundle\release\*.aab
echo.
echo Upload that file in Google Play Console ^> Your app ^> Production/Testing ^> Create release.
endlocal
