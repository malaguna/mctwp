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

package es.urjc.mctwp.pacs;

import java.io.File;
import java.util.List;

import es.urjc.mctwp.image.management.ImageManager;
import es.urjc.mctwp.image.objects.Image;

/**
 * Abstract class for a DICOM images sender
 * 
 * @author Miguel Ángel Laguna
 *
 */
public abstract class DcmSender {
	protected ImageManager imng = null;
	protected File tempDirectory = null;
	protected String hostname = null;
	protected String aeTitle = null;
	protected String thrName = null;
	protected int trnBufferSize = 1024;
	protected int bufferSize = 1024;
	protected int priority = 0;
	protected int rspDelay = 0;

	//TODO quizas haya que gestionar un directorio por usuario para evitar
	//efectos indeseados por la concurrencia
	public void setTempDirectory(String tempDirectory) {
		if(tempDirectory != null)
			this.tempDirectory = new File(tempDirectory);
			if(!this.tempDirectory.exists())
				this.tempDirectory = null;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public void setAeTitle(String aeTitle) {
		this.aeTitle = aeTitle;
	}

	public void setThrName(String thrName) {
		this.thrName = thrName;
	}

	public void setTrnBufferSize(int trnBufferSize) {
		this.trnBufferSize = trnBufferSize;
	}

	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}

	public void setRspDelay(int rspDelay) {
		this.rspDelay = rspDelay;
	}

	public void setImng(ImageManager imng) {
		this.imng = imng;
	}
	protected ImageManager getImng(){
		return imng;
	}

	public abstract void init() throws DcmException;
	public abstract void sendImages(List<Image> images, DcmDestination destination);
}
