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

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;
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

import es.urjc.mctwp.image.exception.ImageException;
import es.urjc.mctwp.image.management.ImagePluginDefaultImpl;
import es.urjc.mctwp.image.objects.Image;
import es.urjc.mctwp.image.objects.MultipleImage;
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
		String seriesUid = null;
		SingleImage aux = null;

		seriesUid = getSeriesUID(file);

		// If there is a seriesUid, Image is DICOM.
		if (seriesUid != null) {
			String name = getFileName(file);

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
		DicomInputStream dis = null;
		DicomObject dcmObj = null;
		PatientInfo result = null;
		String error = null;
		SingleImageDicomImpl sidi = null;

		if (image == null)
			error = "Null dicom image";
		else if (image instanceof SeriesImageDicomImpl && !((SeriesImageDicomImpl) image).getImages().isEmpty())
			sidi = (SingleImageDicomImpl) ((SeriesImageDicomImpl) image)
					.getImages().get(0);
		else if (image instanceof SingleImageDicomImpl)
			sidi = (SingleImageDicomImpl) image;

		// Retrive demographic dicom file information
		try {
			if(sidi != null){
				dis = new DicomInputStream(sidi.getContent());
				dcmObj = new BasicDicomObject();
				dis.readDicomObject(dcmObj, -1);
	
				result = new PatientInfo();
				result.setCode(dcmObj.getString(Tag.PatientID));
				result.setName(dcmObj.getString(Tag.PatientName));
				result.setStudy(dcmObj.getString(Tag.StudyID));
			}else
				error = "There is no images into serie";
		} catch (IOException ioe) {
			result = null;
			error = "Can't read patient and study dicom header information";
		}

		if (error != null)
			throw new ImageException(error);

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

	/**
	 * Load a singleImage from a file. It checks it is a Dicom file
	 * 
	 * @param file
	 * @return
	 * @throws ImageException
	 */
	private SingleImage loadSingleImage(File file) throws ImageException {
		SingleImage result = null;
		String seriesUid = null;

		seriesUid = getSeriesUID(file);

		if (seriesUid != null) {
			String name = getFileName(file);

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
	private MultipleImage loadSeriesImage(File file) throws ImageException {
		MultipleImage result = new SeriesImageDicomImpl();

		result.setId(file.getName());

		// Add every single image directory has
		for (File f : Arrays.asList(file.listFiles())) {
			SingleImage singleImage = loadSingleImage(f);
			if (singleImage != null) {

				// Is it neccesary to check wheter image has same seriesUid?
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
			String pre = StringUtils.substringBeforeLast(source.getName(), ".");
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

			String ext = StringUtils.substringAfterLast(content.getName(), ".");
			File dest = new File(FilenameUtils.concat(outputDir
					.getAbsolutePath(), single.getId() + "." + ext));
			FileUtils.copyFile(content, dest);
			result.add(dest);

		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new ImageException(e);
		}

		return result;
	}

	/**
	 * Invoke command line xmedcon program to transfor images
	 * 
	 * @param command
	 *            to exec into the server
	 * @param wait
	 *            time to waoit
	 * @return true if all is ok
	 */
	private static void exec(String command, boolean wait) throws Exception {
		Process p = Runtime.getRuntime().exec(command);
		if (wait)
			p.waitFor();
	}

	/**
	 * Scale a non null png image to 128 x 128 png file
	 * 
	 * @param file
	 *            original file to scale
	 * @throws Exception
	 */
	private void scaleThumbnail(File file) throws Exception {
		BufferedImage pngImage = null;
		BufferedImage thumbImage = null;
		Graphics2D graphics2D = null;

		try {
			if (file != null) {

				pngImage = ImageIO.read(file);

				// Set dimensions
				int thumbWidth = 128;
				int thumbHeight = 128;
				int imageWidth = pngImage.getWidth(null);
				int imageHeight = pngImage.getHeight(null);

				// Obtain ratios
				double thumbRatio = (double) thumbWidth / (double) thumbHeight;
				double imageRatio = (double) imageWidth / (double) imageHeight;

				if (thumbRatio < imageRatio) {
					thumbHeight = (int) (thumbWidth / imageRatio);
				} else {
					thumbWidth = (int) (thumbHeight * imageRatio);
				}

				// Draw original image to thumbnail and scale it
				thumbImage = new BufferedImage(thumbWidth, thumbHeight,
						BufferedImage.TYPE_INT_RGB);
				graphics2D = thumbImage.createGraphics();
				graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
						RenderingHints.VALUE_INTERPOLATION_BILINEAR);
				graphics2D.drawImage(pngImage, 0, 0, thumbWidth, thumbHeight,
						null);

				// Save thumbnail image to OUTFILE
				ImageIO.write(thumbImage, "png", file);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new ImageException(e);
		}
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
