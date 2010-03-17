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
import java.util.List;

import org.w3c.dom.Node;

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
public abstract class ImageManager {
	protected List<ImageCreator> creators = new ArrayList<ImageCreator>();
	protected List<ImageConversor> conversors = new ArrayList<ImageConversor>();

	public void setCreators(List<ImageCreator> ics){
		creators = ics;
	}
	public void setConversors(List<ImageConversor> ics){
		conversors = ics;
	}
	
	//Abstract methods
	public abstract Image loadImage(File file) throws ImageException;
	public abstract Image createImage(File file) throws ImageException;
	public abstract List<Image> createImages(List<File> files) throws ImageException;
	public abstract Node obtainNode(Image image) throws ImageException;
	public abstract ThumbNail obtainThumb(Image image) throws ImageException;
	public abstract List<File> obtainDICOM(Image image, File outputDir) throws ImageException;
	public abstract PatientInfo obtainPatientStudyInfo(Image image) throws ImageException;
}
