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

package es.urjc.mctwp.service.blogic;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.dao.DataIntegrityViolationException;

import es.urjc.mctwp.dao.ImageDataDAO;
import es.urjc.mctwp.dao.ImageTypeDAO;
import es.urjc.mctwp.dao.PatientDAO;
import es.urjc.mctwp.dao.ResultDAO;
import es.urjc.mctwp.dao.StudyDAO;
import es.urjc.mctwp.dao.TaskDAO;
import es.urjc.mctwp.image.exception.ImageCollectionException;
import es.urjc.mctwp.image.exception.ImageException;
import es.urjc.mctwp.image.impl.dicom.DicomImagePlugin;
import es.urjc.mctwp.image.management.ImageCollectionManager;
import es.urjc.mctwp.image.management.ImagePluginManager;
import es.urjc.mctwp.image.objects.DicomSCHeaderAttrs;
import es.urjc.mctwp.image.objects.Image;
import es.urjc.mctwp.image.objects.PatientInfo;
import es.urjc.mctwp.image.objects.ThumbNail;
import es.urjc.mctwp.modelo.Group;
import es.urjc.mctwp.modelo.ImageData;
import es.urjc.mctwp.modelo.Patient;
import es.urjc.mctwp.modelo.Process;
import es.urjc.mctwp.modelo.Protocolable;
import es.urjc.mctwp.modelo.Result;
import es.urjc.mctwp.modelo.Study;
import es.urjc.mctwp.modelo.Task;
import es.urjc.mctwp.modelo.Trial;
import es.urjc.mctwp.modelo.User;
import es.urjc.mctwp.pacs.DcmDestination;
import es.urjc.mctwp.pacs.DcmSender;
import es.urjc.mctwp.pacs.DcmSenderFactory;

public class ImageUtils extends AbstractBLogic{
	//DAO's
	private TaskDAO taskDao = null;
	private StudyDAO studyDao = null;
	private ResultDAO resultDao = null;
	private PatientDAO patientDao = null;
	private ImageDataDAO imageDataDao = null;
	private ImageTypeDAO imageTypeDao = null;
	
	//Other business objects
	private ImagePluginManager imgManager = null;
	private DcmSenderFactory senderFactory = null;
	private ImageCollectionManager imgColManager = null;
	private DicomImagePlugin dicomPlugin = null;
	
	public void setTaskDao(TaskDAO taskDao) {
		this.taskDao = taskDao;
	}
	public TaskDAO getTaskDao() {
		return taskDao;
	}
	public void setStudyDao(StudyDAO studyDao) {
		this.studyDao = studyDao;
	}
	public StudyDAO getStudyDao() {
		return studyDao;
	}
	public void setResultDao(ResultDAO resultDao) {
		this.resultDao = resultDao;
	}
	public ResultDAO getResultDao() {
		return resultDao;
	}
	public void setPatientDao(PatientDAO patientDao) {
		this.patientDao = patientDao;
	}
	public PatientDAO getPatientDao() {
		return patientDao;
	}
	public void setImageManager(ImagePluginManager imgManager) {
		this.imgManager = imgManager;
	}
	public ImagePluginManager getImageManager() {
		return imgManager;
	}
	public void setImageDataDao(ImageDataDAO imageDataDao) {
		this.imageDataDao = imageDataDao;
	}
	public ImageDataDAO getImageDataDao() {
		return imageDataDao;
	}
	public void setImageTypeDao(ImageTypeDAO imageTypeDao) {
		this.imageTypeDao = imageTypeDao;
	}
	public ImageTypeDAO getImageTypeDao() {
		return imageTypeDao;
	}
	public void setSenderFactory(DcmSenderFactory senderFactory) {
		this.senderFactory = senderFactory;
	}
	public DcmSenderFactory getSenderFactory() {
		return senderFactory;
	}
	public void setImageColManager(ImageCollectionManager imgColManager) {
		this.imgColManager = imgColManager;
	}
	public ImageCollectionManager getImageColManager() {
		return imgColManager;
	}

	public void setDicomPlugin(DicomImagePlugin dicomPlugin) {
		this.dicomPlugin = dicomPlugin;
	}
	public DicomImagePlugin getDicomPlugin() {
		return dicomPlugin;
	}
	public void storeTemporalImages(User user, List<File> files) throws ImageCollectionException, ImageException, IOException{
		if(files != null){
			imgColManager.storeTemporalImages(user.getLogin(), files);
			
			for(File file : files)
				file.delete();
		}
	}
	
	/**
	 * Sends to destination a list of images identified by a 
	 * list of ids
	 * 
	 * @param images
	 * @param destination
	 */
	public void sendImages(List<String> imagesId, String col, DcmDestination destination) throws ImageException, ImageCollectionException{
		DcmSender sender = getSenderFactory().getSender();
		List<Image> images = null;
		
		if( (sender != null) && (imagesId != null) && (!imagesId.isEmpty()) &&
			( (destination != null) && (destination.isValid()) ) ){
			
			images = new ArrayList<Image>();
			
			for(String id : imagesId){
				Image image = imgColManager.getImage(col, id, false);
				images.add(image);
			}
			
			try{
				sender.sendImages(images, destination);
			}catch(Exception e){
				throw new ImageException("Images can not be sended", e);
			}
		}
	}
	
	/**
	 * Sends to destination a list of images identified by a 
	 * list of ids
	 * 
	 * @param images
	 * @param destination
	 */
	public void sendImageAsSC(String imageId, String col, DcmDestination destination, DicomSCHeaderAttrs header) throws ImageException, ImageCollectionException{
		DcmSender sender = getSenderFactory().getSender();
		
		if( (sender != null) && (imageId != null) && (header != null) &&
			( (destination != null) && (destination.isValid()) ) ){
			
			Image image = imgColManager.getImage(col, imageId, false);
			sender.sendImageAsSC(image, header, destination, dicomPlugin);
		}
	}
	
	/**
	 * At this moment there is no choice to store automatically a set of images into 
	 * a trial, because there is no way to know the group to store images in.
	 * 
	 * @param tempColl
	 * @param group
	 * @param images
	 * @throws Exception 
	 */
	public void persistImagesTrial(String tempColl, Trial trial, List<String> imagesId, Integer imgType) throws Exception{
		throw new Exception("There is not possible to add images to a trial directly, you must select a group");
	}
		
	/**
	 * It moves a set of images from tempCollection to proper study collection 
	 * into selected group. This method find patient and study info into image
	 * and if patient or study don't exist it create before.
	 * 
	 * @param tempColl
	 * @param group
	 * @param images
	 * @throws Exception 
	 */
	public void persistImagesGroup(String tempColl, Group group, List<String> imagesId, Integer imgType) throws Exception{
		List<PatientInfo> patInfo = new ArrayList<PatientInfo>();
		List<Image> images = new ArrayList<Image>();
		Study study = null;
		
		if( (tempColl == null) || (group == null) || ( (imagesId == null) || (imagesId.isEmpty()) ) ) return;
		
		//Check all images has patient and study info
		for(String idImage: imagesId){
			Image auxImg = imgColManager.getImage(tempColl, idImage, true);
			PatientInfo auxInf = getImageManager().obtainPatientStudyInfo(auxImg);
			
			if( (auxImg == null) || (auxInf == null) || 
				(auxInf.getName() == null) ||
				(auxInf.getCode() == null) ||
				(auxInf.getStudy() == null) )
				
				throw new Exception("Image [" + idImage + "] has no patient or study information");
			
			patInfo.add(auxInf);
			images.add(auxImg);
		}
			
		//If all images are ok, store it
		for(int i = 0; i < images.size(); i++ ){
			Image auxImg = images.get(i);
			PatientInfo auxInf = patInfo.get(i);
			
			//Create or retrieve patient and study information
			study = findOrCreatePatientStudy(auxInf, group);

			//Move image from tempCollection to trial collection
			String collection = group.getTrial().getCollection();
			persistImage(tempColl, collection, auxImg.getId(), group, study, imgType);
		}
	}
		
	/**
	 * It moves a set of images from tempCollection to proper study collection 
	 * into selected patient. This method find study info into image
	 * and if study doesn't exist it creates before.
	 * 
	 * @param tempColl
	 * @param group
	 * @param images
	 * @throws Exception 
	 */
	public void persistImagesPatient(String tempColl, Patient patient, List<String> imagesId, Integer imgType) throws Exception{
		List<PatientInfo> patInfo = new ArrayList<PatientInfo>();
		List<Image> images = new ArrayList<Image>();
		Study study = null;
		
		if( (tempColl == null) || (patient == null) || ( (imagesId == null) || (imagesId.isEmpty()) ) ) return;

		//Check all images has patient and study info
		for(String idImage: imagesId){
			Image auxImg = imgColManager.getImage(tempColl, idImage, true);
			PatientInfo auxInf = getImageManager().obtainPatientStudyInfo(auxImg);
			
			if( (auxImg == null) || (auxInf == null) || 
				(auxInf.getStudy() == null) )
				
				throw new Exception("Image [" + idImage + "] has no study information");
			
			patInfo.add(auxInf);
			images.add(auxImg);
		}

		//If all images are ok, store it
		for(int i = 0; i < images.size(); i++ ){
			Image auxImg = images.get(i);
			PatientInfo auxInf = patInfo.get(i);
			
			//Create or retrieve patient and study information
			study = findOrCreateStudy(auxInf, patient);

			//Move image from tempCollection to trial collection
			String collection = patient.getCollection();
			persistImage(tempColl, collection, auxImg.getId(), patient, study, imgType);
		}
	}
	
	/**
	 * This is very similar to persistImages, but it relates the images to store 
	 * with a task, and also creates a result object for each image.
	 * 
	 * @param tempColl
	 * @param task
	 * @param images
	 * @throws Exception
	 */
	public void persistImageResult(String tempColl, Result result, List<String> images, Integer imgType) throws Exception{
		if(images == null) return;
		
		for(String idImage: images){
			String collection = result.getTask().getSource().getCollection();
			
			//create database image object
			ImageData imageData = new ImageData();
			imageData.setDate(new Date());
			imageData.setImageId(idImage);
			imageData.setImgType(imageTypeDao.findById(imgType));
			imageData.setImageType("");

			result.addImage(imageData);
			
			//Obtain thumbnail
			ThumbNail tn = imgColManager.getThumbNail(tempColl, idImage, true);
			imageData.setThumbanilContent(new FileInputStream(tn.getContent()));
			
			try{
				imageDataDao.persist(imageData);
				imgColManager.acceptTemporalImage(tempColl, collection, idImage);
			}catch (RuntimeException re){
				
				//Means, image inserted already exist into DB, but new images have been added to collection
				if(!(re instanceof DataIntegrityViolationException)){
					//logger.error(re.getMessage());
					throw re;
				}
			}
		}		
	}

	/**
	 * It moves a set of images from tempCollection to study collection. 
	 * The images are associated with the study.
	 * 
	 * @param tempColl Collection where images are readed from
	 * @param study 
	 * @param images
	 * @throws Exception
	 */
	public void persistImagesStudy(String tempColl, Study study, List<String> images, Integer imgType) throws Exception{
		if( (tempColl == null) || (study == null) || ( (images == null) || (images.isEmpty()))) return;
		
		for(String idImage: images){
			
			//Move image from tempCollection to trial collection
			String collection = study.getPatient().getGroup().getTrial().getCollection();
			persistImage(tempColl, collection, idImage, study, study, imgType);
		}		
	}
	
	/**
	 * Private routine that store a image into a collection, it is used by persistImages and
	 * persistImagesGroup. This method must be transactional, if an error occurs it must be 
	 * rollback every change it does.
	 * 
	 * @param tempColl
	 * @param defColl
	 * @param idImage
	 * @param study
	 * @throws Exception
	 */
	private void persistImage(String tempColl, String defColl, String idImage, Protocolable source, Study study, Integer imgType) throws Exception{
		ImageData imageData = null;
		
		//First, it is necessary to  check if image already exist into study, it is possible to
		//add new images to a series.
		imageData = imageDataDao.findImageByUidAndStudy(idImage, study.getCode());
		
		//If there is no existent image data, it creates new database image object
		if(imageData == null){
			imageData = new ImageData();
			imageData.setStudy(study);
			imageData.setDate(new Date());
			imageData.setImageId(idImage);
			imageData.setResult(null);
			imageData.setImageType("");
			imageData.setImgType(imageTypeDao.findById(imgType));

			//Obtain thumbnail
			ThumbNail tn = imgColManager.getThumbNail(tempColl, idImage, true);
			imageData.setThumbanilContent(new FileInputStream(tn.getContent()));
		}
		
		try{
			if(imageData.getCode() == 0){
				imageDataDao.persist(imageData);
				createTasks(imageData, source, imgType);
			}
			imgColManager.acceptTemporalImage(tempColl, defColl, idImage);
		}catch (RuntimeException re){
			
			//Means, image inserted already exist into DB, but new images have been added to collection
			if(!(re instanceof DataIntegrityViolationException)){
				//logger.error(re.getMessage());
				throw re;
			}
		}		
	}
	
	/**
	 * It search patient and study using info's identifiers, if patient or study doesn't
	 * exists, it creates.
	 *  
	 * @param info
	 * @return
	 */
	private Study findOrCreatePatientStudy(PatientInfo info, Group group){
		Study result = null;
		boolean searchStudy = true;
		boolean savePatient = false;
		
		if(info != null){
			
			//Search or create Patient
			Patient patient = patientDao.findPatientInGroup(group.getCode(), info.getCode());
			if(patient == null){
				searchStudy = false;
				patient = new Patient();
				patient.setHospitalId(info.getCode());
				patient.setCompleteName(info.getName());
				patient.setGroup(group);
			}
			
			//Search or create Study
			if(searchStudy)
				result = studyDao.findStudyInPatient(patient.getCode(), info.getStudy());
			
			if(result == null){
				savePatient = true;
				result = new Study();
				result.setDate(new Date());
				result.setHospitalId(info.getStudy());
				patient.addStudy(result);
			}
			
			//Persist new Patient and/or study only if necessary
			if(!searchStudy || savePatient)
				patientDao.persist(patient);
		}
			
		return result;
	}
	
	/**
	 * It search study using info's identifiers, if study doesn't
	 * exists, it creates.
	 *  
	 * @param info
	 * @return
	 */
	private Study findOrCreateStudy(PatientInfo info, Patient patient){
		Study result = null;
		boolean saveStudy = false;
		
		if(info != null){
			
			//Search or create Study
			result = studyDao.findStudyInPatient(patient.getCode(), info.getStudy());
			
			if(result == null){
				saveStudy = true;
				result = new Study();
				result.setDate(new Date());
				result.setHospitalId(info.getStudy());
				patient.addStudy(result);
			}
			
			//Persist new Patient and/or study only if necessary
			if(saveStudy)
				studyDao.persist(result);
		}
			
		return result;
	}
		
	/**
	 * Wrapper method to avoid create a HashSet everytime 
	 * @param image
	 * @param source
	 */
	private void createTasks(ImageData image, Protocolable source, Integer imgType){
		HashSet<ImageData> set = new HashSet<ImageData>();
		set.add(image);
		createTasks(set, source, imgType);
	}

	/**
	 * It creates automatically tasks for all process of the study protocol.
	 * 
	 * @param image
	 * @param study
	 */
	private void createTasks(Set<ImageData> images, Protocolable source, Integer imgType){
		Task task = null;
		
		if( (images != null) && !(images.isEmpty()) && (source.getAllProcesses() != null) ){
			
			for (Process p : source.getAllProcesses()){
				if(p.getImgType().getCode().equals(imgType)){
					Calendar cal = Calendar.getInstance();
					cal.add(Calendar.DAY_OF_MONTH, p.getDaysToDo());
					task = new Task();
					
					task.setProcess(p.getProcessDef());
					task.setOwner(p.getOwner());
					task.setEndDate(cal.getTime());
					task.setSource(source);
					
					//ImageData.addTask is the convenience method
					for(ImageData img : images)
						img.addTask(task);
					
					this.taskDao.persist(task);
				}
			}
		}
	}
	
	/**
	 * It returns Dicom Header of a Dicom image
	 * @param orig
	 * @return
	 * @throws ImageCollectionException 
	 * @throws ImageException 
	 */
	public DicomSCHeaderAttrs getDicomHeader(String imageId, String collection) throws ImageException, ImageCollectionException {
		DicomSCHeaderAttrs result = null;
		
		Image img = imgColManager.getImage(collection, imageId, false);
		if(dicomPlugin != null)
			result = dicomPlugin.getDicomHeader(img);
		
		return result;
	}
}
