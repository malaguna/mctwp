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

import java.util.ArrayList;

import es.urjc.mctwp.image.objects.Image;
import es.urjc.mctwp.image.objects.SeriesImage;

/**
 * 
 * @author miguel
 *
 */
public class SeriesImageDicomImpl extends SeriesImage {
	private static final long serialVersionUID = 4989462241770033068L;
	public static final String DCM_EXT = "dcm";

	public SeriesImageDicomImpl(){
		super();
		this.setType(DCM_EXT);
	}
	

	@Override
	public void addImage(Image image) {
		
		//Check correct type of image to add
		if(image.getType().equals(DCM_EXT)) {
			
			//Avoid null pointer exception
			if(this.getImages() == null){
				this.setImages(new ArrayList<Image>());
			}
			
			this.getImages().add(image);
		}
	}


	@Override
	public void delImage(Image image) {

		//Check correct type of image to delete
		if(image.getType().equals(DCM_EXT)) {
			
			//Avoid null pointer exception
			if(this.getImages() != null){
				this.getImages().remove(image);
			}
		}
	}
	
}
