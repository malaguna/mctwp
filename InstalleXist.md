# Installing eXist #

These instructions are for Ubuntu Server, 32 and 64 bits. It will install **eXist** into _/var/lib/xmldb_ path, but you can change it, if you want.

  * Create user _xmldb_:
```
sudo adduser --home /var/lib/xmldb --disabled-login xmldb
```
  * Install eXist (version 1.2):
```
sudo java -jar eXist-setup-1.2.X-revXXXX.jar -p /var/lib/xmldb
```
  * Edit _/var/lib/xmldb/tools/wrapper/conf/wrapper.conf_:
    * Change property _wrapper.app.parameter.1_ to value _standalone_.
    * Comment property _wrapper.app.parameter.2_.
    * Change property _wrapper.java.maxmemory_ to 512 Mb or whatever you want.
  * Make symbolic link into _/etc/init.d_ to _/var/lib/xmldb/tools/wrapper/bin/exist.sh_
  * Configure the link as a service script.
```
sudo update-rc.d exist defaults
```
  * User _xmldb_ must own _/var/lib/xmldb_ path.
```
sudo chown -R xmldb:xmldb /var/lib/xmldb
```

If your system is 64 bits, you must replace wrapper script, because default it is only 32 compliant. To do this, you have to download 64 linux community version of wrapper, from [Tanuki Software](http://wrapper.tanukisoftware.com).

Go to _/var/lib/xmldb/tools/wrapper_ and substitute following files with the new ones:

  * lib/libwrapper.so
  * lib/wrapper.jar
  * bin/wrapper

## Configure eXist ##

Once eXist is installed, you must run it

```
sudo service exist start
```

Then you use eXist client to configure a new user and a new collection. Launch eXist client:

```
/var/lib/xmldb/bin/client.sh -u admin -s -ouri=xmldb:exist://localhost:8088/xmlrpc
```

This will open a command line interface. Execute following commands to create a user and a collection:

```
mkcol mctwp
adduser mctwp
```

The second command will prompt you for _password_ **don't forget it**, _home collection_ and _groups_. Give as _home collection_, **mctwp** and for _groups_ **dba**.

Later, you will configure application with this user and collection.

Then execute _quit_ command to leave this program.