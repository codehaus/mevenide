rmdir /S /Q build
mkdir build
cd build
cvs -d:pserver:anonymous@cvs.sourceforge.net:/cvsroot/mevenide co .
move update\mevenide-update .
move update\mevenide-feature .
cd maven-plugins\maven-eclipse-plugin-plugin
rem maven plugin:install plugin:deploy
cd ..\..\mevenide-master\
maven mevenide:build-all 
cd ..\..