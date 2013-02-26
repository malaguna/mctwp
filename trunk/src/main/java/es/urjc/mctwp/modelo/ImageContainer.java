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

package es.urjc.mctwp.modelo;

import java.util.List;
import java.util.Set;

import es.urjc.mctwp.service.blogic.ImageContainerTypeVisitor;
import es.urjc.mctwp.service.blogic.PersistImagesVisitor;

/**
 * This is not a persistent entity, it is a class to provide common
 * functionality to persist images. It is related with PersistImagesVisitor
 * 
 * @author miguel
 *
 */
public abstract class ImageContainer extends DomainObject {
	private static final long serialVersionUID = 7432967495288899879L;
	
	public abstract void accept(PersistImagesVisitor piv, List<String> imagesId, String tempCol, Integer imgType) throws Exception;
	public abstract Class<? extends ImageContainer> accept(ImageContainerTypeVisitor imcv);
	public abstract Set<ImageData> getAllImages();
	public abstract String getDescription();
	public abstract String getCollection();
	public abstract String getType();
}
