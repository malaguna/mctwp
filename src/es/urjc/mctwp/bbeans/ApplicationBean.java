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

package es.urjc.mctwp.bbeans;

import es.urjc.mctwp.pacs.DcmException;
import es.urjc.mctwp.pacs.DcmStorageServer;

public class ApplicationBean extends AbstractBean {
	private DcmStorageServer pacs = null;
	private boolean runningPacs = false;
	
	public boolean isRunningPacs() {
		return runningPacs;
	}
	
	public DcmStorageServer getPacs() {
		return pacs;
	}
	
	public void setPacs(DcmStorageServer server){
		pacs = server;
		try{
			pacs.init();
			pacs.setService(getService());
			startPacs();
		}
		catch(DcmException msse){
		}
	}
	
	public void startPacs(){
		try{
			pacs.start();
			runningPacs = true;
		}
		catch(DcmException msse){
		}
	}

	public void stopPacs(){
		pacs.stop();
		runningPacs = false;
	}
}
