@echo off
setlocal

:: Config
set SRC=test
set BUILD=testbuild
set CLASS=test.TestClient

:: Create build folder
rd /s /q %BUILD% 2>nul
mkdir %BUILD%

echo ----------------------------------------
echo Compiling TestClient.java...
javac -d %BUILD% %SRC%\TestClient.java

if %errorlevel% neq 0 (
    echo ❌ Compilation failed!
    exit /b %errorlevel%
)

echo ----------------------------------------
echo Running TestClient...
cd %BUILD%
java %CLASS%
cd ..

echo ----------------------------------------
echo ✅ Test completed.
endlocal
pause
