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

package es.urjc.mctwp.image.impl.analyze;

import java.io.File;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Node;

import es.urjc.mctwp.image.exception.ImageException;
import es.urjc.mctwp.image.management.ImagePluginDefaultImpl;
import es.urjc.mctwp.image.objects.Image;
import es.urjc.mctwp.image.objects.PatientInfo;
import es.urjc.mctwp.image.objects.SingleImage;

/**
 * 
 * @author miguel
 *
 */
public class AnalyzeImagePlugin extends ImagePluginDefaultImpl {
	
	@Override
	public Image createImage(File file) throws ImageException {
		SingleImage res = null;

		String ext = getFileExtension(file);
		
		//Extension can not be null and must be hdr or img
		String name = (ext != null)?
				StringUtils.substringBeforeLast(file.getName(), "."):
				file.getName();
				
		res = new ImageAnalyzeImpl();
		res.setId(name);
		
		return res;
	}
	
	@Override
	public Image loadImage(File file) throws ImageException{
		Image result = null;
		
		if(file != null){

		}
		
		return result;
	}

	@Override
	public PatientInfo getPatientInfo(Image image) throws ImageException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<File> toDicom(Image image, File outputDir)
			throws ImageException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public File toPng(Image image) throws ImageException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node toXml(Image image) throws ImageException {
		// TODO Auto-generated method stub
		return null;
	}
}
