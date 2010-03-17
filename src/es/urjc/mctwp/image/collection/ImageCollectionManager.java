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

package es.urjc.mctwp.image.collection;

import java.util.List;

import es.urjc.mctwp.image.exception.ImageCollectionException;
import es.urjc.mctwp.image.exception.ImageException;
import es.urjc.mctwp.image.management.ImageManager;
import es.urjc.mctwp.image.objects.Attribute;
import es.urjc.mctwp.image.objects.Image;
import es.urjc.mctwp.image.objects.ThumbNail;

public abstract class ImageCollectionManager {
	protected ImageContentCollection imcc = null;
	protected ImageXMLCollection imxc = null;
	protected ImageManager imng = null;

	public void setImageXMLCollection(ImageXMLCollection imxc){
		this.imxc = imxc;
	}	
	public void setImageContentCollection(ImageContentCollection imcc){
		this.imcc = imcc;
	}	
	public void setImageManager(ImageManager imng){
		this.imng = imng;
	}
	
	//Collection operations
	/**
	 * Creates both, content and XML collection. If an error occurs
	 * it delete collections.
	 */
	public abstract void createCollection(String colName) throws ImageCollectionException;
	
	//Temp collection operations
	/**
	 * clean temp collections. All files older than a age must be
	 * deleted, and void collections must be delete too.
	 */
	public abstract void cleanTempCollections();
	
	public abstract List<Image> findImages(String colName, List<Attribute> attributes) throws ImageException, ImageCollectionException;
	public abstract Image getImage(String colName, String imageId) throws ImageException, ImageCollectionException;
	public abstract ThumbNail getThumbNail(String colName, String idImage) throws ImageException, ImageCollectionException;
	
	//Temp image operations
	public abstract void storeTemporalImages(String colName, List<Image> images) throws ImageCollectionException;
	public abstract Image getTemporalImage(String colName, String imageId) throws ImageException, ImageCollectionException;
	public abstract ThumbNail getTemporalThumbNail(String colName, String idImage) throws ImageException, ImageCollectionException;
	public abstract List<ThumbNail> getThumbNailsTemporalImages(String colName) throws ImageException, ImageCollectionException;
	
	//Both, temp and persistent image operations
	/**
	 * Moves a temporary image to a definitive collection
	 */
	public abstract void persistImage(String tempColName, String defColName, String idImage) throws ImageException, ImageCollectionException;
	
}
