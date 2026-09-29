@echo off
setlocal
set BASE_DIR=%~dp0
set GRADLE_VERSION=9.6.0
set CACHE_DIR=%USERPROFILE%\.gradle\wrapper\dists\opponify-gradle-%GRADLE_VERSION%
set DIST=%CACHE_DIR%\gradle-%GRADLE_VERSION%
set ZIP=%CACHE_DIR%\gradle-%GRADLE_VERSION%-bin.zip
if not exist "%DIST%\bin\gradle.bat" (
  if not exist "%CACHE_DIR%" mkdir "%CACHE_DIR%"
  if not exist "%ZIP%" powershell -NoProfile -Command "Invoke-WebRequest -UseBasicParsing https://services.gradle.org/distributions/gradle-%GRADLE_VERSION%-bin.zip -OutFile '%ZIP%'"
  powershell -NoProfile -Command "Invoke-WebRequest -UseBasicParsing https://services.gradle.org/distributions/gradle-%GRADLE_VERSION%-bin.zip.sha256 -OutFile '%ZIP%.sha256'"
  powershell -NoProfile -Command "$expected=(Get-Content '%ZIP%.sha256' -Raw).Trim().ToLower(); $actual=(Get-FileHash '%ZIP%' -Algorithm SHA256).Hash.ToLower(); if($expected -ne $actual){throw 'Gradle distribution checksum mismatch'}"
  powershell -NoProfile -Command "Expand-Archive -Force '%ZIP%' '%CACHE_DIR%'"
)
call "%DIST%\bin\gradle.bat" -p "%BASE_DIR%" %*
