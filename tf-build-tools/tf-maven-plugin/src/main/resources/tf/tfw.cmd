echo off
set ORIG=%cd%
set INPUT=%0%
set TARGET=%INPUT:tfw.cmd=%
cd %TARGET%
powershell -ep bypass -file tfw.ps1 %*
cd %ORIG%
exit /b
