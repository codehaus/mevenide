@echo off

rem ---------------------------------------------------
rem this script downloads, builds and installs mevenide
rem TODO manage feature deploiement
rem ---------------------------------------------------

echo ***************************************************************************
echo *                                                                         *
echo * this script has been tested under winxp and should work under win 2000. *
echo * i dont think it is compatible with nt4 because of the set /P thingie.   *
echo *                                                                         *
echo ***************************************************************************

pause

pushd
goto prepareFs


rem ----------------------
rem process initialization
rem ----------------------

:prepareFs
if "%ECLIPSE_HOME%" == "" goto noEclipseHome
set /P buildDir=enter checkout folder :
rmdir /S /Q %buildDir%
mkdir %buildDir%
cd %buildDir%
goto prepareMavenOptions

:prepareMavenOptions
set /P debug=build in debug mode (Y/N) ?
if "%debug%" == "Y" set maven_opts = -e 
goto checkout

:checkout
echo checking out mevenide from cvs.sourceforge.net
cvs -d:pserver:anonymous@cvs.sourceforge.net:/cvsroot/mevenide co .
move update\mevenide-update .
move update\mevenide-feature .
goto build


rem ---------------------
rem mevenide construction
rem ---------------------

:build
set /P installMep=install maven-eclipse-plugin-plugin (Y/N) ?
if "%installMep%" == "Y" goto buildMepPlugin
if "%installMep%" == "N" goto buildMevenide
goto build

:buildMepPlugin
cd maven-plugins\maven-eclipse-plugin-plugin
echo installing maven-eclipse-plugin-plugin
call maven %maven_opts% plugin:install plugin:deploy
cd ..\..
goto buildMevenide

:buildMevenide
echo building mevenide with maven. it may take a few minutes. 
cd mevenide-master
call maven %maven_opts% mevenide-eclipse:build-all 
cd ..\..
goto shouldInstall


rem ---------------------------------------------------------
rem mevenide installation (should be handled by a maven goal)
rem ---------------------------------------------------------

:shouldInstall
set /P shouldInstall=install mevenide for eclipse (Y/N) ? 
if "%shouldInstall%" == "Y" goto installMevenide
if "%shouldInstall%" == "N" goto finalize
goto installMevenide

:installMevenide
if "%JAVA_HOME%" == "" goto noJavaHome
popd 
set mevenideTempInstallDir=mevenideTempInstallDir
mkdir %buildDir%\%mevenideTempInstallDir%  
for %%f in ( %buildDir%\mevenide-feature\target\eclipse\dist\plugins\*.jar ) do copy %%f %buildDir%\%mevenideTempInstallDir%
goto extractJars

:extractJars
echo installing mevenide into %ECLIPSE_HOME%
set expandedDirs=%buildDir%\expandedDirs
mkdir %expandedDirs%
cd %expandedDirs%
call %JAVA_HOME%\bin\jar xvf ..\%mevenideTempInstallDir%\org.mevenide.core_0.1.1.jar
call %JAVA_HOME%\bin\jar xvf ..\%mevenideTempInstallDir%\org.mevenide.grabber_0.1.1.jar
call %JAVA_HOME%\bin\jar xvf ..\%mevenideTempInstallDir%\org.mevenide.ui_0.1.1.jar
cd ..\..
rmdir /S /Q %expandedDirs%\META-INF
xcopy /S /E %expandedDirs% %ECLIPSE_HOME%\plugins
echo mevenide has been installed. 
goto cleanInstallTemp

:cleanInstallTemp
rmdir /S /Q %buildDir%\%mevenideTempInstallDir%
goto finalize


rem --------------
rem error handling
rem --------------

:noEclipseHome
echo ECLIPSE_HOME not found. 
set /P eclipsehome=enter the location of your Eclipse location : 
set ECLIPSE_HOME = %eclipsehome%
goto prepareFs

:noJavaHome
if "%JAVA_HOME%" == "" echo JAVA_HOME not found
if NOT EXIST "%JAVA_HOME%\bin\jar.exe" echo invalid JAVA_HOME : jar executable not found
set /P javahome=enter Java Home Directory :
set JAVA_HOME=%javahome%
if EXIST "%JAVA_HOME%\bin\jar.exe" goto installMevenide
goto noJavaHome

rem --------------------
rem process finalization 
rem --------------------

:finalize
set /P shouldDropFiles=drop installation files before exiting (Y/N) ?
if  "%shouldDropFiles%" == "Y" rmdir /S /Q %buildDir%
pause