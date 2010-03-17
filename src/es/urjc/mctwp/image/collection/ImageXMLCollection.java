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
import es.urjc.mctwp.image.management.ImageManager;
import es.urjc.mctwp.image.objects.Attribute;
import es.urjc.mctwp.image.objects.Image;

/**
 * 
 * @author Miguel Ángel Laguna
 *
 */
public abstract class ImageXMLCollection {
	protected ImageManager imng = null;

	public void setImageManager(ImageManager imng){
		this.imng = imng;
	}

	//Collection operations
	public abstract void createCollection(String collection) throws ImageCollectionException;
	public abstract void deleteCollection(String collection) throws ImageCollectionException;
	
	//Image Operations
	public abstract void storeImage(String collection, Image image) throws ImageCollectionException;
	public abstract void deleteImage(String collection, String idImage) throws ImageCollectionException; 
	public abstract List<String> findImages(String collection, List<Attribute> criteria);
}
