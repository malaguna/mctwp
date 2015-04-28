# News #

## Big Maven Refactor! February, 2013 ##

After some months of inactivity, due to personal reasons, I have just finished a whole code refactor to change project management from Eclipse to Maven.

However, I could not manage three old libraries, that has been added as local repository. You could find how to manage with **external\_libs.sh** script, located at **trunk** dir.

Now it is time to do a big rework to update all infrastructure libraries, and maven is the right way to do it.

## September, 2011 ##

Some bugs have been solved. Virtual Machine was not working properly because when you copied it, new Virtual MAC address is created. This makes ubuntu stop working network so you could not access the application. It has been added a script called **fix-network.sh** into **demo** home user. You must run this script only the first time you download the VM. The script restart the VM, and then VM network is working again.

If you upgrade from previous version, you need to run a data base update (see [issue 23](https://code.google.com/p/mctwp/issues/detail?id=23)):

```
ALTER TABLE attachment ALTER COLUMN tipo TYPE varchar(100);
```

## June, 2011 : Screencast ##

You can see a screencast [here](http://www.youtube.com/watch?v=LX--NZY4VkY&cc_load_policy=1).

## May, 2011 : Virtual Machine and User Manual! ##

We are proud to announce the first release of MCT Image Server. You can find several files in the download section:
  * a compressed file with the application and the database scripts,
  * a virtual machine ready to test the application, and
  * a manual where you can find how to install the application, how to play the virtual machine, and how to use the application.

All files have been compressed with the 7z application. 7z is free and multi-platform. You can download it from [7-zip.org](http://www.7-zip.org). There are binaries for GNU/Linux, Mac and Windows.

It is not possible to upload files larger than 100 Mb, due to Google Code restrictions. So we have had to split the virtual machine into several files. You first have to download all _vmachine_ files, and then open the first one with 7z. Then, follow the manual instructions to play the virtual machine.

Please, if you find bugs or have suggestions, feel free to create new issues in the issue area. Your comments will be appreciated.

# What is it? #

This a work in progress about image management into Clinical Trials. It is being tested and developed at [Fundación CIEN](http://www.fundacioncien.es).

# How does it work? #

This software allows researchers to store and manage images of a Clinical Trial. It does not depend on image format, because it has a plugin system to support new formats and standards.

But DICOM plays an special role, thus this software allows to send and receive DICOM images through DICOM C-STORE transaction, and it is also ready to convert images from any format to DICOM (If the plugin allows).

It also manages task related to images, and to define protocol over trials in order to create tasks automatically when new images are stored.

Basically, images are stored as part of a patient study or as result of tasks. Patients are related to a group and finally a trial has one or more groups.

# Can I use it? #

Of course you can. This is free software (GPL v3) and we would like you to test it. There is also a lot of things to do, so if you like, you can collaborate.

# Documentation #

You can find all necessary documentation in the manual. You can download it from the download section.

&lt;wiki:gadget url="http://www.ohloh.net/p/500576/widgets/project\_thin\_badge.xml" height="36" border="0"/&gt;