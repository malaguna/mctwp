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

//This file  has been created using source code of DcmSnd.java created by
//Gunter Zeilinger. Project dcm4che2

package es.urjc.mctwp.pacs.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.dcm4che2.data.BasicDicomObject;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.dcm4che2.data.UID;
import org.dcm4che2.data.UIDDictionary;
import org.dcm4che2.io.DicomInputStream;
import org.dcm4che2.io.StopTagInputHandler;
import org.dcm4che2.net.Association;
import org.dcm4che2.net.ConfigurationException;
import org.dcm4che2.net.Device;
import org.dcm4che2.net.DimseRSPHandler;
import java.util.concurrent.Executor;
import org.dcm4che2.net.NetworkApplicationEntity;
import org.dcm4che2.net.NetworkConnection;
import org.dcm4che2.net.NewThreadExecutor;
import org.dcm4che2.net.TransferCapability;

import es.urjc.mctwp.image.exception.ImageException;
import es.urjc.mctwp.image.impl.dicom.DicomImagePlugin;
import es.urjc.mctwp.image.objects.DicomSCHeaderAttrs;
import es.urjc.mctwp.image.objects.Image;
import es.urjc.mctwp.pacs.DcmDestination;
import es.urjc.mctwp.pacs.DcmException;
import es.urjc.mctwp.pacs.DcmSender;

public class DcmSenderImpl extends DcmSender{

	//Objects for C-Store
	private NetworkApplicationEntity remoteAE = null;
	private NetworkApplicationEntity localAE = null;
	private NetworkConnection remoteConn = null;
	private NetworkConnection localConn = null;
	private Association assoc = null;
	private Executor executor = null;
	private Device device = null;
	private Logger logger = null;
	private HashMap<String, Set<String>> as2ts = null;
	    
	//Constants 
	private static final int PEEK_LEN = 1024;
	private static final String[] IVLE_TS = { 
		UID.ImplicitVRLittleEndian,
		UID.ExplicitVRLittleEndian, 
		UID.ExplicitVRBigEndian,
	};
	private static final String[] EVLE_TS = {
		UID.ExplicitVRLittleEndian,
		UID.ImplicitVRLittleEndian,
		UID.ExplicitVRBigEndian, 
	};
	private static final String[] EVBE_TS = { 
		UID.ExplicitVRBigEndian,
		UID.ExplicitVRLittleEndian, 
		UID.ImplicitVRLittleEndian, 
	};

	
	public DcmSenderImpl (){
		logger = Logger.getLogger(this.getClass());
    }
	
	/**
	 * Log an error with important information for correction
	 * 
	 * @param obj
	 * @param method
	 * @param e
	 */
	private void logErrMsg(String method, Exception e){
		String classDsc = "DcmSenderImpl";
		String cadError = "Class [" + classDsc + "], " +
						"Method [" + method + "], " +
						"Error: " + e.getMessage();
		
		logger.error(cadError);
		
	}
	

	/**
	 * Initialize local infrastructure to allow image sending
	 * 
	 * @throws DcmException
	 */
	@Override
	public void init() throws DcmException{
		String error = null;
		
		//Check parameters configuration
		if( (aeTitle == null) || (thrName == null) || 
			(hostname == null) || (tempDirectory == null) ) {
			
			error = "Bad configuration, review server parameters";
			
		}else{
		
			try{
				//Create objects
				executor = new NewThreadExecutor(thrName);
				device = new Device(thrName);
				
				remoteAE = new NetworkApplicationEntity();
				localAE	= new NetworkApplicationEntity();
				remoteConn = new NetworkConnection();
				localConn = new NetworkConnection();
				
				//init objects
		        remoteAE.setInstalled(true);
		        remoteAE.setAssociationAcceptor(true);
		        remoteAE.setNetworkConnection(remoteConn);

				device.setNetworkApplicationEntity(localAE);
				device.setNetworkConnection(localConn);

				localAE.setNetworkConnection(localConn);
				localAE.setAssociationInitiator(true);
				localAE.setAETitle(aeTitle);

				localConn.setHostname(hostname);
				localConn.setReceiveBufferSize(bufferSize);
				localConn.setSendBufferSize(bufferSize);
				
				as2ts = new HashMap<String, Set<String>>();
				
			}catch(Exception e){
				error = e.getMessage();
			}

			if(error != null){
				DcmException msse =  new DcmException(error);
				logErrMsg("init", msse);
				throw msse;
			}
		}
	}
	
	/**
	 * This method tries to connect with the destination, parse all files 
	 * and try to set correct transfer syntax and finally send images.
	 * 
	 * TODO prepare exception for different kind of errors:
	 *  - destination not found
	 *  - bad image
	 *  - unsupported format
	 *  - etc.
	 * 
	 * @param destination
	 * @param images
	 */
	public void sendImages(List<Image> images, DcmDestination destination){
		List<File> files = null;
		List<FileInfo> parsedFiles = null;
		
		if( (destination != null) && (images != null) && (!images.isEmpty()) ){
			
			try {
				files = obtainDicomFiles(images);
				parsedFiles = parseFiles(files);
				configureTransferCapability();
				connectTo(destination);
				sendFiles(parsedFiles, null);

			} catch (Exception e) {
				logErrMsg("sendImages", e);
				//TODO Throws own exceptions
			}			
		}
	}
	
	/**
	 * This method convert image to Dicom and override header info. Then
	 * tries to connect with the destination, parse new Dicom file 
	 * and try to set correct transfer syntax and finally send image.
	 * 
	 * TODO prepare exception for different kind of errors:
	 *  - destination not found
	 *  - bad image
	 *  - unsupported format
	 *  - etc.
	 * 
	 * @param destination
	 * @param dicomHeader
	 * @param image
	 */
	public void sendImageAsSC(Image image, DicomSCHeaderAttrs header, DcmDestination destination, DicomImagePlugin plugin){
		List<File> files = null;
		List<FileInfo> parsedFiles = null;
		
		if( (destination != null) && (image != null) && (header != null) ){
			
			try {
				files = getImng().obtainDICOM(image, tempDirectory);
				
				//Override Dicom Header
				if(files != null && files.size() == 1){
					parsedFiles = parseFiles(files);
					configureTransferCapability();
					connectTo(destination);
					sendFiles(parsedFiles, header);
				}else{
					throw new Exception("There are no files, or there more than one files");
				}

			} catch (Exception e) {
				logErrMsg("sendSCImage", e);
				//TODO Throws own exceptions
			}			
		}
	}
	
	/**
	 * Tries to connect to destination part.
	 * 
	 * @param destination
	 * @throws ConfigurationException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private void connectTo(DcmDestination destination) throws ConfigurationException, IOException, InterruptedException{
		
		remoteAE.setAETitle(destination.getAeTitle());
		remoteConn.setHostname(destination.getHostname());
		remoteConn.setPort(destination.getPort());
		
		assoc = localAE.connect(remoteAE, executor);
	}
	
	/**
	 * Get images and transform to DICOM objects.
	 * 
	 * @param images
	 * @return
	 */
	private List<File> obtainDicomFiles(List<Image> images) throws ImageException{
		List<File> result = null;
		List<File> aux = null;
		
		if( (images != null) && (!images.isEmpty()) ){
			result = new ArrayList<File>();
			
			for(Image i : images){
				aux = getImng().obtainDICOM(i, tempDirectory);
				
				if( (aux != null) && (!aux.isEmpty()) )
					result.addAll(aux);
			}
		}
		
		return result;
	}
	
	/**
	 * Parse a list of DICOM files in order to send it
	 * 
	 * @param files
	 */
	private List<FileInfo> parseFiles(List<File> files) throws DcmException{
		List<FileInfo> result = null;
		String error = null;
		
		if( (files != null) && (!files.isEmpty()) ){
			result = new ArrayList<FileInfo>();
			
			for(File file : files){
				
				FileInfo info = new FileInfo(file);
				DicomObject dcmObj = new BasicDicomObject();
				try {
					DicomInputStream dis = new DicomInputStream(file);
					try {
						dis.setHandler(new StopTagInputHandler(Tag.StudyDate));
						dis.readDicomObject(dcmObj, PEEK_LEN);
						info.tsuid = dis.getTransferSyntax().uid();
						info.fmiEndPos = dis.getEndOfFileMetaInfoPosition();
					} finally {
						try {dis.close();} catch (IOException ignore) {}
					}
				} catch (IOException e) {
					error = "Failed to parse " + file + ", error: " + e.getMessage();
				}
				
				info.cuid = dcmObj.getString(Tag.SOPClassUID);
				if (info.cuid == null)
					error = "Missing SOP Class UID in " + file;

				info.iuid = dcmObj.getString(Tag.SOPInstanceUID);
				if (info.iuid == null)
					error = "Missing SOP Instance UID in " + file;

				if(error != null){
					throw new DcmException(error);
				}else{
					addTransferCapability(info.cuid, info.tsuid);
					result.add(info);
				}
			}
		}
		
		return result;
	}
	
	/**
	 * Sends a list of DICOM objects to destination
	 * 
	 * @param files
	 */
	private void sendFiles(List<FileInfo> parsedFiles, DicomSCHeaderAttrs header) throws DcmException{
		if( (parsedFiles != null) && (!parsedFiles.isEmpty()) ){

			for(FileInfo info : parsedFiles){
	            
	            TransferCapability tc = assoc.getTransferCapabilityAsSCU(info.cuid);
	            if (tc == null) {
	            	String error = UIDDictionary.getDictionary().prompt(info.cuid) +
	            	 			" not supported by " + remoteAE.getAETitle();
	            	
	            	throw new DcmException(error);
	            }

	            String tsuid = selectTransferSyntax(tc.getTransferSyntax(), info.tsuid);
	            if (tsuid == null) {
	                String error = UIDDictionary.getDictionary().prompt(info.cuid)
	                        + " with " + UIDDictionary.getDictionary().prompt(info.tsuid)
	                        + " not supported by" + remoteAE.getAETitle();

	                throw new DcmException(error);
	            }
	            
	            try {	            	
	                DimseRSPHandler rspHandler = new DimseRSPHandler() {
	                    public void onDimseRSP(Association as, DicomObject cmd, DicomObject data) {
							int status = cmd.getInt(Tag.Status);
							
							switch (status) {
								case 0:
									break;
								case 0xB000:
								case 0xB006:
								case 0xB007:
									break;
							}
	                    }
	                };

	                DataWriter dw = new DataWriter(info);
	                dw.setHeader(header);
	                dw.setTranscoderBufferSize(trnBufferSize);
	                assoc.cstore(info.cuid, info.iuid, priority, dw, tsuid, rspHandler);
		            assoc.waitForDimseRSP();

	            } catch (Exception e) {
	                throw new DcmException(e);
	            }
	        }
		}
	}
	
    private void addTransferCapability(String cuid, String tsuid) {
        Set<String> ts = (HashSet<String>) as2ts.get(cuid);
        if (ts == null) {
            ts = new HashSet<String>();
            ts.add(UID.ImplicitVRLittleEndian);
            as2ts.put(cuid, ts);
        }
        ts.add(tsuid);
    }

    private void configureTransferCapability() {
        TransferCapability[] tc = null;
        int i = 0;
        
        if( (as2ts != null) && (as2ts.size() > 0) ){
        	
            tc = new TransferCapability[as2ts.size()];
                        
            for(Map.Entry<String, Set<String>> e : as2ts.entrySet()){
            	
                String cuid = (String) e.getKey();
                Set<String> ts = (Set<String>) e.getValue();
                tc[i++] = new TransferCapability(cuid, 
                        (String[]) ts.toArray(new String[ts.size()]),
                        TransferCapability.SCU);
            }
            
            localAE.setTransferCapability(tc);       	
        }
    }
    
	private String selectTransferSyntax(String[] available, String tsuid) {
		String result = null;
		
		if( (available != null) && ( (tsuid != null) && (tsuid.length() > 0) ) ){
			if (tsuid.equals(UID.ImplicitVRLittleEndian))
				result = selectTransferSyntax(available, IVLE_TS);
			if (tsuid.equals(UID.ExplicitVRLittleEndian))
				result = selectTransferSyntax(available, EVLE_TS);
			if (tsuid.equals(UID.ExplicitVRBigEndian))
				result = selectTransferSyntax(available, EVBE_TS);
		}else
			result = tsuid;
		
		return result;
	}
	
	private String selectTransferSyntax(String[] available, String[] tsuids) {
		String result = null;
		
		if( ( (available != null) && (available.length > 0) ) && 
			( (tsuids != null) && (tsuids.length > 0) ) ) {

			for(String tsuid : tsuids){
				for(String avail : available){
					result = (avail.equals(tsuid))?avail:null;
					break;
				}
				if(result != null) break;
			}
		}
		
		return result;
	}
}