#!/bin/bash

# ---------------------------------------------------
# this script downloads, builds and installs mevenide
# TODO manage feature deploiement
# ---------------------------------------------------

# set +v

# ----------------------
# process initialization
# ----------------------

function prepareFs {
	if test "$ECLIPSE_HOME" = "" ; then
	    noEclipseHome
	fi
	echo enter checkout folder :
	read buildDir
	if [ -d "$buildDir" ] ; then
		rm -R $buildDir
	fi
	mkdir $buildDir
	cd $buildDir
	prepareMavenOptions
}

function prepareMavenOptions {
	echo build in debug mode Y/N ?
	read debug
	if test "$debug" = "Y" || test "$debug" = "y" ; then
	    export maven_opts=-e 
	fi
	checkout
}

function checkout {
	echo checking out mevenide from cvs.sourceforge.net
	cvs -d:pserver:anonymous@cvs.sourceforge.net:/cvsroot/mevenide co .
	mv update/mevenide-update .
	mv update/mevenide-feature .
	build
}

# ---------------------
# mevenide construction
# ---------------------

function build {
	echo install maven-eclipse-plugin-plugin Y/N ?
	read installMep
	if test "$installMep" = "Y" || test "$installMep" = "y" ; then
	    buildMepPlugin
	else
	    buildMevenide
	fi
}

function buildMepPlugin {
	cd maven-plugins/maven-eclipse-plugin-plugin
	echo installing maven-eclipse-plugin-plugin
	source maven -b $maven_opts plugin:install plugin:deploy
	cd ../..
	buildMevenide
}

function buildMevenide {
	echo building mevenide with maven. it may take a few minutes. 
	cd mevenide-master
	source maven -b $maven_opts mevenide-eclipse:build-all 
	cd ../..
	shouldInstall
}

# ---------------------------------------------------------
# mevenide installation (should be handled by a maven goal)
# ---------------------------------------------------------

function shouldInstall {
	echo install mevenide for eclipse Y/N ? 
	read shouldInstall
	if test "$shouldInstall" = "Y" || test "$shouldInstall" = "y" ; then
	    installMevenide
	else
	    finalize
	fi
}

function installMevenide {
	if test "$JAVA_HOME" = "" ; then
	    noJavaHome
	fi
	#popd 
	export mevenideTempInstallDir=mevenideTempInstallDir
	cd $buildDir
	mkdir $mevenideTempInstallDir
	for f in mevenide-feature/target/eclipse/dist/plugins/*.jar ; do 
	    cp $f $mevenideTempInstallDir
	done
	cd ..
	extractJars
}

function extractJars {
	echo installing mevenide into $ECLIPSE_HOME
	cd $buildDir
	export expandedDirs=expandedDirs
	mkdir $expandedDirs
	cd $expandedDirs
	$JAVA_HOME/bin/jar xvf ../$mevenideTempInstallDir/org.mevenide.core_0.1.1.jar
	$JAVA_HOME/bin/jar xvf ../$mevenideTempInstallDir/org.mevenide.grabber_0.1.1.jar
	$JAVA_HOME/bin/jar xvf ../$mevenideTempInstallDir/org.mevenide.ui_0.1.1.jar
	cd ..
	rm -R $expandedDirs/META-INF
	cp -R $expandedDirs/* $ECLIPSE_HOME/plugins
	cd ..
	echo mevenide has been installed. 
	cleanInstallTemp
}

function cleanInstallTemp {
	cd $buildDir
	rm -R $mevenideTempInstallDir
	cd ..
	finalize
}

# --------------
# error handling
# --------------

function noEclipseHome {
	echo ECLIPSE_HOME not found. 
	echo enter the location of your Eclipse location : 
	read eclipsehome
	export ECLIPSE_HOME=$eclipsehome
	prepareFs
}

function noJavaHome {
	if test "$JAVA_HOME" = "" ; then
	    echo JAVA_HOME not found
	fi
	if ![-e $JAVA_HOME/bin/jar.exe] ; then
	    echo invalid JAVA_HOME : jar tool not found
	fi
	echo enter Java Home Directory :
	read javahome
	export JAVA_HOME=$javahome
	if [-e $JAVA_HOME/bin/jar.exe] ; then
	    installMevenide
	fi
	noJavaHome
}

# --------------------
# process finalization 
# --------------------

function finalize {
	echo drop installation files before exiting Y/N ?
	read shouldDropFiles
	if test "$shouldDropFiles" = "Y" ; then
	    rm -R $buildDir
	fi
	echo done
}


prepareFs
