@rem
@rem Gradle startup script for Windows
@rem
@if "%DEBUG%"=="" @echo off

set DIRNAME=%~dp0
if "%DIRNAME%"=="" set DIRNAME=.
cd /d "%DIRNAME%"
set GRADLE_WRAPPER_JAR=%DIRNAME%gradle\wrapper\gradle-wrapper.jar

if not exist "%GRADLE_WRAPPER_JAR%" (
    echo.
    echo ERROR: gradle-wrapper.jar not found. Create Gradle Wrapper first:
    echo   1. Install Gradle from https://gradle.org/install/
    echo   2. In this folder run: gradle wrapper
    echo   Or download gradle-wrapper.jar and put in gradle\wrapper\
    echo.
    exit /b 1
)

set JAVA_EXE=java
if defined JAVA_HOME set JAVA_EXE=%JAVA_HOME%\bin\java

"%JAVA_EXE%" -jar "%GRADLE_WRAPPER_JAR%" %*
