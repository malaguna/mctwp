mvn org.apache.maven.plugins:maven-install-plugin:2.3.1:install-file -Dfile=../libs/exist-1.2.jar -DgroupId=org.exist -DartifactId=exist -Dversion=1.2 -Dpackaging=jar -DlocalRepositoryPath=libs

mvn org.apache.maven.plugins:maven-install-plugin:2.3.1:install-file -Dfile=../libs/xmldb-1.0.jar -DgroupId=org.xmldb -DartifactId=xmldb -Dversion=1.0 -Dpackaging=jar -DlocalRepositoryPath=libs

mvn org.apache.maven.plugins:maven-install-plugin:2.3.1:install-file -Dfile=../libs/xmlrpc-1.2.jar -DgroupId=org.apache.xmlrpc -DartifactId=xmlrpc-patched -Dversion=1.2 -Dpackaging=jar -DlocalRepositoryPath=libs
