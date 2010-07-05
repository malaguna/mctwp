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

package es.urjc.mctwp.image.impl.management;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.w3c.dom.Node;

import es.urjc.mctwp.image.exception.ImageException;
import es.urjc.mctwp.image.management.ImageConversor;
import es.urjc.mctwp.image.management.ImageCreator;
import es.urjc.mctwp.image.management.ImageManager;
import es.urjc.mctwp.image.objects.Image;
import es.urjc.mctwp.image.objects.PatientInfo;
import es.urjc.mctwp.image.objects.ThumbNail;

/**
 * Implementation of local service for image manager
 * 
 * @author miguel
 *
 */
public class ImageManagerLocalImpl extends ImageManager {
	private Logger logger = Logger.getLogger(this.getClass());  
	
	@Override
	public Image loadImage(File file) throws ImageException {
		return buildImage(file, false);
	}

	@Override
	public Image createImage(File file) throws ImageException {
		return buildImage(file, true);
	}

	/**
	 * Use the list of creators to build image objects from files
	 */
	@Override
	public List<Image> createImages(List<File> files) throws ImageException {
		List<Image> result = null;
		
		if(files != null){
			result = new ArrayList<Image>();

			for(File file : files){
				Image image = buildImage(file, true);
				
				if(image != null)
					result.add(image);
			}
		}
		
		return result;
	}

	/**
	 * Obtain a node for header image informacion
	 * 
	 * @param image
	 * @return DOMNode
	 * @throws ImageException
	 */
	@Override
	public Node obtainNode(Image image) throws ImageException{
		Node result = null;
		
		for(ImageConversor conversor : conversors){
			result = conversor.toXml(image);
			if(result != null) break;
		}
		
		return result;
	}

	/**
	 * Obtain a thumbnail from an image
	 * 
	 * @param image
	 * @return thumbnail
	 * @throws ImageException
	 */
	@Override
	public ThumbNail obtainThumb(Image image) throws ImageException{
		ThumbNail result = null;
		
		for(ImageConversor conversor : conversors){
			File thumb = conversor.toPng(image);
			if(thumb != null){
				result = new ThumbNail();
				result.setContent(thumb);
				result.setId(image.getId());
				
				PatientInfo info = conversor.getPatientInfo(image);
				if(info != null)
					result.setPatInfo(info);
				
				break;
			}
		}
		
		return result;
	}
	
	/**
	 * Writes into outputDir a DICOM representation of the Image
	 * 
	 * @param image
	 * @param outputDir
	 * @return List<File>
	 * @throws ImageException
	 */
	@Override
	public List<File> obtainDICOM(Image image, File outputDir) throws ImageException {
		List<File> result = null;
		
		for(ImageConversor conversor : conversors){
			result = conversor.toDicom(image, outputDir);
			if(result != null)
				break;
		}
		
		return result;
	}
	
	/**
	 * Builds an image from a file
	 * 
	 * @param file file that represent the image
	 * @return null if there is no creator for this file
	 * @throws ImageException
	 */
	private Image buildImage(File file, boolean isNew) throws ImageException{
		Image result = null;
		
		for(ImageCreator creator : creators){
			result = isNew?creator.createImage(file):creator.loadImage(file);
			if(result != null)
				break;
		}
		
		//It means there is not constructor for this file
		if(result == null)
			logger.error("[" + file.getName() + "] is not recognize, no image is created.");
		
		return result;
	}

	@Override
	public PatientInfo obtainPatientStudyInfo(Image image) throws ImageException {
		PatientInfo result = null;
		
		if(image != null){
			for(ImageConversor conversor : conversors){
				result = conversor.getPatientInfo(image);
				if(result != null) break;
			}
		}
		
		return result;
	}
}
