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

//This file  has been created using source code of DcmRcv.java created by
//Gunter Zeilinger. Project dcm4che2

package es.urjc.mctwp.pacs.impl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.apache.naming.factory.BeanFactory;
import org.dcm4che2.data.BasicDicomObject;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.dcm4che2.data.UID;
import org.dcm4che2.io.DicomOutputStream;
import org.dcm4che2.net.Association;
import org.dcm4che2.net.Device;
import org.dcm4che2.net.DicomServiceException;
import org.dcm4che2.net.Executor;
import org.dcm4che2.net.NetworkApplicationEntity;
import org.dcm4che2.net.NetworkConnection;
import org.dcm4che2.net.NewThreadExecutor;
import org.dcm4che2.net.PDVInputStream;
import org.dcm4che2.net.Status;
import org.dcm4che2.net.TransferCapability;
import org.dcm4che2.net.service.StorageService;
import org.dcm4che2.net.service.VerificationService;

import es.urjc.mctwp.image.management.ImageCollectionManager;
import es.urjc.mctwp.pacs.DcmException;
import es.urjc.mctwp.pacs.DcmStorageServer;
import es.urjc.mctwp.service.Command;
import es.urjc.mctwp.service.ServiceDelegate;
import es.urjc.mctwp.service.commands.ModalityAllowed;

public class DcmStorageServerImpl extends StorageService implements DcmStorageServer{
	private ServiceDelegate service = null;
	private BeanFactory bf = null;

	//Objects for C-Store
	private NetworkApplicationEntity localAE = null;
	private NetworkConnection localConn = null;
	private ImageCollectionManager imc = null;
	private Executor executor = null;
	private Device device = null;
	private Logger logger = null;

	//Params
	private File tempDirectory = null;
	private String hostname = null;
	private String aeTitle = null;
	private String thrName = null;
	private int bufferSize = 1024;
	private int rspDelay = 0;
	private int port = 0;
	    
	//Constants
	private String[] tsuids = NON_RETIRED_LE_TS;
	private static final int KB = 1024;
	private static final String[] ONLY_DEF_TS = {
		UID.ImplicitVRLittleEndian
	};
	private static final String[] NON_RETIRED_LE_TS = {	
		UID.JPEGLSLossless,
		UID.JPEGLossless,
		UID.JPEGLosslessNonHierarchical14,
		UID.JPEG2000LosslessOnly,
		UID.DeflatedExplicitVRLittleEndian,
		UID.RLELossless,
		UID.ExplicitVRLittleEndian,
		UID.ImplicitVRLittleEndian,
		UID.JPEGBaseline1,
		UID.JPEGExtended24,
		UID.JPEGLSLossyNearLossless,
		UID.JPEG2000,
		UID.MPEG2
	};	    
	private static final String[] CUIDS = {	
		UID.BasicStudyContentNotificationSOPClassRetired,
		UID.StoredPrintStorageSOPClassRetired,
		UID.HardcopyGrayscaleImageStorageSOPClassRetired,
		UID.HardcopyColorImageStorageSOPClassRetired,
		UID.ComputedRadiographyImageStorage,
		UID.DigitalXRayImageStorageForPresentation,
		UID.DigitalXRayImageStorageForProcessing,
		UID.DigitalMammographyXRayImageStorageForPresentation,
		UID.DigitalMammographyXRayImageStorageForProcessing,
		UID.DigitalIntraoralXRayImageStorageForPresentation,
		UID.DigitalIntraoralXRayImageStorageForProcessing,
		UID.StandaloneModalityLUTStorageRetired,
		UID.EncapsulatedPDFStorage,
		UID.StandaloneVOILUTStorageRetired,
		UID.GrayscaleSoftcopyPresentationStateStorageSOPClass,
		UID.ColorSoftcopyPresentationStateStorageSOPClass,
		UID.PseudoColorSoftcopyPresentationStateStorageSOPClass,
		UID.BlendingSoftcopyPresentationStateStorageSOPClass,
		UID.XRayAngiographicImageStorage,
		UID.EnhancedXAImageStorage,
		UID.XRayRadiofluoroscopicImageStorage,
		UID.EnhancedXRFImageStorage,
		UID.XRayAngiographicBiPlaneImageStorageRetired,
		UID.PositronEmissionTomographyImageStorage,
		UID.StandalonePETCurveStorageRetired,
		UID.CTImageStorage,
		UID.EnhancedCTImageStorage,
		UID.NuclearMedicineImageStorage,
		UID.UltrasoundMultiframeImageStorageRetired,
		UID.UltrasoundMultiframeImageStorage,
		UID.MRImageStorage,
		UID.EnhancedMRImageStorage,
		UID.MRSpectroscopyStorage,
		UID.RTImageStorage,
		UID.RTDoseStorage,
		UID.RTStructureSetStorage,
		UID.RTBeamsTreatmentRecordStorage,
		UID.RTPlanStorage,
		UID.RTBrachyTreatmentRecordStorage,
		UID.RTTreatmentSummaryRecordStorage,
		UID.NuclearMedicineImageStorageRetired,
		UID.UltrasoundImageStorageRetired,
		UID.UltrasoundImageStorage,
		UID.RawDataStorage,
		UID.SpatialRegistrationStorage,
		UID.SpatialFiducialsStorage,
		UID.RealWorldValueMappingStorage,
		UID.SecondaryCaptureImageStorage,
		UID.MultiframeSingleBitSecondaryCaptureImageStorage,
		UID.MultiframeGrayscaleByteSecondaryCaptureImageStorage,
		UID.MultiframeGrayscaleWordSecondaryCaptureImageStorage,
		UID.MultiframeTrueColorSecondaryCaptureImageStorage,
		UID.VLImageStorageRetired,
		UID.VLEndoscopicImageStorage,
		UID.VideoEndoscopicImageStorage,
		UID.VLMicroscopicImageStorage,
		UID.VideoMicroscopicImageStorage,
		UID.VLSlideCoordinatesMicroscopicImageStorage,
		UID.VLPhotographicImageStorage,
		UID.VideoPhotographicImageStorage,
		UID.OphthalmicPhotography8BitImageStorage,
		UID.OphthalmicPhotography16BitImageStorage,
		UID.StereometricRelationshipStorage,
		UID.VLMultiframeImageStorageRetired,
		UID.StandaloneOverlayStorageRetired,
		UID.BasicTextSR,
		UID.EnhancedSR,
		UID.ComprehensiveSR,
		UID.ProcedureLogStorage,
		UID.MammographyCADSR,
		UID.KeyObjectSelectionDocument,
		UID.ChestCADSR,
		UID.StandaloneCurveStorageRetired,
		UID._12leadECGWaveformStorage,
		UID.GeneralECGWaveformStorage,
		UID.AmbulatoryECGWaveformStorage,
		UID.HemodynamicWaveformStorage,
		UID.CardiacElectrophysiologyWaveformStorage,
		UID.BasicVoiceAudioWaveformStorage,
		UID.HangingProtocolStorage,
		UID.SiemensCSANonImageStorage 
	};
	    
	public void setService(ServiceDelegate service) {
		this.service = service;
	}
	public ServiceDelegate getService() {
		return service;
	}
	public void setBf(BeanFactory bf) {
		this.bf = bf;
	}
	public BeanFactory getBf() {
		return bf;
	}
	public void setAeTitle(String aeTitle) {
		this.aeTitle = aeTitle;
	}
	public void setHostname(String hostname){
		this.hostname = hostname;
	}
	public void setBufferSize(int bufferSize){
		if( (bufferSize > 0) && (bufferSize < 1025) )
			this.bufferSize = bufferSize * KB;
	}
	public void setRspDelay(int rspDelay) {
		this.rspDelay = rspDelay;
	}
	public void setThrName(String thrName) {
		this.thrName = thrName;
	}
	@Override
	public void setImageCollectionManager(ImageCollectionManager imc) {
		this.imc = imc;
	}
	public void setPort(int port){
		this.port = port;
	}
	public void setTempDirectory(String tempDirectory) {
		if(tempDirectory != null)
			this.tempDirectory = new File(tempDirectory);
			if(!this.tempDirectory.exists())
				this.tempDirectory = null;
	}

	public DcmStorageServerImpl() {
		super(CUIDS);
		logger = Logger.getLogger(this.getClass());
    }
	
	/**
	 * Initialize server. 
	 * 
	 * @throws Exception if no properly configurated
	 */
	public void init() throws DcmException{
		
		//Check parameters configuration
		if( (aeTitle == null) || (thrName == null) || 
			(hostname == null) || (tempDirectory == null) || 
			(port == 0) ){
			
			String error = "Bad configuration, review server parameters";
			logger.error(error);
			throw new DcmException(error);
			
		}else{
		
			try{
				//Create objects
				executor 	= new NewThreadExecutor(thrName);
				device 		= new Device(thrName);
				localAE		= new NetworkApplicationEntity();
				localConn	= new NetworkConnection();
				
				//init objects
				device.setNetworkApplicationEntity(localAE);
				device.setNetworkConnection(localConn);
				localAE.setNetworkConnection(localConn);
				localAE.setAssociationAcceptor(true);
				localAE.register(new VerificationService());
				localAE.register(this);
				
				localAE.setAETitle(aeTitle);
				localConn.setHostname(hostname);
				localConn.setPort(port);
				localConn.setReceiveBufferSize(bufferSize);
				localConn.setSendBufferSize(bufferSize);
				
				initTransferCapability();
			}catch(Exception e){
				throw new DcmException(e);
			}
		}
	}
	
	public void start() throws DcmException{        
		try{
			device.startListening(executor );
		}catch(IOException ioe){
			throw new DcmException(ioe);
		}
	}
	
	public void stop(){
		device.stopListening();
	}

	private void initTransferCapability(){
		TransferCapability[] tc = new TransferCapability[CUIDS.length+1];
		tc[0] = new TransferCapability(UID.VerificationSOPClass, ONLY_DEF_TS, TransferCapability.SCP);

		for (int i = 0; i < CUIDS.length; i++)
			tc[i+1] = new TransferCapability(CUIDS[i], tsuids, TransferCapability.SCP);
	
		localAE.setTransferCapability(tc);        
	}

	/**
	 * Callback that stores DICOM received file into FS
	 */
	protected void doCStore(Association as, int pcid, DicomObject rq,
	        PDVInputStream dataStream, String tsuid, DicomObject rsp)
	        throws IOException, DicomServiceException {
		
		//Is modality allowed?
		Command cmd = getCommand(ModalityAllowed.class);
		((ModalityAllowed)cmd).setModality(as.getCallingAET());
		cmd = runCommand(cmd);
		
		if(((ModalityAllowed)cmd).getResult())
			try{
				//Get Dicom object
				String cuid = rq.getString(Tag.AffectedSOPClassUID);
				String iuid = rq.getString(Tag.AffectedSOPInstanceUID);
				BasicDicomObject fmi = new BasicDicomObject();
				fmi.initFileMetaInformation(cuid, iuid, tsuid);
				
				//Store temporary dicom file
				String filename = iuid + ".dcm";
				File file = new File(FilenameUtils.concat(tempDirectory.getAbsolutePath(), filename));
				FileOutputStream fos = new FileOutputStream(file);
				BufferedOutputStream bos = new BufferedOutputStream(fos, bufferSize);
				DicomOutputStream dos = new DicomOutputStream(bos);
				dos.writeFileMetaInformation(fmi);
				dataStream.copyTo(dos);
				dos.close();
				
				//Store into persistent space
				ArrayList<File> files = new ArrayList<File>();
				files.add(file);
				imc.storeTemporalImages(as.getCallingAET(), files);
				file.delete();
			}catch (Exception e){
				throw new DicomServiceException(rq, Status.ProcessingFailure, e.getMessage());
			}
	
		//Wait if there is delay configured
		if (rspDelay > 0)
			try{
				Thread.sleep(rspDelay);
			}catch (InterruptedException e) {}
	}
	
	/**
	 * Return new instance of command identified by clazz
	 * 
	 * @param clase
	 * @return
	 */
	private Command getCommand(Class<?> clazz){
		Command command = null;
		
		//Build a new command using Web Application Context (wac)
		try{
			Constructor<?> c = clazz.getConstructor(BeanFactory.class);
			command = (Command)c.newInstance(bf);
		}catch(Exception e){
			logger.error("Error creating new command [" + clazz.toString() + "] : " + e.getMessage());
		}

		return command;
	}
	
	/**
	 * It runs a command safely and catching all error info. If commands fail, 
	 * it will redirect action to error page.
	 * 
	 * @param cmd
	 * @return
	 */
	private Command runCommand(Command cmd){
		Command result = cmd;

		try{
			result = service.runCommand(cmd);
		}catch(Exception ce){
			logger.error("Error running command [" + cmd.toString() + "] : " + ce.getMessage());
		}
		
		return result;
	}
	
}
