@echo off
setlocal

if "%JAVA_HOME%" == "" goto NO_HOME
goto HAS_HOME

:NO_HOME
set JAVA=java

goto BUILD_COMMAND

:HAS_HOME
set JAVA="%JAVA_HOME%\bin\java.exe"

:JAVA_OPTS
set "JAVA_OPTS=-Xms256m -Xmx512m -Xss2m"
set "JAVA_OPTS=%JAVA_OPTS% -Djava.endorsed.dirs='%~dp0lib\endorsed'"
set "JAVA_OPTS=%JAVA_OPTS% -Dexist.home='%~dp0exist-home'"


:BUILD_COMMAND
set COMMAND=%JAVA% -jar "%~dp0lib\${project.artifactId}-${project.version}.jar" %JAVA_OPTS%

:COMMAND_REPEAT
  if "%~1" == "" GOTO RUN
  set COMMAND=%COMMAND% %1
  shift
goto COMMAND_REPEAT

:RUN
echo %COMMAND%
%COMMAND%

endlocal
@echo on
