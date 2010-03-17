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
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import es.urjc.mctwp.image.exception.ImageException;
import es.urjc.mctwp.image.objects.Image;

public abstract class ImageCreatorDefaultImpl implements ImageCreator{
	protected Logger logger = Logger.getLogger(this.getClass()); 
	private SeriesIdMapper mapper = null;

	public SeriesIdMapper getMapper(){return mapper;}
	public void setMapper(SeriesIdMapper mapper){
		this.mapper = mapper;
	}
	
	public abstract Image createImage(File file) throws ImageException; 
	public abstract Image loadImage(File file) throws ImageException; 
	
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
	 * null, that is because some ImageCreator can process images without
	 * extension.
	 * 
	 * @param file
	 * @return file extension
	 * @throws ImageException if it is not possible to determine the extension
	 */
	protected String getFileExtension(File file) throws ImageException{
		
		String ext  = StringUtils.substringAfterLast(file.getName(), ".");
		if( (ext == null) || (ext.equals("")) )
			ext = null;
		
		return ext;
	}
}
