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

package es.urjc.mctwp.service.commands.imageCmds;

import java.util.List;

import org.springframework.beans.factory.BeanFactory;

import es.urjc.mctwp.service.blogic.PersistImagesVisitor;
import es.urjc.mctwp.modelo.ImageContainer;
import es.urjc.mctwp.service.ActionNames;
import es.urjc.mctwp.service.BeanNames;
import es.urjc.mctwp.service.Command;

public class PersistImages extends Command {
	private PersistImagesVisitor piv = null;
	private List<String> images = null;
	private ImageContainer source = null;
	private String tempColl = null;
	private Integer imgType = null;

	public PersistImages(BeanFactory bf) {
		super(bf);
		piv = (PersistImagesVisitor)bf.getBean(BeanNames.PERSIST_IMG_VISITOR);
		setActionName(ActionNames.PERSIST_IMAGES);
		setReadOnly(false);
	}
	
	public void setImages(List<String> images) {
		this.images = images;
	}
	public List<String> getImages() {
		return images;
	}
	public void setTempColl(String tempCol) {
		this.tempColl = tempCol;
	}
	public String getTempCol() {
		return tempColl;
	}
	public void setImgType(Integer imgType) {
		this.imgType = imgType;
	}

	public Integer getImgType() {
		return imgType;
	}

	public void setSource(ImageContainer protocolable) {
		this.source = protocolable;
	}
	public ImageContainer getSource() {
		return source;
	}

	@Override 
	public boolean isValidCommand(){
		return  super.isValidCommand() &&
				piv != null &&
				tempColl != null &&
				images != null &&
				imgType != null &&
				source != null;
	}

	@Override
	public Command runCommand() throws Exception{
		source.accept(piv, images, tempColl, imgType);
		createLogComment("audit.persistImage", images.size(), tempColl, source.getType(), source.getDescription());
		return this;
	}
}
