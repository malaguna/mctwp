# Minimal configuration #

If you already has a server with a Servlet Container, of course you could use it to deploy this application. Don't forget to download all required [Libraries](LibrariesDependencies.md) and install it either _WEB-INF/lib_ or servlet container shared libs directory.

You need to setup four elements:

  * **eXist XML Data Base**: You could install this data base in the same server machine or another one. Version required is 1.2. If you install eXist into a 64bit machine, you must be carefull with service script, because it ready for 32bit, and you must hack it. Follow these [instructions](InstalleXist.md).

  * **Postgres Data Base**: You need a Postgres Data Base in the same server machine or another one. You need to run the script that creates the schema and init master data. This script is in download section.

  * **(X)MedCon**: Most distributions includes this software, so install version >= 0.10.4. If your distribution does not include this software, download and compile, it is very easy.

  * **Image Library Directory**: You need to create a directory where application will store images. It does not matter whether this directory relies on local file systems or remote, but the same system user who runs Servlet Container must have permission to write here.