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

import es.urjc.mctwp.image.management.ImagePluginManager;

public class DcmSenderFactory {
	private DcmSender sampleObjSender = null;
	private String tempDirectory = null;
	private ImagePluginManager imng = null;
	private String hostname = null;
	private String aeTitle = null;
	private String thrName = null;
	private int trnBufferSize = 1024;
	private int bufferSize = 1024;
	private int priority = 0;
	private int rspDelay = 0;
	
	public String getTempDirectory() {return tempDirectory;}
	public void setTempDirectory(String tempDirectory) {
		this.tempDirectory = tempDirectory;
	}

	public String getHostname() {return hostname;}
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getAeTitle() {return aeTitle;}
	public void setAeTitle(String aeTitle) {
		this.aeTitle = aeTitle;
	}

	public String getThrName() {return thrName;}
	public void setThrName(String thrName) {
		this.thrName = thrName;
	}

	public int getTrnBufferSize() {return trnBufferSize;}
	public void setTrnBufferSize(int trnBufferSize) {
		this.trnBufferSize = trnBufferSize;
	}

	public int getBufferSize() {return bufferSize;}
	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}

	public int getPriority() {return priority;}
	public void setPriority(int priority) {
		this.priority = priority;
	}

	public int getRspDelay() {return rspDelay;}
	public void setRspDelay(int rspDelay) {
		this.rspDelay = rspDelay;
	}

	public void setImageManager(ImagePluginManager imng) {
		this.imng = imng;
	}
	public void setSampleObjSender(DcmSender sampleObjSender) {
		this.sampleObjSender = sampleObjSender;
	}

	public DcmSender getSender(){
		DcmSender dsi = null;
		
		try {
			dsi = sampleObjSender.getClass().newInstance();
			
			dsi.setAeTitle(aeTitle);
			dsi.setHostname(hostname);
			dsi.setThrName(thrName);
			dsi.setRspDelay(rspDelay);
			dsi.setTempDirectory(tempDirectory);
			dsi.setTrnBufferSize(trnBufferSize);
			dsi.setBufferSize(bufferSize);
			dsi.setImng(imng);
			
			dsi.init();
		} catch (Exception e) {}
		
		return dsi;
	}
}
