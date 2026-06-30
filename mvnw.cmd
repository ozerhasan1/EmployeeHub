@echo off
setlocal

set "MAVEN_VERSION=3.9.9"
set "BASE_DIR=%~dp0"

if not defined MAVEN_USER_HOME (
  set "MAVEN_USER_HOME=%USERPROFILE%\.m2"
)

set "MVNW_DIST_DIR=%MAVEN_USER_HOME%\wrapper\dists\apache-maven-%MAVEN_VERSION%"
set "MVNW_MAVEN_HOME=%MVNW_DIST_DIR%\apache-maven-%MAVEN_VERSION%"

if not exist "%MVNW_MAVEN_HOME%\bin\mvn.cmd" (
  echo Downloading Apache Maven %MAVEN_VERSION%...
  powershell -NoProfile -ExecutionPolicy Bypass -Command "$ErrorActionPreference='Stop'; $version=$env:MAVEN_VERSION; $dist=$env:MVNW_DIST_DIR; New-Item -ItemType Directory -Force -Path $dist | Out-Null; $zip=Join-Path $dist ('apache-maven-' + $version + '-bin.zip'); $url='https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/' + $version + '/apache-maven-' + $version + '-bin.zip'; Invoke-WebRequest -Uri $url -OutFile $zip; Expand-Archive -Path $zip -DestinationPath $dist -Force"
  if errorlevel 1 exit /b 1
)

call "%MVNW_MAVEN_HOME%\bin\mvn.cmd" %*
set "MVNW_EXIT_CODE=%ERRORLEVEL%"
endlocal & exit /b %MVNW_EXIT_CODE%
