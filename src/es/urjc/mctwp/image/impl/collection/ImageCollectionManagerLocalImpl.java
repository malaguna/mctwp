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

package es.urjc.mctwp.image.impl.collection;

import java.util.ArrayList;
import java.util.List;

import es.urjc.mctwp.image.collection.ImageCollectionManager;
import es.urjc.mctwp.image.exception.ImageCollectionException;
import es.urjc.mctwp.image.exception.ImageException;
import es.urjc.mctwp.image.objects.Attribute;
import es.urjc.mctwp.image.objects.Image;
import es.urjc.mctwp.image.objects.ThumbNail;

public class ImageCollectionManagerLocalImpl extends ImageCollectionManager {

	@Override
	public void cleanTempCollections() {
		// TODO crear el método de limpieza de archivos viejos en las colecciones temporales
	}

	@Override
	/**
	 * Create both, xml collection and content collection
	 */
	public void createCollection(String colName) throws ImageCollectionException {
		try{
			this.imxc.createCollection(colName);
			this.imcc.createCollection(colName);
		}catch(ImageCollectionException ice){
			this.imxc.deleteCollection(colName);
			this.imcc.deleteCollection(colName);
			throw ice;
		}
	}

	@Override
	public List<Image> findImages(String colName, List<Attribute> attributes) throws ImageException, ImageCollectionException {
	
		List<Image> result = null;
		List<String> images = null;
		
		//FobtainThumbind identifiers of images that satisfies search criteria
		images = imxc.findImages(colName, attributes);
		if( (images != null) && (images.size() >0) ){
			result = new ArrayList<Image>();
			
			//Build images returned above
			for(String idImage : images){
				Image image = imcc.loadImage(colName, idImage);
				result.add(image);
			}
		}
		
		return result;
	}	

	@Override
	public Image getImage(String colName, String imageId) throws ImageException, ImageCollectionException {
		
		return imcc.loadImage(colName, imageId);
	}

	@Override
	public Image getTemporalImage(String colName, String imageId) throws ImageException, ImageCollectionException {
		
		return imcc.loadTempImage(colName, imageId);
	}

	@Override
	public ThumbNail getThumbNail(String colName, String idImage) throws ImageException, ImageCollectionException{
		
		return imng.obtainThumb(imcc.loadImage(colName, idImage));
	}

	@Override
	public ThumbNail getTemporalThumbNail(String colName, String idImage) throws ImageException, ImageCollectionException{
		
		return imng.obtainThumb(imcc.loadTempImage(colName, idImage));
	}

	/**
	 * Try to load png thumbnails from temporal collection, if it does not
	 * exist, then generate it.
	 */
	@Override
	public List<ThumbNail> getThumbNailsTemporalImages(String colName) throws ImageException, ImageCollectionException {
		List<ThumbNail> result = null;
		List<Image> images = null;
		
		images = imcc.loadAllImagesTempCollection(colName);
		if(images != null){
			result = new ArrayList<ThumbNail>();
			
			for(Image image : images){
				ThumbNail tn = imng.obtainThumb(image);
				result.add(tn);
			}
		}
		
		return result;
	}

	/**
	 * It stores a new image into content and xml collections.
	 */
	@Override
	public void persistImage(String tempColName, String defColName, String idImage) throws ImageException, ImageCollectionException {
		Image image  = imcc.loadTempImage(tempColName, idImage);
		
		if(image != null) {

			imcc.storeImage(defColName, image);
			try{
				imxc.storeImage(defColName, image);
			}catch(ImageCollectionException ice){

				//Undo changes
				imcc.deleteImage(defColName, idImage);
				throw ice;
			}
			
			//Any case, it is necessary to remove temporal image
			try{
				imcc.deleteTempImage(tempColName, idImage);
			}catch(ImageCollectionException ice){
				//Undo changes
				imxc.deleteImage(defColName, idImage);
				imcc.deleteImage(defColName, idImage);
				throw ice;
			}
		}
	}

	@Override
	public void storeTemporalImages(String colName, List<Image> images) throws ImageCollectionException {
		
		for(Image image : images){
			imcc.storeTempImage(colName, image);
		}
	}
}
