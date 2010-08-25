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

import org.apache.naming.factory.BeanFactory;

import es.urjc.mctwp.image.management.ImageCollectionManager;
import es.urjc.mctwp.service.ServiceDelegate;

public interface DcmStorageServer {
	
	public void setImageCollectionManager(ImageCollectionManager imc);
	public void setService(ServiceDelegate service);
	public void setBf(BeanFactory bf);

	public void init() throws DcmException;
	public void start() throws DcmException;
	public void stop();
}
