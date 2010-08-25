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

import org.w3c.dom.Node;

import es.urjc.mctwp.image.exception.ImageException;
import es.urjc.mctwp.image.objects.Image;
import es.urjc.mctwp.image.objects.PatientInfo;

/**
 * This interface specifies methods to be implemented in order
 * to add a new standard or file format.
 * 
 * It also necessary to implement new image type specializing 
 * Image, SingleImage or MultipleImage 
 * 
 * @author Miguel Ángel Laguna Lobato
 *
 */
public interface ImagePlugin {
	
	/**
	 * This method must create an appropriate Image object from
	 * a single file or a directory. The files to build the image
	 * must remain referenced for the lifetime of the image.
	 * 
	 * @param file
	 * @return Image
	 * @throws ImageException
	 */
	public Image createImage(File file) throws ImageException;

	/**
	 * This method is the same as createImage, but in this case,
	 * it is not necessary to create new image identifier, it must
	 * be read from file.
	 * 
	 * @param file
	 * @return Image
	 * @throws ImageException
	 */
	public Image loadImage(File file) throws ImageException;
	
	/**
	 * Obtains a XML Node that represent header information of Image
	 * 
	 * @param image
	 * @return
	 * @throws ImageException
	 */
	public Node toXml(Image image) throws ImageException;
	
	/**
	 * Returns a PNG file of the Image stored into collection
	 * @param image
	 * @return
	 * @throws ImageException
	 */
	public File toPng(Image image) throws ImageException;
	
	/**
	 * Writes into outputDir a DICOM representation of the Image and returns
	 * a list of files for each file content of the Image.
	 * 
	 * @param image
	 * @param outputDir
	 * @throws ImageException
	 */
	public List<File> toDicom(Image image, File outputDir) throws ImageException;
	
	/**
	 * Retrieves demographic and study related information from image
	 * 
	 * @param image
	 * @return null if this functionality is not supported, PatientInfo other way
	 * @throws ImageException
	 */
	public PatientInfo getPatientInfo(Image image) throws ImageException;
	
	public String[] getSupportedExtensions();
	public boolean getSupportNoExtension();
}
