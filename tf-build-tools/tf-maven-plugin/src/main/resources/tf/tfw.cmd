echo off
set ORIG=%cd%
set INPUT=%0%
set TARGET=%INPUT%\..
cd %TARGET%
powershell -ep bypass -file %0\..\tfw.ps1 %*
cd %ORIG%
exit /b
