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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Node;

import es.urjc.mctwp.image.ImageUtils;
import es.urjc.mctwp.image.exception.ImageException;
import es.urjc.mctwp.image.objects.Image;
import es.urjc.mctwp.image.objects.PatientInfo;
import es.urjc.mctwp.image.objects.ThumbNail;

/**
 * Base abstract implementation for all image managers
 * 
 * @author miguel
 * 
 */
public class ImagePluginManager {
	private Logger logger = Logger.getLogger(this.getClass());
	private Map<String, List<ImagePlugin>> plugins = new HashMap<String, List<ImagePlugin>>();

	public void setPlugins(List<ImagePlugin> iplgs) {
		if (iplgs != null) {
			for (ImagePlugin plugin : iplgs) {

				if (plugin.getSupportNoExtension())
					addPluginExtension("", plugin);

				String[] extensions = plugin.getSupportedExtensions();
				if (extensions != null)
					for (String extension : extensions)
						addPluginExtension(extension, plugin);
			}
		}
	}

	// Abstract methods
	public Image loadImage(File file) throws ImageException {
		return buildImage(file, false);
	}

	public Image createImage(File file) throws ImageException {
		return buildImage(file, true);
	}

	/**
	 * Use the list of creators to build image objects from files
	 */
	public List<Image> createImages(List<File> files) throws ImageException {
		List<Image> result = null;

		if (files != null) {
			result = new ArrayList<Image>();

			for (File file : files) {
				Image image = buildImage(file, true);

				if (image != null)
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
	public Node obtainNode(Image image) throws ImageException {
		Node result = null;

		List<ImagePlugin> lstPlugins = getPlugins(image);
		if (lstPlugins != null)
			for (ImagePlugin plugin : lstPlugins) {
				try{
					result = plugin.toXml(image);
					if (result != null)
						break;
				}catch (RuntimeException re){
					logger.error(re.getLocalizedMessage());
					re.printStackTrace();
				}
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
	public ThumbNail obtainThumb(Image image) throws ImageException {
		ThumbNail result = null;

		List<ImagePlugin> lstPlugins = getPlugins(image);
		if (lstPlugins != null)
			for (ImagePlugin plugin : lstPlugins) {
				
				try{
					File thumb = plugin.toPng(image);
					if (thumb != null) {
						result = new ThumbNail();
						result.setContent(thumb);
						result.setId(image.getId());
	
						PatientInfo info = plugin.getPatientInfo(image);
						if (info != null)
							result.setPatInfo(info);
	
						break;
					}
				}catch (RuntimeException re){
					logger.error(re.getLocalizedMessage());
					re.printStackTrace();
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
	public List<File> obtainDICOM(Image image, File outputDir)
			throws ImageException {
		List<File> result = null;

		List<ImagePlugin> lstPlugins = getPlugins(image);
		if (lstPlugins != null)
			for (ImagePlugin plugin : lstPlugins) {
				try{
					result = plugin.toDicom(image, outputDir);
					if (result != null)
						break;
				}catch (RuntimeException re){
					logger.error(re.getLocalizedMessage());
					re.printStackTrace();
				}
			}

		return result;
	}

	/**
	 * Builds an image from a file
	 * 
	 * @param file
	 *            file that represent the image
	 * @return null if there is no creator for this file
	 * @throws ImageException
	 */
	public PatientInfo obtainPatientStudyInfo(Image image)
			throws ImageException {
		PatientInfo result = null;

		List<ImagePlugin> lstPlugins = getPlugins(image);
		if (lstPlugins != null)
			for (ImagePlugin plugin : lstPlugins) {
				try{
					result = plugin.getPatientInfo(image);
					if (result != null)
						break;
				}catch (RuntimeException re){
					logger.error(re.getLocalizedMessage());
					re.printStackTrace();
				}
			}

		return result;
	}
	
	/**
	 * Builds an image from a file
	 * 
	 * @param file
	 *            file that represent the image
	 * @return null if there is no creator for this file
	 * @throws ImageException
	 */
	private Image buildImage(File file, boolean isNew) throws ImageException {
		Image result = null;

		List<ImagePlugin> lstPlugins = getPlugins(file);
		if (lstPlugins != null)
			for (ImagePlugin creator : lstPlugins) {
				result = isNew ? creator.createImage(file) : creator
						.loadImage(file);
				if (result != null)
					break;
			}

		// It means there is not constructor for this file
		if (result == null)
			logger.error("[" + file.getName()
					+ "] is not recognize, no image is created.");

		return result;
	}

	private void addPluginExtension(String extension, ImagePlugin plugin) {
		List<ImagePlugin> auxLst = plugins.get(ImageUtils.normalizeExtension(extension));

		if (auxLst == null) {
			auxLst = new ArrayList<ImagePlugin>();
			plugins.put(extension, auxLst);
		}

		if (!auxLst.contains(plugin))
			auxLst.add(plugin);
	}

	private List<ImagePlugin> getPlugins(Image image) {
		List<ImagePlugin> result = null;

		if (image != null) {
			result = plugins.get(ImageUtils.normalizeExtension(image.getType()));
		}

		return result;
	}

	private List<ImagePlugin> getPlugins(File file) {
		List<ImagePlugin> result = null;

		if (file != null) {
			String ext = StringUtils.substringAfterLast(file.getName(), FilenameUtils.EXTENSION_SEPARATOR_STR);
			result = plugins.get(ImageUtils.normalizeExtension(ext));
		}

		return result;
	}
}
