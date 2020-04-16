@REM ----------------------------------------------------------------------------
@REM Terraform Install script
@REM
@REM Required ENV vars:
@REM JAVA_HOME - location of a JDK home dir
@REM
@REM Optional ENV vars
@REM M2_HOME - location of maven2's installed home dir
@REM MAVEN_BATCH_ECHO - set to 'on' to enable the echoing of the batch commands
@REM MAVEN_BATCH_PAUSE - set to 'on' to wait for a keystroke before ending
@REM MAVEN_OPTS - parameters passed to the Java VM when running Maven
@REM     e.g. to debug Maven itself, use
@REM set MAVEN_OPTS=-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8000
@REM MAVEN_SKIP_RC - flag to disable loading of mavenrc files
@REM ----------------------------------------------------------------------------

@REM Begin all REM lines with '@' in case MAVEN_BATCH_ECHO is 'on'
echo off
setlocal EnableDelayedExpansion
set DOWNLOAD_URL=
set DOWNLOAD_SITE=
set RELEASE_DIR=
set RELEASE_NAME=
set RELEASE_VER=
set RELEASE_OS=
set RELEASE_SUFFIX=
set RELEASE_PATH=

@REM set title of command window
title %0
@REM echo %1
if NOT "%1" == "" (
set DOWNLOAD_URL=%1
)
echo %DOWNLOAD_URL%

@REM enable echoing by setting MAVEN_BATCH_ECHO to 'on'
@if "%MAVEN_BATCH_ECHO%" == "on"  echo %MAVEN_BATCH_ECHO%

@REM set %HOME% to equivalent of $HOME
if "%HOME%" == "" (set "HOME=%HOMEDRIVE%%HOMEPATH%")

@REM Execute a user defined script before this one
if not "%MAVEN_SKIP_RC%" == "" goto skipRcPre
@REM check for pre script, once with legacy .bat ending and once with .cmd ending
if exist "%HOME%\mavenrc_pre.bat" call "%HOME%\mavenrc_pre.bat"
if exist "%HOME%\mavenrc_pre.cmd" call "%HOME%\mavenrc_pre.cmd"
:skipRcPre

@setlocal

set ERROR_CODE=0

@REM To isolate internal variables from possible post scripts, we use another setlocal
@setlocal

@REM ==== START VALIDATION ====
if not "%JAVA_HOME%" == "" goto OkJHome

echo.
echo Error: JAVA_HOME not found in your environment. >&2
echo Please set the JAVA_HOME variable in your environment to match the >&2
echo location of your Java installation. >&2
echo.
goto error

:OkJHome
if exist "%JAVA_HOME%\bin\java.exe" goto init

echo.
echo Error: JAVA_HOME is set to an invalid directory. >&2
echo JAVA_HOME = "%JAVA_HOME%" >&2
echo Please set the JAVA_HOME variable in your environment to match the >&2
echo location of your Java installation. >&2
echo.
goto error

@REM ==== END VALIDATION ====

:init

@REM Find the project base dir, i.e. the directory that contains the folder ".mvn".
@REM Fallback to current working directory if not found.

set MAVEN_PROJECTBASEDIR=%MAVEN_BASEDIR%
IF NOT "%MAVEN_PROJECTBASEDIR%"=="" goto endDetectBaseDir

set EXEC_DIR=%CD%
set WDIR=%EXEC_DIR%
:findBaseDir
IF EXIST "%WDIR%"\.mvn goto baseDirFound
cd ..
IF "%WDIR%"=="%CD%" goto baseDirNotFound
set WDIR=%CD%
goto findBaseDir

:baseDirFound
set MAVEN_PROJECTBASEDIR=%WDIR%
cd "%EXEC_DIR%"
goto endDetectBaseDir

:baseDirNotFound
set MAVEN_PROJECTBASEDIR=%EXEC_DIR%
cd "%EXEC_DIR%"

:endDetectBaseDir

IF NOT EXIST "%MAVEN_PROJECTBASEDIR%\.mvn\jvm.config" goto endReadAdditionalConfig

@setlocal EnableExtensions EnableDelayedExpansion
for /F "usebackq delims=" %%a in ("%MAVEN_PROJECTBASEDIR%\.mvn\jvm.config") do set JVM_CONFIG_MAVEN_PROPS=!JVM_CONFIG_MAVEN_PROPS! %%a
@endlocal & set JVM_CONFIG_MAVEN_PROPS=%JVM_CONFIG_MAVEN_PROPS%

:endReadAdditionalConfig

SET MAVEN_JAVA_EXE="%JAVA_HOME%\bin\java.exe"
@REM set WRAPPER_JAR="%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.jar"
set WRAPPER_JAR="C:\temp\terraform_0.12.24_windows_amd64.zip"
set WRAPPER_LAUNCHER=org.apache.maven.wrapper.MavenWrapperMain

@REM set DOWNLOAD_URL="https://releases.hashicorp.com/terraform/0.12.24/terraform_0.12.24_windows_amd64.zip"
@REM set DOWNLOAD_URL="https://repo.maven.apache.org/maven2/io/takari/maven-wrapper/0.5.6/maven-wrapper-0.5.6.jar"

echo "before"

@REM if not exist DOWNLOAD_URL (
@REM if DOWNLOAD_URL == "" (
if not defined DOWNLOAD_URL (
   FOR /F "tokens=1,2 delims==" %%A IN (%MAVEN_PROJECTBASEDIR%\terraform-maven.properties) DO (
       IF "%%A"=="distributionUrl" (SET DOWNLOAD_URL=%%B) )
)
if not defined DOWNLOAD_SITE (
   FOR /F "tokens=1,2 delims==" %%A IN (%MAVEN_PROJECTBASEDIR%\terraform-maven.properties) DO (
       IF "%%A"=="distributionSite" (SET DOWNLOAD_SITE=%%B) )
)
if not defined RELEASE_DIR (
   FOR /F "tokens=1,2 delims==" %%A IN (%MAVEN_PROJECTBASEDIR%\terraform-maven.properties) DO (
       IF "%%A"=="releaseDir" (SET RELEASE_DIR=%%B) )
)
if not defined RELEASE_NAME (
   FOR /F "tokens=1,2 delims==" %%A IN (%MAVEN_PROJECTBASEDIR%\terraform-maven.properties) DO (
       IF "%%A"=="releaseName" (SET RELEASE_NAME=%%B) )
)
if not defined RELEASE_VER (
   FOR /F "tokens=1,2 delims==" %%A IN (%MAVEN_PROJECTBASEDIR%\terraform-maven.properties) DO (
       IF "%%A"=="releaseVer" (SET RELEASE_VER=%%B) )
)
if not defined RELEASE_OS (
   FOR /F "tokens=1,2 delims==" %%A IN (%MAVEN_PROJECTBASEDIR%\terraform-maven.properties) DO (
       IF "%%A"=="releaseOS" (SET RELEASE_OS=%%B) )
)
if not defined RELEASE_SUFFIX (
   FOR /F "tokens=1,2 delims==" %%A IN (%MAVEN_PROJECTBASEDIR%\terraform-maven.properties) DO (
       IF "%%A"=="releaseSuffix" (SET RELEASE_SUFFIX=%%B) )
)

(set RELEASE_PATH=%DOWNLOAD_SITE%/%RELEASE_DIR%/%RELEASE_VER%/%RELEASE_NAME%_%RELEASE_VER%_%RELEASE_OS%_%RELEASE_SUFFIX%)
@REM distributionUrl=https://releases.hashicorp.com/terraform/0.12.24/terraform_0.12.24_windows_amd64.zip
(set FILE_NAME="\terraform-maven.properties")
(set OVERALL_DIR="%MAVEN_PROJECTBASEDIR%%FILE_NAME%")

@REM set %RELEASE_PATH%=%~1
@REM set RELEASE_PATH=%RELEASE_PATH%:"=%
set RELEASE_PATH=%RELEASE_PATH:"=%

@REM releaseDir="terraform"
@REM releaseName="terraform"
@REM releaseVer="0.12.24"
@REM releaseOS="windows"
@REM releaseSuffix="amd64.zip"

echo "after"
echo %DOWNLOAD_URL%
echo %DOWNLOAD_SITE%
echo %RELEASE_DIR%
echo %RELEASE_NAME%
echo %RELEASE_VER%
echo %RELEASE_OS%
echo %RELEASE_SUFFIX%
echo %RELEASE_PATH%
@REM echo %OVERALL_DIR%

@REM exit /b
@REM FOR /F "tokens=1,2 delims==" %%A IN ("%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.properties") DO (
@REM     IF "%%A"=="wrapperUrl" SET DOWNLOAD_URL=%%B
)

@REM Extension to allow automatically downloading the maven-wrapper.jar from Maven-central
@REM This allows using the maven wrapper in projects that prohibit checking in binary data.
if exist %WRAPPER_JAR% (
    if "%MVNW_VERBOSE%" == "true" (
        echo Found %WRAPPER_JAR%
    )
) else (
    if not "%MVNW_REPOURL%" == "" (
        SET DOWNLOAD_URL="https://releases.hashicorp.com/terraform/0.12.24/terraform_0.12.24_windows_amd64.zip"
    )
    if "%MVNW_VERBOSE%" == "true" (
        echo Couldn't find %WRAPPER_JAR%, downloading it ...
        echo Downloading from: %DOWNLOAD_SITE%
    )

    powershell -Command "&{"^
               "$WebClient = New-Object System.Net.WebClient;"^
               "$webclient.DownloadFile('%RELEASE_PATH%', '%WRAPPER_JAR%')"^
		"}"

    powershell -Command "&{"^
             "Expand-Archive -LiteralPath '%WRAPPER_JAR%' -DestinationPath D:\test_ps"^
		"}"

    powershell -Command "&{"^
           "$oldpath = (Get-ItemPropertyValue -Path 'HKLM:\System\CurrentControlSet\Control\Session Manager\Environment' -Name PATH);"^
           "$newpath = $oldpath + ';D:\test_ps';"^
           "Set-ItemProperty -Path 'Registry::HKEY_LOCAL_MACHINE\System\CurrentControlSet\Control\Session Manager\Environment' -Name PATH -Value $newPath"^
		"}"


@REM "$oldpath = (Get-ItemProperty -Path 'Registry::HKEY_LOCAL_MACHINE\System\CurrentControlSet\Control\Session Manager\Environment' -Name PATH).path"^




@REM  [string]::IsNullOrEmpty('%MVNW_PASSWORD%'))) {"^
@REM 		"$webclient.Credentials = new-object System.Net.NetworkCredential('%MVNW_USERNAME%', '%MVNW_PASSWORD%');"^
@REM 		"}"^
@REM 		"[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; $webclient.DownloadFile('%DOWNLOAD_URL%', '%WRAPPER_JAR%')"^
@REM 		"}"
    if "%MVNW_VERBOSE%" == "true" (
        echo Finished downloading %WRAPPER_JAR%
    )
)
@REM End of extension

@REM https://releases.hashicorp.com/terraform/0.12.24/terraform_0.12.24_windows_amd64.zip

@REM Provide a "standardized" way to retrieve the CLI args that will
@REM work with both Windows and non-Windows executions.
set MAVEN_CMD_LINE_ARGS=%*

%MAVEN_JAVA_EXE% %JVM_CONFIG_MAVEN_PROPS% %MAVEN_OPTS% %MAVEN_DEBUG_OPTS% -classpath %WRAPPER_JAR% "-Dmaven.multiModuleProjectDirectory=%MAVEN_PROJECTBASEDIR%" %WRAPPER_LAUNCHER% %MAVEN_CONFIG% %*
if ERRORLEVEL 1 goto error
goto end

:error
set ERROR_CODE=1

:end
@endlocal & set ERROR_CODE=%ERROR_CODE%

if not "%MAVEN_SKIP_RC%" == "" goto skipRcPost
@REM check for post script, once with legacy .bat ending and once with .cmd ending
if exist "%HOME%\mavenrc_post.bat" call "%HOME%\mavenrc_post.bat"
if exist "%HOME%\mavenrc_post.cmd" call "%HOME%\mavenrc_post.cmd"
:skipRcPost

@REM pause the script if MAVEN_BATCH_PAUSE is set to 'on'
if "%MAVEN_BATCH_PAUSE%" == "on" pause

if "%MAVEN_TERMINATE_CMD%" == "on" exit %ERROR_CODE%

exit /B %ERROR_CODE%
