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

package es.urjc.mctwp.image.impl.collection.fs;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import es.urjc.mctwp.image.collection.ImageContentCollection;
import es.urjc.mctwp.image.exception.ImageCollectionException;
import es.urjc.mctwp.image.exception.ImageException;
import es.urjc.mctwp.image.objects.Image;
import es.urjc.mctwp.image.objects.SeriesImage;
import es.urjc.mctwp.image.objects.SingleImage;

/**
 * File system broker to manage dicom fila storage
 * 
 * @author Miguel Ángel Laguna Lobato
 *
 */
public class ImageContentCollectionFSImpl extends ImageContentCollection{
	private FileFilter icff;
	private String 	fsBaseDirPath;
	private String  fsTmpCollDir;
	private File 	basedir;
	private File 	tmpcoll;
	private Logger  logger;
	
	public ImageContentCollectionFSImpl(){
		logger = Logger.getLogger(this.getClass());
		icff = new ImageContentFileFilter();
	}

	/**
	 * Sets and checks the base path for storing images
	 * 
	 * @param fspath
	 */
	public void setFsBaseDirPath(String fspath){
		
		if( (fspath != null) && (fspath.length() > 0) ){
			this.fsBaseDirPath = fspath;
			
			basedir = new File(fsBaseDirPath);
			if(!basedir.exists()){
				String error = fspath + " doesn't exist. Can't manage image storage.";
				logger.error(error);
				throw new RuntimeException(error);
			}
		}else{
			String error = "given path is null or empty";
			logger.error(error);
			throw new RuntimeException(error);
		}

		//Is not sure the order to set basedir and tempcoll
		if(fsTmpCollDir != null)
			createTempCollection();
	}
	
	/**
	 * Sets and checks the base path for storing temporal images
	 * 
	 * @param fspath
	 */
	public void setFsTmpCollDir(String name){
		if( (name != null) && (name.length() > 0) ){
			this.fsTmpCollDir = name;
		}else
			throw new RuntimeException("Invalid temporary collection name");
		
		//Is not sure the order to set basedir and tempcoll
		if(fsBaseDirPath != null)
			createTempCollection();
	}
	
	/**
	 * Once both path for temporal and persistent images are specified, it is
	 * created temporal collection
	 */
	private void createTempCollection(){
		tmpcoll = new File(FilenameUtils.concat(fsBaseDirPath, fsTmpCollDir));
		if(!tmpcoll.exists()) tmpcoll.mkdir();
	}

	/**
	 * Finds an image into a collection and init correct type of image
	 * 
	 * @param collection
	 * @param imageId
	 * @return
	 */
	private Image findImage(File collection, String imageId){
		Image result = null;
		
		for(File file : collection.listFiles()){
			String id = (file.isFile())?StringUtils.substringBeforeLast(file.getName(), "."):file.getName();
			if(id.equals(imageId)){

				try{
					result = imng.loadImage(file);
				}catch(ImageException ie){
					logger.error("Loading image [" + imageId + "] from collection [" + collection + "] " + ie.getMessage());
					result = null;
				}
				
				break;
			}
		}
		
		return result;
	}
	
	/**
	 * Create a subdirectory for store dicom files
	 * 
	 * @param name
	 */
	public void createCollection(String name) throws ImageCollectionException{
		
		if( (name != null) && (name.length() > 0) ){		
			File temp = new File(FilenameUtils.concat(basedir.getAbsolutePath(), name));
			
			if(temp.exists()){
				String error = "Collection [" + name + "] already exists";
				logger.error(error);
				throw new ImageCollectionException(error);
			}else{
				try{
					temp.mkdir();
				}catch (Exception e){
					logger.error(e.getMessage());
					throw new ImageCollectionException(e);
				}
			}
		}else{
			String error = "Can't create collection, given name is null or empty";
			logger.error(error);
			throw new ImageCollectionException(error);
		}
	}
	
	/**
	 * Don't delete anything
	 */
	public void deleteCollection(String name) throws ImageCollectionException{
		//do nothing
	}
	
	/**
	 * Retrieve an image from collection
	 * 
	 * @param collection
	 * @param id of image
	 * @return dcm file
	 */
	@Override
	public Image loadImage(String collection, String idImage) throws ImageCollectionException{
		Image result = null;
		
		if( (collection != null) && (idImage != null) &&
			(collection.length() > 0) && (idImage.length() > 0) ){		

			File temp = new File(FilenameUtils.concat(basedir.getAbsolutePath(), collection));
			
			if(!temp.exists()){
				String error = "Collection [" + collection + "] doesn't exists";
				throw new ImageCollectionException(error);
			}else{
				result = findImage(temp, idImage);
				if(result == null){
					String error = "File [" + idImage + "] is wrong or doesn't exists";
					throw new ImageCollectionException(error);
				}
			}
		}
		
		return result;
	}
	
	
	/**
	 * Retrieve an image from a temporal collection
	 * 
	 * @param collection
	 * @param id of image
	 * @return dcm file
	 */
	@Override
	public Image loadTempImage(String collection, String idImage) throws ImageCollectionException{
		Image result = null;
		
		if( (collection != null) && (idImage != null) &&
			(collection.length() > 0) && (idImage.length() > 0) ){		

			File temp = new File(FilenameUtils.concat(tmpcoll.getAbsolutePath(), collection));
			
			if(!temp.exists()){
				String error = "Temp collection [" + collection + "] doesn't exists";
				throw new ImageCollectionException(error);
			}else{
				result = findImage(temp, idImage);
				if(result == null){
					String error = "File [" + idImage + "] is wrong or doesn't exists";
					throw new ImageCollectionException(error);
				}
			}
		}
		
		return result;
	}
	
	/**
	 * Store an image into a collection. If the collection does not exist
	 * an exception is thrown.
	 * 
	 * @param collection
	 * @param dcm
	 */
	@Override
	public void storeImage(String collection, Image image) throws ImageCollectionException{
		
		if( (collection != null) && (image != null) && (collection.length() > 0) ){		

			File temp = new File(FilenameUtils.concat(basedir.getAbsolutePath(), collection));

			if(!temp.exists()){
				String error = "Collection [" + collection + "] doesn't exists";
				throw new ImageCollectionException(error);
			}else{
				if(image instanceof SingleImage)
					storeSingleImage(temp, (SingleImage)image);
				else if (image instanceof SeriesImage)
					storeSeriesImage(temp, (SeriesImage)image);
				else
					logger.error("Class image not supported!");
			}
		}
	}	

	/**
	 * Store an image into a collection. If the temp collection does not
	 * exist we create it.
	 * 
	 * @param collection
	 * @param dcm
	 */
	@Override
	public void storeTempImage(String collection, Image image) throws ImageCollectionException{
		
		if( (collection != null) && (image != null) && (collection.length() > 0) ){		

			File temp = new File(FilenameUtils.concat(tmpcoll.getAbsolutePath(), collection));

			//Temp collections are diferent, if they don't exist we create it
			if(!temp.exists()){
				temp.mkdir();
			}
			
			if(image instanceof SingleImage)
				storeSingleImage(temp, (SingleImage)image);
			else if (image instanceof SeriesImage)
				storeSeriesImage(temp, (SeriesImage)image);
			else
				logger.error("Class image not supported!");
		}
	}
	
	/**
	 * delete an image
	 */
	@Override
	public void deleteImage(String collection, String idImage) throws ImageCollectionException{

		if( (collection != null) && (idImage != null) &&
			(collection.length() > 0) && (idImage.length() > 0) ){		

			File temp = new File(FilenameUtils.concat(basedir.getAbsolutePath(), collection));
			Image img = null;
			
			if(!temp.exists()){
				String error = "Collection [" + collection + "] doesn't exists";
				throw new ImageCollectionException(error);
			}else{
				img = findImage(temp, idImage);
				if(img == null){
					String error = "Image [" + idImage + "] doesn't exists";
					throw new ImageCollectionException(error);
				}
				try{
					deleteImage(temp, img);
				}catch (Exception e){
					logger.error(e.getMessage());
					throw new ImageCollectionException(e);
				}
			}
		}
	}
	
	/**
	 * delete a temporal image
	 */
	@Override
	public void deleteTempImage(String collection, String idImage) throws ImageCollectionException{

		if( (collection != null) && (idImage != null) &&
			(collection.length() > 0) && (idImage.length() > 0) ){		

			File temp = new File(FilenameUtils.concat(tmpcoll.getAbsolutePath(), collection));
			Image img = null;
			
			if(!temp.exists()){
				String error = "Temp collection [" + collection + "] doesn't exists";
				throw new ImageCollectionException(error);
			}else{
				img = findImage(temp, idImage);
				if(img == null){
					String error = "Image [" + idImage + "] doesn't exists";
					throw new ImageCollectionException(error);
				}
				try{
					deleteImage(temp, img);
				}catch (Exception e){
					logger.error(e.getMessage());
					throw new ImageCollectionException(e);
				}
			}
		}
	}
	
	/**
	 * Retrieve all images in the temp collection, except thumbnails 
	 * files. See file filter implementation.
	 */
	@Override
	public List<Image> loadAllImagesTempCollection(String name) throws ImageCollectionException{
		List<Image> result = null;
		
		if( (name != null) && (name.length() > 0) ){		
			File temp = new File(FilenameUtils.concat(tmpcoll.getAbsolutePath(), name));
			
			if(!temp.exists()){
				String error = "Temp collection [" + name + "] does not exists";
				logger.error(error);
				throw new ImageCollectionException(error);
			}else{
				try{
					result = new ArrayList<Image>();
					
					//For all files and directories (excluding thumbnails, see icff filter)
					for(File file : Arrays.asList(temp.listFiles(icff)))
						result.add(imng.loadImage(file));

				}catch (Exception e){
					logger.error(e.getMessage());
					throw new ImageCollectionException(e);
				}
			}
		}
		
		return result;
	}
	
	/**
	 * Store single image into collection, we trust that collection exists
	 * 
	 * @param collection
	 * @param idImage
	 * @param single
	 * @throws ImageCollectionException
	 */
	private void storeSingleImage(File collection, SingleImage single) throws ImageCollectionException{
		File content = single.getContent();

		if(!content.exists()){
			String error = "Single image [" + single.getId() + "] content [" + content.getPath() + "] doesn't exists";
			throw new ImageCollectionException(error);
		}
		
		try{
			File dest = null;			
			String ext = getFileExtension(content);
			
			if(ext != null)
				dest = new File(FilenameUtils.concat(collection.getAbsolutePath(), single.getId() + "." + ext));
			else
				dest = new File(FilenameUtils.concat(collection.getAbsolutePath(), single.getId()));
			
			FileUtils.copyFile(content, dest);
		}catch (Exception e){
			logger.error(e.getMessage());
			throw new ImageCollectionException(e);
		}
	}
	
	/**
	 * Store series image into collection, we trust that collection exists
	 * 
	 * @param collection
	 * @param idImage
	 * @param single
	 * @throws ImageCollectionException
	 */
	private void storeSeriesImage(File collection, SeriesImage serie) throws ImageCollectionException{
		File seriesDir = new File(FilenameUtils.concat(collection.getAbsolutePath(), serie.getId()));
		
		//Check if there is images to store
		if( (serie.getImages() != null) && (!serie.getImages().isEmpty()) ){

			//Create series dir if doesn't exists
			if(!seriesDir.exists()){
				seriesDir.mkdir();
			}
			
			for(Image img : serie.getImages()){
				if(img instanceof SingleImage)
					storeSingleImage(seriesDir, (SingleImage)img);
				//TODO Do we store recursive series?
			}
		}
	}

	/**
	 * Delete any type of image, we trust image is correctly loaded.
	 * 
	 * @param image
	 * @throws IOException 
	 */
	private void deleteImage(File coll, Image image) throws IOException{
		
		if(image instanceof SingleImage)
			((SingleImage)image).getContent().delete();
		else if(image instanceof SeriesImage){
			SeriesImage si = (SeriesImage)image;
			
			//Delete directory
			File aux = new File(FilenameUtils.concat(coll.getAbsolutePath(), si.getId()));
			FileUtils.deleteDirectory(aux);
		}
	}
}
