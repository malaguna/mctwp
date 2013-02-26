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

package es.urjc.mctwp.image.impl.dicom;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.dcm4che2.data.BasicDicomObject;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.dcm4che2.io.DicomInputStream;
import org.dcm4che2.io.SAXWriter;
import org.w3c.dom.Node;

import es.urjc.mctwp.image.ImageUtils;
import es.urjc.mctwp.image.exception.ImageException;
import es.urjc.mctwp.image.management.ImagePluginDefaultImpl;
import es.urjc.mctwp.image.objects.DicomSCHeaderAttrs;
import es.urjc.mctwp.image.objects.Image;
import es.urjc.mctwp.image.objects.SeriesImage;
import es.urjc.mctwp.image.objects.PatientInfo;
import es.urjc.mctwp.image.objects.SingleImage;

public class DicomImagePlugin extends ImagePluginDefaultImpl {
	private String medconPath = "/usr/bin/medcon";
	private String options = "-c png -e 1";
	private URL xslt = null;

	public String getMedconPath() {
		return medconPath;
	}

	public void setMedconPath(String medconPath) {
		this.medconPath = medconPath;
	}

	public void setXslt(URL xslt) {
		this.xslt = xslt;
	}

	@Override
	public Image createImage(File file) throws ImageException {
		SeriesImageDicomImpl res = null;
		SingleImageDicomImpl aux = null;
		String seriesUid = null;

		seriesUid = getSeriesUID(file);

		// If there is a seriesUid, Image is DICOM.
		if (seriesUid != null) {
			String name = ImageUtils.getFileName(file);

			// Create single image
			aux = new SingleImageDicomImpl();
			aux.setContent(file);
			aux.setId(name);

			// Create series image, first get prop series id
			String seriesId = getMapper().getSeriesPropId(seriesUid);
			res = new SeriesImageDicomImpl();
			res.setId(seriesId);
			res.addImage(aux);
		}

		return res;
	}

	@Override
	public PatientInfo getPatientInfo(Image image) throws ImageException {
		PatientInfo result = null;

		DicomObject dcmObj = getDicomObject(image);
		if(dcmObj != null){
			result = new PatientInfo();
			result.setCode(dcmObj.getString(Tag.PatientID));
			result.setName(dcmObj.getString(Tag.PatientName));
			result.setStudy(dcmObj.getString(Tag.StudyID));
		}

		return result;
	}

	@Override
	public Image loadImage(File file) throws ImageException {
		Image result = null;

		if (file != null) {
			if (file.isDirectory())
				result = loadSeriesImage(file);
			else if (file.isFile())
				result = loadSingleImage(file);
		}

		return result;
	}

	@Override
	public List<File> toDicom(Image image, File outputDir)
			throws ImageException {
		List<File> result = null;
		String error = null;

		if (image == null)
			error = "Null dicom image";
		else if (!outputDir.isDirectory())
			error = "Destination [" + outputDir.getAbsolutePath()
					+ "] is not a directory";
		else if (image instanceof SingleImageDicomImpl)
			result = toDicom((SingleImageDicomImpl) image, outputDir);
		else if (image instanceof SeriesImageDicomImpl)
			result = toDicom((SeriesImageDicomImpl) image, outputDir);
		else
			error = "Expecting DICOM image file format";

		if (error != null)
			throw new ImageException(error);

		return result;
	}

	@Override
	public File toPng(Image image) throws ImageException {
		String error = null;
		File result = null;

		if (image == null)
			error = "Null dicom image";
		if (image instanceof SingleImageDicomImpl)
			result = toPng((SingleImageDicomImpl) image);
		else if (image instanceof SeriesImageDicomImpl)
			result = toPng((SeriesImageDicomImpl) image);
		else
			error = "Expecting DICOM image file format";

		if (error != null)
			throw new ImageException(error);

		return result;
	}

	@Override
	public Node toXml(Image image) throws ImageException {
		String error = null;
		Node result = null;

		if (image == null)
			error = "Null dicom image";
		else if (image instanceof SingleImageDicomImpl)
			result = toXml((SingleImageDicomImpl) image);
		else if (image instanceof SeriesImageDicomImpl)
			result = toXml((SeriesImageDicomImpl) image);
		else
			error = "Expecting DICOM image file format";

		if (error != null)
			throw new ImageException(error);

		return result;
	}

	@Override
	public boolean getSupportNoExtension() {
		return true;
	}

	@Override
	public String[] getSupportedExtensions() {
		return new String[] {SingleImageDicomImpl.DCM_EXT};
	}
	
	public DicomSCHeaderAttrs getDicomHeader(Image image) throws ImageException{
		DicomSCHeaderAttrs result = null;

		DicomObject dcmObj = getDicomObject(image);
		if(dcmObj != null){
			result = new DicomSCHeaderAttrs();
			result.setAccesionNumber(dcmObj.getString(Tag.AccessionNumber));
			result.setImageDate(dcmObj.getDate(Tag.Date));
			//result.setImageNumber(dcmObj.getString(Tag.num));
			result.setInstitutionName(dcmObj.getString(Tag.InstitutionName));
			result.setPatientBirth(dcmObj.getDate(Tag.PatientBirthDate));
			result.setPatientId(dcmObj.getString(Tag.PatientID));
			result.setPatientName(dcmObj.getString(Tag.PatientName));
			result.setPatientSex(dcmObj.getString(Tag.PatientSex));
			result.setSeriesDate(dcmObj.getString(Tag.SeriesDate));
			result.setSeriesDescription(dcmObj.getString(Tag.SeriesDescription));
			result.setSeriesInstanceUID(dcmObj.getString(Tag.SeriesInstanceUID));
			result.setSeriesNumber(dcmObj.getString(Tag.SeriesNumber));
			result.setStudyDate(dcmObj.getDate(Tag.StudyDate));
			result.setStudyDescription(dcmObj.getString(Tag.StudyDescription));
			result.setStudyInstanceUID(dcmObj.getString(Tag.StudyInstanceUID));
			result.setStudyTime(dcmObj.getDate(Tag.StudyTime));
		}
		
		return result;
	}
	
	/**
	 * Returns dicom object. It parses image an reports any exception
	 * 
	 * @param image
	 * @return
	 * @throws ImageException
	 */
	private DicomObject getDicomObject(Image image) throws ImageException{
		SingleImageDicomImpl sidi = null;
		DicomInputStream dis = null;
		DicomObject result = null;
		String error = null;

		if (image == null)
			error = "Null dicom image";
		else if (image instanceof SeriesImageDicomImpl && !((SeriesImageDicomImpl) image).getImages().isEmpty())
			sidi = (SingleImageDicomImpl) ((SeriesImageDicomImpl) image)
					.getImages().get(0);
		else if (image instanceof SingleImageDicomImpl)
			sidi = (SingleImageDicomImpl) image;

		// Retrieve header dicom from file information
		try {
			if(sidi != null){
				dis = new DicomInputStream(sidi.getContent());
				result = new BasicDicomObject();
				dis.readDicomObject(result, -1);
			}else
				error = "There is no images into serie";
		} catch (IOException ioe) {
			result = null;
			error = "Can't read dicom file";
		}

		if (error != null)
			throw new ImageException(error);

		return result;
	}

	/**
	 * Load a singleImage from a file. It checks it is a Dicom file
	 * 
	 * @param file
	 * @return
	 * @throws ImageException
	 */
	private SingleImage loadSingleImage(File file) throws ImageException {
		SingleImageDicomImpl result = null;
		String seriesUid = null;

		seriesUid = getSeriesUID(file);

		if (seriesUid != null) {
			String name = ImageUtils.getFileName(file);

			// Create image
			result = new SingleImageDicomImpl();
			result.setContent(file);
			result.setId(name);
		}

		return result;
	}

	/**
	 * Loads a Dicom serie from a directory containing dicom files from the same
	 * serie
	 * 
	 * @param file
	 * @return
	 * @throws ImageException
	 */
	private SeriesImage loadSeriesImage(File file) throws ImageException {
		SeriesImage result = new SeriesImageDicomImpl();

		result.setId(ImageUtils.getFileName(file));

		// Add every single image directory has
		for (File f : Arrays.asList(file.listFiles())) {
			SingleImage singleImage = loadSingleImage(f);
			if (singleImage != null) {

				//TODO Is it neccesary to check wheter image has same seriesUid?
				result.addImage(singleImage);
			}
		}

		// If no image has been added, it is wrong format
		if (result.getImages() == null)
			result = null;

		return result;
	}

	/**
	 * Check if it is a nice dicom file and returns seriesUid
	 * 
	 * @return series uid if image is dicom, null other case.
	 */
	private String getSeriesUID(File file) {
		DicomInputStream dis = null;
		DicomObject dcmObj = null;
		String result = null;

		try {
			dis = new DicomInputStream(file);
			dcmObj = new BasicDicomObject();
			dis.readDicomObject(dcmObj, -1);
			result = dcmObj.getString(Tag.SeriesInstanceUID);
			dis.close();
			dcmObj.clear();
		} catch (IOException ioe) {
			result = null;
		}

		return result;
	}

	/**
	 * Obtains a png file from the one singleImage of the series
	 * 
	 * @param series
	 * @return
	 * @throws ImageException
	 */
	private File toPng(SeriesImageDicomImpl series) throws ImageException {
		File result = null;

		if ((series.getImages() != null) && (!series.getImages().isEmpty())) {
			int i = series.getImages().size();

			if (i > 3)
				result = toPng(series.getImages().get(i / 2));
			else
				result = toPng(series.getImages().get(0));
		}

		return result;
	}

	/**
	 * Obtains a png file from a singleImage
	 * 
	 * @param single
	 * @return
	 * @throws ImageException
	 */
	private File toPng(SingleImageDicomImpl single) throws ImageException {
		File result = null;
		File source = null;

		try {

			// Prepare output file
			source = single.getContent();
			String base = source.getParent();
			String pre = ImageUtils.getFileName(source);
			result = new File(FilenameUtils.concat(base, pre + ".png"));

			// Check if thumbnails exists. Like a cache of thumbnails
			if ((!result.exists()) || (!result.isFile())
					|| (result.length() == 0)) {

				// Delete a possible erroneus thumbanil file
				if (result.exists())
					result.delete();

				// Execute transformation and scale
				String cmd = medconPath + " " + options + " -f "
						+ source.getAbsolutePath() + " -o "
						+ result.getAbsolutePath();

				exec(cmd, true);
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

	/**
	 * Obtains an XML node representation from the first singleImage of the
	 * series
	 * 
	 * @param series
	 * @return
	 * @throws ImageException
	 */
	private Node toXml(SeriesImageDicomImpl series) throws ImageException {
		Node result = null;

		if ((series.getImages() != null) && (!series.getImages().isEmpty()))
			result = toXml(series.getImages().get(0));

		return result;
	}

	/**
	 * Obtains XML node representation of a singleImage
	 * 
	 * @param single
	 * @return
	 * @throws ImageException
	 */
	private Node toXml(SingleImage single) throws ImageException {
		DicomInputStream dis = null;
		TransformerHandler th = null;
		DOMResult result = null;
		File source = null;

		try {

			// Prepare XSLT transformer
			result = new DOMResult();
			th = getTransformerHandler();
			th.getTransformer().setOutputProperty(OutputKeys.INDENT, "yes");
			th.setResult(result);

			// Get writer and set image pixel data excluded from XML
			final SAXWriter writer = new SAXWriter(th, null);
			writer.setExclude(new int[] { Tag.PixelData });

			// Parse and convert to XML
			source = single.getContent();
			dis = new DicomInputStream(source);
			dis.setHandler(writer);
			// dis.readDicomObject(new BasicDicomObject(), -1);
			dis.readDicomObject();

		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new ImageException(e);
		} finally {
			try {
				dis.close();
			} catch (IOException ioe) {
				logger.error(ioe.getMessage());
			}
		}

		return result.getNode();
	}

	/**
	 * Obtains a DICOM representation for each single image into serie.
	 * 
	 * @param series
	 * @return
	 * @throws ImageException
	 */
	private List<File> toDicom(SeriesImageDicomImpl series, File outputDir)
			throws ImageException {
		List<File> result = null;
		List<File> aux = null;

		if ((series.getImages() != null) && (!series.getImages().isEmpty())) {
			result = new ArrayList<File>();

			for (Image i : series.getImages()) {
				aux = toDicom(i, outputDir);

				if ((aux != null) && (!aux.isEmpty()))
					result.addAll(aux);
			}
		}

		return result.isEmpty() ? null : result;
	}

	/**
	 * Obtains a DICOM representation for single image
	 * 
	 * @param single
	 * @return
	 * @throws ImageException
	 */
	private List<File> toDicom(SingleImage single, File outputDir)
			throws ImageException {
		List<File> result = new ArrayList<File>();
		File content = single.getContent();

		try {

			String ext = StringUtils.substringAfterLast(content.getName(), FilenameUtils.EXTENSION_SEPARATOR_STR);
			if(ext == null) ext = SingleImageDicomImpl.DCM_EXT;
			File dest = new File(FilenameUtils.concat(outputDir
					.getAbsolutePath(), single.getId() + FilenameUtils.EXTENSION_SEPARATOR_STR + ext));
			FileUtils.copyFile(content, dest);
			result.add(dest);

		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new ImageException(e);
		}

		return result;
	}

	/**
	 * Get the XSL transformer to convert dicom into XML. If there is an xslt
	 * specified, it is used.
	 * 
	 * @return TransformerHandler
	 * @throws TransformerConfigurationException
	 * @throws IOException
	 */
	private TransformerHandler getTransformerHandler() throws Exception {
		SAXTransformerFactory stf = null;
		StreamSource sc = null;

		stf = (SAXTransformerFactory) TransformerFactory.newInstance();
		if (xslt != null)
			sc = new StreamSource(xslt.openStream(), xslt.toExternalForm());

		return (sc == null) ? stf.newTransformerHandler() : stf
				.newTransformerHandler(sc);
	}
}
