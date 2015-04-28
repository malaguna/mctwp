# Complete instructions #

This instructions are checked with Ubuntu Server 10.04. Ubuntu must be installed with minimal options. I don't install graphical environment in servers machines, but you could install it, if you want.

It doesn't matter whether you choose 32 bits or 64, all software needed is compatible.

## Basic packages ##

Once Ubuntu is installed, you have to install another needed packages. In order to find all of them, it is necessary that you allow **parter** repository in _/etc/apt/sources.list_ file , make sure following packages are installed:

  * _autoconf_
  * _build-essential_
  * _libssl-dev_
  * _libapache2-mod-jk_
  * _openssh-server_
  * _sun-java6-jdk_
  * _postgresql_
  * _postgresql-contrib_
  * _libpng12-dev_
  * _pkg-config_
  * _libglib2.0-dev_
  * _libgtk2.0-dev_
  * _medcon_

## Java configuration ##

  * Create shell script _/etc/profile.d/java.sh_ and make it runnable _chmod +x_. file must contain following
```
export JAVA_HOME=/usr/lib/jvm/java-6-sun
```

## Tomcat Installation ##

Tomcat installation is done with Apache APR. This increase Tomcat performance, but you can skip if found it difficult.

### APR Installation ###

You can skip this if don't want APR.

  * Download and unpack _apr-1.X.X.tar.gz_ from [APR home](http://apr.apache.org/)
  * Get into uncompressed folder and run:
```
./configure --prefix=/usr/local/apr
```
  * then, run _make_, _make test_ and finally _sudo make install_. When running tests, you could see if everyone succeed. After running install, you could check if all binaries have been copied to _/usr/local/apr_

### Tomcat Installation ###

  * Download and unpack _apache-tomcat-6.0.X.tar.gz_ from [Tomcat home](http://tomcat.apache.org/).
  * Create a _tomcat_ user:
```
sudo adduser --no-create-home --disabled-login tomcat
```
  * Move uncompressed folder to _/opt_.
  * Create a soft link into _/opt_ called _tomcat_ that points to moved tomcat folder.
  * Create shell script _/etc/profile.d/tomcat.sh_ and make it runnable _chmod +x_ file must contain:
```
export CATALINA_HOME=/opt/tomcat
```

  * Create into folder _opt/tomcat/conf_ Following files:
    * tomcat-users.xml
```
<?xml version='1.0' encoding='utf-8'?>
<tomcat-users>
  <role rolename="manager"/>
  <user username="admin" password="inicial" roles="manager"/>
</tomcat-users>
```
    * jmxremote.access
```
monitorRole readonly
controlRole readonly
```
    * jmxremote.password
```
monitorRole inicial
controlRole inicial
```
  * Make tomcat user owner of all _/opt/tomcat_ content.

### Installing tomcat as a system service ###

This can be avoided if you don't want to run tomcat as a system service.

  * Go to _/opt/tomcat/bin_.
  * Uncompress _tomcat-native.tar.gz_.
  * Go to _/opt/tomcat/bin/tomcat-native-1.1.4-src/jni/native_.
  * Run:
```
sudo ./configure --with-apr=/usr/local/apr/bin --with-java-home=/usr/lib/jvm/java-6-sun --with-ssl=no
sudo make
sudo make install
```
  * For older Ubuntu versions, it is necessary to change file _/etc/modprobe.d/aliases_ switching alias _net-pf-10_ off.

  * Download and unpack _commons-daemon-1.X.X-src.tar.gz_ from Apache commons.
  * Get into _src/native/unix_ subdirectory and run:
```
sudo autoconf
sudo ./configure --with-java=/usr/lib/jvm/java-6-sun
sudo make
cp jsvc /opt/tomcat/bin
```

  * Copy into _/opt/tomcat_ this [shell script](TomcatScript.md). You can configure this script changing some properties as server name, and so on.
  * Create a soft link into _/etc/init.d_ called _tomcat_ that points to _/opt/tomcat/tomcat.sh_, and don't forget to give execution flag to this script.
```
sudo chmod +x /opt/tomcat/tomcat.sh
sudo ln -s /opt/tomcat/tomcat.sh /etc/init.d/tomcat
```
  * Setup as a system service script:
```
sudo update-rc.d tomcat defaults
```

## Configuring AJP ##

AJP is a protocol that makes possible to use Apache and Tomcat work together. However it is possible to use Tomcat without Apache, although I prefer to use Apache because of authentication realms.

You can use this configuration to run Apache and Tomcat in different machines, but you will have to change some properties.

If you don't want to use AJP, you need to configure user authentication into Tomcat.

  * Configure _ServerName_ directive into _/etc/apache2/httpd.conf_ defining server name, for example:
```
ServerName localhost
```
  * Change content of _/etc/libapache2-mod-jk/workers.properties_ to:
```
ps=/
worker.list=worker
worker.worker.type=ajp13
worker.worker.host=localhost
worker.worker.port=8009
worker.worker.mount=/mctwp /mctwp/*
```
  * Create file _/etc/apache2/mods-available/jk.conf_ with following content:
```
#Ubicacion del fichero workers.properties
JkWorkersFile /etc/libapache2-mod-jk/workers.properties

#Ubicación del log
JkLogFile /var/log/apache2/mod_jk.log

#Nivel de logeo
JkLogLevel info

#Formato de logeo
JkLogStampFormat "[%a %b %d %H:%M:%S %Y] "

#Opciones
JkOptions +ForwardKeySize +ForwardURICompat -ForwardDirectories

#Formato de logeo
JkRequestLogFormat "%w %V %T"
```
  * Run:
```
sudo a2dismod jk
sudo a2enmod jk
```

### Apache credentials ###

You can configure Apache to check user credentials several ways. Here it is the easiest one: to use a local file:

  * Create into _/etc/apache2_ an Apache password file called _passwd.txt_, using _htpasswd_.
```
sudo htpasswd -c /etc/apache2/passwd.txt USER_NAME
```
  * This command will prompt twice for the password. Remember that -c option must be used only when password file does not exist, if you already have an existing password file and just want to add new credential, you must remove -c option.
  * Add following directive under **ServerAdmin** in file _/etc/apache2/sites-available/default_.
```
JkMountCopy on
```
  * Add at the end of the same file, but before ending **VirtualHost** node, following directives:
```
    <Location "/mctwp">
        AuthType basic
        AuthName "mctwp"
        # Validacion contra fichero htpasswd.txt
        AuthUserFile "/etc/apache2/passwd.txt"
        Require valid-user
    </Location>

    <LocationMatch "/mctwp/jsp/research/upload/processUpload.jsp">
        Satisfy any
    </LocationMatch>
```
  * Add following attribute _tomcatAuthentication="false"_ to AJP connector node of configuration file _/opt/tomcat/conf/server.xml_.
  * Reload Apache and Tomcat:
```
sudo /etc/init.d/apache2 restart
sudo /etc/init.d/tomcat stop
sudo /etc/init.d/tomcat start
```

## eXist Installation ##

It is described [here](InstalleXist.md).

## PostgreSQL configuration ##

  * Here it is necessary to do several things.
    * First it is necessary to create a new postgres system user called _admon_. When creating the role, postgres will ask you for a password, **don't forget it**, you will need it later to configure application.
    * Then it is necessary to create the database and to enable some contrib modules.
    * Finally you have to create an admin user into users table application. To do this, you need to insert manually using SQL. The same login you choose for this user must be added to the apache password file we have already seen.
  * Here there are all the commands:
```
sudo su - postgres

createuser --no-superuser --createdb --no-createrole --login --pwprompt admon
createdb mctwp

psql -d mctwp -f /usr/share/postgresql/8.4/contrib/uuid-ossp.sql
psql -d mctwp -f /usr/share/postgresql/8.4/contrib/lo.sql

psql -d mctwp -f schema.sql
psql -d mctwp -f configuration.sql

psql -d mctwp -c "insert into usuario values(nextval('usuario_seq'), uuid_generate_v1(), 'NAME', 'SURNAME', now(), 'LOGIN', true, '');"

NOTE: If mctwp BBDD exists, you must drop it with dropdb
```
  * Now, you have to configure _/etc/postgresql/8.3/main/pg\_hba.conf_ properly, following is an untrust configuration, but you can use to test:
```
local   all         all                              trust
host    all         all         127.0.0.1/32         trust
```

## Image Storage ##

  * User _tomcat_ must own a folder where the application will store all images. You can create the folder where you want, for example _/var/lib/imgdb_. It would be ideal if this path where on NAT folder or iSCSI mounted.

## Instalar XmedCon ##

We already install (X)MedCon from Ubuntu software repositories, but if you prefer last version, or your distribution doesn't have this package, here you got how to build:

  * Download and unpack _xmedcon-0.10.4.tar.bz2_ from [(X)!MedCon page](http://xmedcon.sourceforge.net/).
  * Go to uncompressed folder and run:
```
./configure
make
sudo make install
```

## Reboot machine ##

Reboot machine and check all is OK.