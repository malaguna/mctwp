# Configure Application #

This application is managed by Spring and thus all configurable aspects are defined into a file called **applicationContext.xml**. This file is located into _WEB-INF_ dir.

Here are main thing to configure:

  * **Data base connection**:
```
<bean id="DataSource" class="org.apache.commons.dbcp.BasicDataSource" destroy-method="close">
	<property name="driverClassName" value="org.postgresql.Driver" />
	<property name="url" value="jdbc:postgresql://<HOST>:<PORT>/<DB_NAME>" />
	<property name="username" value="<USER>" />
	<property name="password" value="<PASSWORD>" />
</bean>
```
  * **(X)MedCon bin path**: into plugin beans:
```
<bean id="AnalyzePlugin" class="es.urjc.mctwp.image.impl.analyze.AnalyzeImagePlugin">
	<property name="medconPath" value="<PATH-TO-MEDCON-BINARY>" />
	<property name="mapper" ref="MapSeriesId" />
</bean>

<bean id="DicomPlugin" class="es.urjc.mctwp.image.impl.dicom.DicomImagePlugin">
	<property name="medconPath" value="<PATH-TO-MEDCON-BINARY>" />
	<property name="mapper" ref="MapSeriesId" />
</bean>
```
  * **XML Data Base connection**:
```
<bean id="XmlImageColl" class="es.urjc.mctwp.image.impl.collection.eXist.ImageXMLCollectionExistImpl">
	<property name="dburi"  value="xmldb:exist://<HOST>:<PORT>/<DB_NAME>" />
	<property name="prefix" value="/db/mctwp" />
	<property name="user"   value="<USER>" />
	<property name="pass"   value="<PASSWORD>" />
</bean>
```
  * **Image Collection Directory**:
```
<bean id="FSImageColl" class="es.urjc.mctwp.image.impl.collection.fs.ImageContentCollectionFSImpl">
	<property name="fsBaseDirPath" value="<PATH>" />
	<property name="fsTmpCollDir" value="temporal" />
</bean>
```
  * DICOM PACS configuration: Finally there are two beans that define parameters for DICOM transcations. Here you need to configure AETitle, host, port, etc. These are:
    * MiniPACS
    * DcmSenderFactory

Once you have configured this file, you need to restart application if this were running.