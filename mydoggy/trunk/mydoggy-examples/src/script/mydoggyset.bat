@echo off

::----------------------------------------------------------------------
:: MyDoggySet Startup Script for Windows
::----------------------------------------------------------------------

IF "%MYDOGGY_JDK%" == "" SET MYDOGGY_JDK=%JAVA_HOME%
IF "%MYDOGGY_JDK%" == "" goto error

SET MYDOGGY_HOME=..

SET JAVA_EXE=%MYDOGGY_JDK%\jre\bin\java.exe
IF NOT EXIST "%JAVA_EXE%" goto error

SET MYDOGGYSET_MCN=org.noos.xing.mydoggy.mydoggyset.MyDoggySet

set REQUIRED_MYDOGGYSET_JVM_ARGS=-Dsun.java2d.noddraw=true -Djava.library.path=%MYDOGGY_HOME%\lib\win32
SET JVM_ARGS=%REQUIRED_MYDOGGYSET_JVM_ARGS%

SET OLD_PATH=%PATH%
SET PATH=%MYDOGGY_HOME%\bin;%PATH%

SET CLASS_PATH=%MYDOGGY_HOME%\lib\mydoggy-api-1.5.0.jar
SET CLASS_PATH=%CLASS_PATH%;%MYDOGGY_HOME%\lib\mydoggy-plaf-1.5.0.jar
SET CLASS_PATH=%CLASS_PATH%;%MYDOGGY_HOME%\lib\mydoggy-res-1.5.0.jar
SET CLASS_PATH=%CLASS_PATH%;%MYDOGGY_HOME%\lib\mydoggy-itest-1.5.0.jar
SET CLASS_PATH=%CLASS_PATH%;%MYDOGGY_HOME%\lib\mydoggy-examples-1.5.0.jar
SET CLASS_PATH=%CLASS_PATH%;%MYDOGGY_HOME%\lib\TableLayout-20050920.jar

"%JAVA_EXE%" %JVM_ARGS% -cp "%CLASS_PATH%" %MYDOGGYSET_MCN% %*

SET PATH=%OLD_PATH%
goto end

:error
echo --------------------------------------------------------------------
echo ERROR: cannot start MyDoggySet.
echo No JDK found to run. Please validate either MYDOGGY_JDK or JAVA_HOME
echo points to valid installation path.
echo --------------------------------------------------------------------
pause

:end
