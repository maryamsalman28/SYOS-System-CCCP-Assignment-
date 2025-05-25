@echo off
setlocal

:: STEP 1: Set your Tomcat installation path
set "CATALINA_HOME=C:\Program Files\Apache Software Foundation\Tomcat 10.1"
set WAR_NAME=ROOT.war

echo ----------------------------------------
echo Stopping Tomcat...
call "%CATALINA_HOME%\bin\shutdown.bat"
timeout /t 3 >nul

echo ----------------------------------------
echo Deploying WAR file...
copy /Y "%WAR_NAME%" "%CATALINA_HOME%\webapps\%WAR_NAME%"

echo ----------------------------------------
echo Starting Tomcat...
call "%CATALINA_HOME%\bin\startup.bat"

echo ----------------------------------------
echo ✅ Deployment complete! Visit: http://localhost:8080/index.html
endlocal
pause
