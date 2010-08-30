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

package es.urjc.mctwp.image.impl.analyze;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.w3c.dom.Node;

import es.urjc.mctwp.image.exception.ImageException;
import es.urjc.mctwp.image.management.ImagePluginDefaultImpl;
import es.urjc.mctwp.image.objects.Image;
import es.urjc.mctwp.image.objects.PatientInfo;

/**
 * 
 * @author Miguel Ángel Laguna Lobato
 *
 */
public class AnalyzeImagePlugin extends ImagePluginDefaultImpl {
	private final static String PNG_FORMAT_OPT = "png";
	private final static String DCM_FORMAT_OPT = "dicom";
	
	@Override
	public Image createImage(File file) throws ImageException {
		Image res = null;

		//Check extension and image. It can not be null
		String ext = getFileExtension(file);
		
		if(ext != null && !ext.isEmpty()){
			String name = getFileName(file);
			boolean isSingle  = ext.equals(SingleAnalyzeImageImpl.NIFIT_EXT);
			boolean isComplex = ext.equals(ComplexAnalyzeImageImpl.ANALYZE_HDR_EXT);
			
			try{
				if(isSingle || isComplex){	
					if(checkFormat(file)){	
						if(isSingle){
							if(isNifti(file)){
								SingleAnalyzeImageImpl aux = new SingleAnalyzeImageImpl();
								aux.setContent(file);
								aux.setId(getUniqueImageId());
								res = aux;
							}else{
								String error = "File : [" + file.getAbsolutePath() + "] is not NIFTI compliant";
								logger.error(error);
								throw new ImageException(error);
							}
						}else{
							File data = new File(name + "." + ComplexAnalyzeImageImpl.ANALYZE_IMG_EXT);
							if(data.exists()){
								ComplexAnalyzeImageImpl aux = new ComplexAnalyzeImageImpl();
								aux.setHeader(file);
								aux.setData(data);
								aux.setId(getUniqueImageId());
								res = aux;
							}else{
								String error = "There is no image data for file : [" + file.getName() + "]";
								logger.error(error);
								throw new ImageException(error);
							}
						}
					}
				}
			} catch (IOException e) {
				logger.error(e.getLocalizedMessage());
			}				
		}
				
		return res;
	}
	
	@Override
	public Image loadImage(File file) throws ImageException{
		Image result = null;
		
		if(file != null){
			try {
				if(checkFormat(file)){
					String name = getFileName(file);
					
					if(file.isDirectory()){
						File header = null;
						File data = null;
						
						//Get header and data files into directory
						File[] list = file.listFiles();
						if(list != null)
							for(File auxFile : list){
								String ext = getFileExtension(auxFile);
								if(ComplexAnalyzeImageImpl.ANALYZE_HDR_EXT.equals(ext))
									header = auxFile;
								if(ComplexAnalyzeImageImpl.ANALYZE_IMG_EXT.equals(ext))
									data = auxFile;
							}
						
						if(header != null && data != null){
							if(checkFormat(header)){
								ComplexAnalyzeImageImpl aux = new ComplexAnalyzeImageImpl();
								aux.setHeader(header);
								aux.setData(data);
								aux.setId(name);
								result = aux;
							}
						}	
					}else if (file.isFile()){
						if(checkFormat(file) && isNifti(file)){
							SingleAnalyzeImageImpl aux = new SingleAnalyzeImageImpl();
							aux.setContent(file);
							aux.setId(name);
							result = aux;
						}
					}
				}
			} catch (IOException e) {
				logger.error(e.getLocalizedMessage());
			}
		}
		
		return result;
	}

	@Override
	public PatientInfo getPatientInfo(Image image) throws ImageException {
		return null;
	}

	@Override
	public List<File> toDicom(Image image, File outputDir) throws ImageException {
		List<File> result = new ArrayList<File>();
		result.add(convert(image, DCM_FORMAT_OPT));
		return result;
	}

	@Override
	public File toPng(Image image) throws ImageException {
		return convert(image, PNG_FORMAT_OPT);
	}

	private File convert(Image image, String format) throws ImageException {
		String medconPath = "/usr/bin/medcon";
		String options = null;
		File result = null;
		File source = null;

		try {
			
			if(image instanceof SingleAnalyzeImageImpl){
				source = ((SingleAnalyzeImageImpl)image).getContent();
				options = "-c " + format + " -e 1";
			}else if (image instanceof ComplexAnalyzeImageImpl){
				source = ((ComplexAnalyzeImageImpl)image).getHeader();
				options = "-c " + format + " -e 1 -fv";
			}
			
			String base = source.getParent();
			String pre = getFileName(source);
			result = new File(FilenameUtils.concat(base, pre + ".png"));

			// Check if thumbnails exists. Like a cache of thumbnails
			if ((!result.exists()) || (!result.isFile())
					|| (result.length() == 0)) {

				// Delete a possible erroneous thumbnail file
				if (result.exists())
					result.delete();

				// Execute transformation and scale
				String cmd = medconPath + " " + options + " -f "
						+ source.getAbsolutePath() + " -o "
						+ result.getAbsolutePath();

				exec(cmd, true);
				if(PNG_FORMAT_OPT.equals(format))
					scaleThumbnail(result);
			}
		} catch (Exception e) {
			if (result.exists())
				result.delete();
			logger.error(e.getMessage());
			throw new ImageException(e);
		}

		return result;
	}
	
	@Override
	public Node toXml(Image image) throws ImageException {
		return null;
	}

	@Override
	public boolean getSupportNoExtension() {
		return false;
	}

	@Override
	public String[] getSupportedExtensions() {
		return new String[] {
				SingleAnalyzeImageImpl.NIFIT_EXT,
				SingleAnalyzeImageImpl.ANALYZE_TYPE,
				ComplexAnalyzeImageImpl.ANALYZE_HDR_EXT};
	}
	
	private boolean checkFormat(File file) throws IOException{
		boolean result = false;
		
		if(file != null){
			DataInputStream stream = new DataInputStream(new FileInputStream(file));
			int check = stream.readInt();
			stream.close();
			
			result = (check == 0x15c) || (swap(check) == 0x15c); 
		}
		
		return result;
	}
	
	private boolean isNifti(File file) throws IOException{
		boolean result = false;
		
		if(file != null){
			DataInputStream stream = new DataInputStream(new FileInputStream(file));
			stream.skip(344);
			byte check1 = stream.readByte();
			byte check2 = stream.readByte();
			byte check3 = stream.readByte();
			byte check4 = stream.readByte();
			stream.close();
			
			result = (check1 == 0x6e) &&
				((check2 == 0x69) || (check2 == 0x2b)) &&
				(check3 == 0x31) &&
				(check4 == 0x00); 
		}
		
		return result;		
	}
	
	private static int swap(int value) {
		int b1 = (value >> 0) & 0xff;
		int b2 = (value >> 8) & 0xff;
		int b3 = (value >> 16) & 0xff;
		int b4 = (value >> 24) & 0xff;

		return b1 << 24 | b2 << 16 | b3 << 8 | b4 << 0;
	}	
}
