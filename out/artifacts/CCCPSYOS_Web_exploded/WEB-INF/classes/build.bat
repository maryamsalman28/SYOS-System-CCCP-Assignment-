@echo off
setlocal

:: CONFIGURATION
set PROJECT_NAME=CCCPSYOS
set BUILD_DIR=build
set WEB_DIR=web
set LIB_DIR=libs
set WAR_NAME=%PROJECT_NAME%.war

echo ----------------------------------------
echo Cleaning previous build...
rd /s /q %BUILD_DIR% 2>nul
mkdir %BUILD_DIR%\WEB-INF\classes

echo ----------------------------------------
echo Finding Java source files...
(
  dir /s /b controller\*.java
  dir /s /b dao\*.java
  dir /s /b model\*.java
  dir /s /b observer\*.java
  dir /s /b service\*.java
  dir /s /b util\*.java
  if exist HelloServlet.java echo HelloServlet.java
) > sources.txt

echo ----------------------------------------
echo Compiling Java source files...
javac -d %BUILD_DIR%\WEB-INF\classes ^
  -cp "%LIB_DIR%\jakarta.servlet-api-6.1.0.jar" ^
  @sources.txt

if %errorlevel% neq 0 (
    echo ❌ Compilation failed!
    del sources.txt
    exit /b %errorlevel%
)

del sources.txt

echo ----------------------------------------
echo Copying web resources...
xcopy /s /e /y %WEB_DIR%\* %BUILD_DIR%\

echo ----------------------------------------
echo Creating WAR file...
cd %BUILD_DIR%
jar -cvf ..\%WAR_NAME% *
cd ..

echo ----------------------------------------
echo ✅ WAR file created successfully: %WAR_NAME%
endlocal
pause
