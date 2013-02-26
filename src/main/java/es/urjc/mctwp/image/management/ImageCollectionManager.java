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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import es.urjc.mctwp.image.ImageUtils;
import es.urjc.mctwp.image.collection.ImageContentCollection;
import es.urjc.mctwp.image.collection.ImageXMLCollection;
import es.urjc.mctwp.image.exception.ImageCollectionException;
import es.urjc.mctwp.image.exception.ImageException;
import es.urjc.mctwp.image.objects.Attribute;
import es.urjc.mctwp.image.objects.ComplexImage;
import es.urjc.mctwp.image.objects.Image;
import es.urjc.mctwp.image.objects.SeriesImage;
import es.urjc.mctwp.image.objects.SingleImage;
import es.urjc.mctwp.image.objects.ThumbNail;

public class ImageCollectionManager {
	private final File sysTempDir = new File(System.getProperty("java.io.tmpdir"));
	private ImageContentCollection imcc = null;
	private ImageXMLCollection imxc = null;
	private ImagePluginManager impm = null;

	public void setImageXMLCollection(ImageXMLCollection imxc) {
		this.imxc = imxc;
	}

	public void setImageContentCollection(ImageContentCollection imcc) {
		this.imcc = imcc;
	}

	public void setImagePluginManager(ImagePluginManager impm) {
		this.impm = impm;
	}

	/**
	 * Creates both, content and XML collection. If an error occurs it delete
	 * collections.
	 */
	public void createCollection(String colName)
			throws ImageCollectionException {
		try {
			this.imxc.createCollection(colName);
			this.imcc.createCollection(colName);
		} catch (ImageCollectionException ice) {
			this.imxc.deleteCollection(colName);
			this.imcc.deleteCollection(colName);
			throw ice;
		}
	}

	/**
	 * clean temp collections. All files older than a age must be deleted, and
	 * void collections must be delete too.
	 */
	public void cleanTempCollections() {
		// TODO crear el método de limpieza de archivos viejos en las
		// colecciones temporales
	}

	public List<Image> findImages(String colName, List<Attribute> attributes)
			throws ImageException, ImageCollectionException {

		List<Image> result = null;
		List<String> images = null;

		// ObtainThumb identifiers of images that satisfies search criteria
		images = imxc.findNodes(colName, attributes);
		if ((images != null) && (images.size() > 0)) {
			result = new ArrayList<Image>();

			// Build images returned above
			for (String idImage : images) {
				File content = imcc.loadContent(colName, idImage, false);
				result.add(impm.loadImage(content));
			}
		}

		return result;
	}

	public Image getImage(String colName, String imageId, boolean temporal)
			throws ImageException, ImageCollectionException {

		return impm.loadImage(imcc.loadContent(colName, imageId, temporal));
	}

	public ThumbNail getThumbNail(String colName, String imageId,
			boolean temporal) throws ImageException, ImageCollectionException {

		return impm.obtainThumb(impm.loadImage(imcc.loadContent(colName,
				imageId, temporal)));
	}

	public List<ThumbNail> getThumbNails(String colName, boolean temporal)
			throws ImageException, ImageCollectionException {

		List<ThumbNail> result = null;
		List<File> files = null;

		files = imcc.loadAllContents(colName, temporal);
		if (files != null) {
			result = new ArrayList<ThumbNail>();

			for (File file : files){
				ThumbNail thumb = impm.obtainThumb(impm.loadImage(file));
				if(thumb != null)
					result.add(thumb);
			}
		}

		return result;
	}

	/**
	 * It parses and stores temporarily, a list of files.
	 * 
	 * @param colName
	 * @param files
	 * @throws ImageCollectionException
	 * @throws ImageException
	 * @throws IOException
	 */
	public void storeTemporalImages(String colName, List<File> files)
			throws ImageCollectionException, ImageException, IOException {

		for (File file : files) {
			Image image = impm.createImage(file);

			if (image != null) {
				if (image instanceof SingleImage) {
					SingleImage simg = (SingleImage) image;
					
					String ext = ImageUtils.getFileExtension(simg.getContent());
					File tmpFile = new File(FilenameUtils.concat(sysTempDir.getAbsolutePath(), image.getId() + FilenameUtils.EXTENSION_SEPARATOR_STR + ext));
					if (tmpFile.exists()) tmpFile.delete();
					FileUtils.copyFile(simg.getContent(), tmpFile);

					imcc.storeContent(colName, tmpFile, true);
					tmpFile.delete();
				} else {

					//Create temp directory where put all file content of Image
					File tmpDir = new File(FilenameUtils.concat(sysTempDir.getAbsolutePath(), image.getId() + FilenameUtils.EXTENSION_SEPARATOR_STR + image.getType()));
					if (tmpDir.exists()) FileUtils.deleteDirectory(tmpDir);
					tmpDir.mkdir();
					
					if (image instanceof SeriesImage) {
	
						List<Image> images = ((SeriesImage) image).getImages();
						if (images != null && !images.isEmpty()) {
	
							for (Image img : images)
								copyToDirectory(tmpDir, img);
	
						}
					} else if (image instanceof ComplexImage) {
						
						List<File> auxFiles = ((ComplexImage)image).getContent();
						if(auxFiles != null && !auxFiles.isEmpty()) {
							
							for(File auxFile : auxFiles)
								copyToDirectory(tmpDir, auxFile);
						}
					}

					//Store content and delete temporal directory
					imcc.storeContent(colName, tmpDir, true);
					FileUtils.deleteDirectory(tmpDir);
				}
			}
		}
	}

	public void acceptTemporalImage(String tempColName, String defColName,
			String idImage) throws ImageException, ImageCollectionException,
			IOException {

		File content = imcc.loadContent(tempColName, idImage, true);
		Image image = impm.loadImage(content);

		if (image != null) {

			imcc.storeContent(defColName, content, false);

			try {
				imxc.storeNode(defColName, image.getId(), impm
						.obtainNode(image));
			} catch (ImageCollectionException ice) {

				// Undo changes
				imcc.deleteContent(defColName, image.getId(), false);
				throw ice;
			}

			// Any case, it is necessary to remove temporal image
			try {
				imcc.deleteContent(tempColName, image.getId(), true);
			} catch (ImageCollectionException ice) {
				// Undo changes
				imxc.deleteNode(defColName, image.getId());
				imcc.deleteContent(defColName, image.getId(), false);
				throw ice;
			}
		}
	}
	
	/**
	 * This functions only delete temporal images, although it is prepared 
	 * to delete persistent images.
	 * 
	 * @param colName
	 * @param imagesId
	 * @param temporal
	 * @throws ImageException
	 * @throws ImageCollectionException
	 */
	public void deleteImages(String colName, List<String> imagesId,
			boolean temporal) throws ImageException, ImageCollectionException {
		
		if(colName != null && imagesId != null && temporal){
			for(String id : imagesId)
				imcc.deleteContent(colName, id, temporal);
		}
	}	

	/**
	 * This function knows how to retrieve all files of any image and
	 * puts all together under a temporal directory. It reproduces 
	 * subdirectories when necessary.
	 * 
	 * @param directory
	 * @param image
	 * @throws IOException
	 */
	private void copyToDirectory(File directory, Image image) throws IOException {
		
		if (image instanceof SingleImage) {
			FileUtils.copyFileToDirectory(((SingleImage) image).getContent(), directory);
			
		} else {
			
			//Create temp directory where put all file content of Image
			File tmpDir = new File(FilenameUtils.concat(directory.getAbsolutePath(), image.getId()));
			if (tmpDir.exists()) tmpDir.mkdir();

			//Copy required files in case of Series Images
			if (image instanceof SeriesImage) {
				List<Image> images = ((SeriesImage) image).getImages();
				if (images != null && !images.isEmpty())
					for (Image img : images)
						copyToDirectory(tmpDir, img);

			//Copy required files in case of Complex Images
			} else if (image instanceof ComplexImage) {
				List<File> aux = ((ComplexImage) image).getContent();

				if (aux != null)
					for (File file : aux)
						copyToDirectory(tmpDir, file);
			}
		}
	}

	/**
	 * This is the same function as above, but it copies all content
	 * from a directory instead of an image
	 * 
	 * @param directory
	 * @param source
	 * @throws IOException
	 */
	private void copyToDirectory(File directory, File source) throws IOException {
		
		if (source.isFile()) {
			FileUtils.copyFileToDirectory(source, directory);
		} else if (source.isDirectory()) {
			
			//Create temp directory where put all file content of Image
			File tmpDir = new File(FilenameUtils.concat(directory.getAbsolutePath(), source.getName()));
			if (!tmpDir.exists()) tmpDir.mkdir();

			if (source.listFiles() != null)
				for (File file : source.listFiles())
					copyToDirectory(tmpDir, file);
		}
	}
}
