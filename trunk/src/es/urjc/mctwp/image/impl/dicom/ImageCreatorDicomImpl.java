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

package es.urjc.mctwp.image.impl.dicom;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.lang.StringUtils;
import org.dcm4che2.data.BasicDicomObject;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.dcm4che2.io.DicomInputStream;

import es.urjc.mctwp.image.exception.ImageException;
import es.urjc.mctwp.image.management.ImageCreatorDefaultImpl;
import es.urjc.mctwp.image.objects.Image;
import es.urjc.mctwp.image.objects.SeriesImage;
import es.urjc.mctwp.image.objects.SingleImage;

/**
 * 
 * @author miguel
 *
 */
public class ImageCreatorDicomImpl extends ImageCreatorDefaultImpl {
	
	@Override
	public Image createImage(File file) throws ImageException {
		String seriesUid = null;
		SeriesImage res = null;
		SingleImage aux = null;

		String ext = getFileExtension(file);
		seriesUid = getSeriesUID(file);

		//If there is a seriesUid, Image is DICOM.
		if(seriesUid != null){
			
			//Get proper file name
			String name = (ext != null)?
					StringUtils.substringBeforeLast(file.getName(), "."):
					file.getName();

			//Create single image
			aux = new SingleImageDicomImpl();
			aux.setContent(file);
			aux.setId(name);
			
			//Create series image, first get prop series id
			String seriesId = getMapper().getSeriesPropId(seriesUid);
			res = new SeriesImageDicomImpl();
			res.setId(seriesId);
			res.addImage(aux);
		}
		
		return res;
	}
	
	@Override
	public Image loadImage(File file) throws ImageException{
		Image result = null;
		
		if(file != null){
			if(file.isDirectory())
				result = loadSeriesImage(file);
			else if(file.isFile())
				result = loadSingleImage(file);
		}
		
		return result;
	}

	/**
	 * Load a singleImage from a file. It checks it is a Dicom file
	 * 
	 * @param file
	 * @return
	 * @throws ImageException
	 */
	private SingleImage loadSingleImage(File file) throws ImageException {
		SingleImage result = null;
		String seriesUid = null;

		String ext = getFileExtension(file);
		seriesUid = getSeriesUID(file);
		
		if(seriesUid != null){

			//Get proper file name
			String name = (ext != null)?
					StringUtils.substringBeforeLast(file.getName(), "."):
					file.getName();
			
			//Create image
			result = new SingleImageDicomImpl();
			result.setContent(file);
			result.setId(name);
		}
		
		return result;
	}

	/**
	 * Loads a Dicom serie from a directory containing dicom files
	 * from the same serie
	 * 
	 * @param file
	 * @return
	 * @throws ImageException
	 */
	private SeriesImage loadSeriesImage(File file) throws ImageException {
		SeriesImage result = new SeriesImageDicomImpl();

		result.setId(file.getName());
		
		//Add every single image directory has
		for(File f : Arrays.asList(file.listFiles())){
			SingleImage singleImage = loadSingleImage(f);
			if(singleImage != null){
				
				//Is it neccesary to check wheter image has same seriesUid?
				result.addImage(singleImage);
			}
		}
		
		//If no image has been added, it is wrong format
		if(result.getImages() == null)
			result = null;
		
		return result;
	}
	
	/**
	 * Check if it is a nice dicom file and returns seriesUid
	 * 
	 * @return series uid if image is dicom, null other case.
	 */
	private String getSeriesUID(File file){
		DicomInputStream dis = null;
		DicomObject dcmObj = null;
		String result = null;
		
		try{
			dis = new DicomInputStream(file);
			dcmObj = new BasicDicomObject();
			dis.readDicomObject(dcmObj, -1);
			result = dcmObj.getString(Tag.SeriesInstanceUID);
			dis.close();
			dcmObj.clear();
		}catch(IOException ioe){
			result = null;
		}	
		
		return result;
	}	
}
