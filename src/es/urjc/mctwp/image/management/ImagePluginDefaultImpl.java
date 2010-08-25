//Copyright 2008, 2009, 2010 Miguel Ángel Laguna Lobato
//
//This file is part of Multiclinical Trial Web-PACS.
//
//Multiclinical  Trial Web-PACS is free  software: you  can redistribute 
//it and/or modify it under  the terms of the GNU General Public License 
//as published by the Free  Software Foundation, either version 3 of the 
//License, or (at your option) any later version.
//
//Multiclinical  Trial Web-PACS is distributed  in the hope that it will 
//be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
//of  MERCHANTABILITY or  FITNESS FOR A PARTICULAR PURPOSE.  See the GNU 
//General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with Multiclinical Trial Web-PACS.  If not, see 
//<http://www.gnu.org/licenses/>.

package es.urjc.mctwp.image.management;

import java.io.File;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Node;

import es.urjc.mctwp.image.exception.ImageException;
import es.urjc.mctwp.image.objects.Image;
import es.urjc.mctwp.image.objects.PatientInfo;

/**
 * This class implements basic commons aspects that must be presents
 * in every image plugin. For example, it has logging facilities, a 
 * series mapper, and convenience methods to get file extensión, and 
 * so on.
 * 
 * It is not necessary to specialize this class.
 * 
 * @author Miguel Ángel Laguna Lobato
 *
 */
public abstract class ImagePluginDefaultImpl implements ImagePlugin {
	protected Logger logger = Logger.getLogger(this.getClass()); 
	private SeriesIdMapper mapper = null;

	public SeriesIdMapper getMapper(){
		return mapper;
	}
	
	public void setMapper(SeriesIdMapper mapper){
		this.mapper = mapper;
	}

	/**
	 * Generates an unique image identifier along all collections
	 * 
	 * @return
	 */
	protected String getUniqueImageId(){
		UUID uiid = UUID.randomUUID();
		
		return uiid.toString();
	}
	
	/**
	 * Try to get the file extension. If there is no extension it returns 
	 * null, that is because some ImagePlugins can process images without
	 * extension.
	 * 
	 * @param file
	 * @return file extension
	 */
	protected String getFileExtension(File file){		
		String ext  = StringUtils.substringAfterLast(file.getName(), ".");
		if( (ext == null) || (ext.equals("")) )
			ext = null;
		
		return ext;
	}

	/**
	 * Try to get the file name.
	 * 
	 * @param file
	 * @return file name
	 */
	protected String getFileName(File file){		
		return StringUtils.substringBeforeLast(file.getName(), ".");
	}

	@Override
	public abstract Image createImage(File file) throws ImageException; 

	@Override
	public abstract Image loadImage(File file) throws ImageException; 

	@Override
	public abstract PatientInfo getPatientInfo(Image image) throws ImageException;

	@Override
	public abstract List<File> toDicom(Image image, File outputDir) throws ImageException ;

	@Override
	public abstract File toPng(Image image) throws ImageException;
	
	@Override
	public abstract Node toXml(Image image) throws ImageException;
}
