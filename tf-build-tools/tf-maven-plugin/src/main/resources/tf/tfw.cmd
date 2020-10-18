@echo off
set THIS_CMD=%0%
set TF_BIN=%THIS_CMD:tfw=%
powershell -ep bypass -file %TF_BIN%tfw.ps1 %*
exit /b
