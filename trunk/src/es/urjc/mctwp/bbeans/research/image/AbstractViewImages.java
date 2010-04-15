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

package es.urjc.mctwp.bbeans.research.image;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.event.ActionEvent;

import org.apache.commons.io.FilenameUtils;

import es.urjc.mctwp.bbeans.RequestInvAbstractBean;
import es.urjc.mctwp.image.objects.ThumbNail;
import es.urjc.mctwp.modelo.ImageData;
import es.urjc.mctwp.pacs.DcmDestination;
import es.urjc.mctwp.service.Command;
import es.urjc.mctwp.service.commands.imageCmds.LoadImageData;
import es.urjc.mctwp.service.commands.imageCmds.SendImages;

public class AbstractViewImages extends RequestInvAbstractBean {
	private DcmDestination destination = new DcmDestination();
	private List<ThumbSelectItem> thumbs = null;
	
	public DcmDestination getDestination(){
		return destination;
	}

	public void setDestination(DcmDestination dest){
		this.destination = dest;
	}	
	
	public List<ThumbSelectItem> getThumbs(){
		return thumbs;
	}
	
	public void setThumbs(List<ThumbSelectItem> images) {
		this.thumbs = images;
	}	
	
	/**
	 * It retrieve image from ActionEvent and update selected 
	 * image into session state and sets list mode to image.
	 *  
	 * @param event
	 */
	public void alUpdateImageSelected(ActionEvent event) {
		getSession().setImage(null);
		ThumbSelectItem tsi = (ThumbSelectItem)selectRowFromEvent(event, ThumbSelectItem.class);

		if(tsi != null){
			Command cmd = getCommand(LoadImageData.class);
			((LoadImageData)cmd).setImageId(tsi.getImageId());
			cmd = runCommand(cmd);
			getSession().setImage(((LoadImageData)cmd).getResult());
		}
	}		

	/**
	 * It sends all selected images to destination
	 * @return
	 */
	public void sendImages(){
		List<String> images = new ArrayList<String>();
		
		//Get id's of selected images
		for(ThumbSelectItem tsi: thumbs){
			if(tsi.getSelected()){
				images.add(tsi.getThumbId());
			}
		}
		
		//Send images
		Command cmd = getCommand(SendImages.class);
		((SendImages)cmd).setDestination(destination);
		((SendImages)cmd).setImages(images);
		runCommand(cmd);
	}
	
	/**
	 * It prepares a valid thumbnail for JSF from a valid ImageDate. 
	 * It writes the thumbnail image into a file contained in the 
	 * user session dir.
	 * 
	 * @param image
	 * @return
	 * @throws IOException
	 * @throws SQLException
	 * @throws FileNotFoundException
	 */
	protected ThumbSelectItem getThumbnailContent(ImageData image) throws IOException, SQLException, FileNotFoundException{
		String base = getSession().getAbsoluteThumbDir();
		String name = image.getImageId() + "." + ThumbNail.TBN_EXT;
		int length  = image.getThumbnailSize();
		
		//Create file for thumbnail and get output stream to write it
		File file = new File(FilenameUtils.concat(base, name));
		FileOutputStream fos = new FileOutputStream(file);
		
		//write thumbnail content into file
		byte[] thContent = new byte[length];
		image.getThumbnailStream().read(thContent, 0, length);
		fos.write(thContent);
		fos.close();
		
		//Create thumbnail and view layer thumbnail. It is neccesary to
		//reference the thumbnail from a relative path in order to work
		//into view
		base = getSession().getRelativeThumbDir();
		file = new File(FilenameUtils.concat(base, name));
		ThumbNail tn = new ThumbNail();
		tn.setContent(file);
		tn.setId(image.getImageId());
		
		ThumbSelectItem tsi = new ThumbSelectItem();
		tsi.setThumbId(tn.getId());
		tsi.setPath(tn.getContent().getPath());
		tsi.setImageId(image.getCode());
		return tsi;
	}
	
	public String accPrepareImagesToDelete(){
		List<Integer> toDelete = null;
		
		if( (thumbs != null) && (thumbs.size() > 0) ){
			toDelete = new ArrayList<Integer>();
			for(ThumbSelectItem tsi: thumbs){
				if(tsi.getSelected()){
					toDelete.add(tsi.getImageId());
				}
			}
		}
		
		ImageDeletionBean bean = (ImageDeletionBean) this.getBackBeanReference("imageDeletionBean");
		bean.setToDelete(toDelete);
		
		return bean.prepareImageDeletion();
	}	
}
