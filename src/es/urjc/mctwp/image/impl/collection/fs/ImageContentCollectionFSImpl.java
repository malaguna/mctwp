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

package es.urjc.mctwp.image.impl.collection.fs;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import es.urjc.mctwp.image.collection.ImageContentCollection;
import es.urjc.mctwp.image.exception.ImageCollectionException;

/**
 * File system broker to manage dicom fila storage
 * 
 * @author Miguel Ángel Laguna Lobato
 * 
 */
public class ImageContentCollectionFSImpl implements ImageContentCollection {
	private FileFilter icff;
	private String fsBaseDirPath;
	private String fsTmpCollDir;
	private File basedir;
	private File tmpcoll;
	private Logger logger;

	public ImageContentCollectionFSImpl() {
		logger = Logger.getLogger(this.getClass());
		icff = new ImageContentFileFilter();
	}

	/**
	 * Sets and checks the base path for storing images
	 * 
	 * @param fspath
	 */
	public void setFsBaseDirPath(String fspath) {

		if ((fspath != null) && (fspath.length() > 0)) {
			this.fsBaseDirPath = fspath;

			basedir = new File(fsBaseDirPath);
			if (!basedir.exists()) {
				String error = fspath
						+ " doesn't exist. Can't manage image storage.";
				logger.error(error);
				throw new RuntimeException(error);
			}
		} else {
			String error = "given path is null or empty";
			logger.error(error);
			throw new RuntimeException(error);
		}

		// Is not sure the order to set basedir and tempcoll
		if (fsTmpCollDir != null)
			createTempCollection();
	}

	/**
	 * Sets and checks the base path for storing temporal images
	 * 
	 * @param fspath
	 */
	public void setFsTmpCollDir(String name) {
		if ((name != null) && (name.length() > 0)) {
			this.fsTmpCollDir = name;
		} else
			throw new RuntimeException("Invalid temporary collection name");

		// Is not sure the order to set basedir and tempcoll
		if (fsBaseDirPath != null)
			createTempCollection();
	}

	/**
	 * Create a subdirectory for store dicom files
	 * 
	 * @param name
	 */
	public void createCollection(String name) throws ImageCollectionException {

		if ((name != null) && (name.length() > 0)) {
			File temp = new File(FilenameUtils.concat(
					basedir.getAbsolutePath(), name));

			if (temp.exists()) {
				String error = "Collection [" + name + "] already exists";
				logger.error(error);
				throw new ImageCollectionException(error);
			} else {
				try {
					temp.mkdir();
				} catch (Exception e) {
					logger.error(e.getMessage());
					throw new ImageCollectionException(e);
				}
			}
		} else {
			String error = "Can't create collection, given name is null or empty";
			logger.error(error);
			throw new ImageCollectionException(error);
		}
	}

	/**
	 * Don't delete anything
	 */
	public void deleteCollection(String name) throws ImageCollectionException {
		// do nothing
	}

	/**
	 * Retrieve a file from any collection
	 * 
	 * @param collection
	 * @param id
	 *            of image
	 * @param temporal
	 *            boolean that indicates wheter collection is temporal or not
	 * @return dcm file
	 */
	@Override
	public File loadContent(String collection, String idImage, boolean temporal)
			throws ImageCollectionException {
		File result = null;

		if ((collection != null) && (idImage != null)
				&& (collection.length() > 0) && (idImage.length() > 0)) {

			File temp = getProperCollection(collection, temporal);

			if (!temp.exists()) {
				String error = (temporal ? "Temp collection" : "Collection")
						+ " [" + collection + "] doesn't exists";
				throw new ImageCollectionException(error);
			} else {
				result = findContent(temp, idImage);
				if (result == null) {
					String error = "File [" + idImage
							+ "] is wrong or doesn't exists";
					throw new ImageCollectionException(error);
				}
			}
		}

		return result;
	}

	/**
	 * Retrieve all contents in the collection, except thumbnails files. See
	 * file filter implementation.
	 */
	@Override
	public List<File> loadAllContents(String collection, boolean temporal)
			throws ImageCollectionException {
		List<File> result = null;

		if ((collection != null) && (collection.length() > 0)) {
			File temp = getProperCollection(collection, temporal);

			if (!temp.exists()) {
				String error = (temporal ? "Temp collection" : "Collection")
						+ " [" + collection + "] doesn't exists";
				logger.error(error);
				throw new ImageCollectionException(error);
			} else {
				try {
					result = Arrays.asList(temp.listFiles(icff));
				} catch (Exception e) {
					logger.error(e.getMessage());
					throw new ImageCollectionException(e);
				}
			}
		}

		return result;
	}

	/**
	 * Store any content into a collection. If the collection does not exist an
	 * it is not temporal an exception is thrown. If collection does not exist,
	 * but it is temporal, then collection is created.
	 * 
	 * @param collection
	 * @param dcm
	 */
	@Override
	public void storeContent(String collection, File content, boolean temporal)
			throws ImageCollectionException {

		if ((collection != null) && (content != null)
				&& (collection.length() > 0)) {

			File temp = getProperCollection(collection, temporal);

			if (!temp.exists()) {
				if (temporal) {
					temp.mkdir();
				} else {
					String error = "Collection [" + collection
							+ "] doesn't exists";
					throw new ImageCollectionException(error);
				}
			}

			try {
				if (content.isFile())
					storeFile(temp, content);
				else if (content.isDirectory())
					storeDirectory(temp, content);
				else
					throw new ImageCollectionException("Can not manage file : "
							+ content.getName());
			} catch (Exception e) {
				logger.error(e.getMessage());
				throw new ImageCollectionException(e);
			}
		}
	}

	/**
	 * delete any content
	 */
	@Override
	public void deleteContent(String collection, String idContent,
			boolean temporal) throws ImageCollectionException {

		if ((collection != null) && (idContent != null)
				&& (collection.length() > 0) && (idContent.length() > 0)) {

			File temp = getProperCollection(collection, temporal);
			File file = null;

			if (!temp.exists()) {
				String error = (temporal ? "Temp collection" : "Collection")
						+ " [" + collection + "] doesn't exists";
				throw new ImageCollectionException(error);
			} else {
				file = findContent(temp, idContent);
				if (file == null) {
					String error = "Content [" + idContent + "] doesn't exists";
					throw new ImageCollectionException(error);
				}

				try {
					if (file.isFile())
						file.delete();
					else if (file.isDirectory())
						FileUtils.deleteDirectory(file);
				} catch (Exception e) {
					logger.error(e.getMessage());
					throw new ImageCollectionException(e);
				}
			}
		}
	}

	/**
	 * Once both path for temporal and persistent images are specified, it is
	 * created temporal collection
	 */
	private void createTempCollection() {
		tmpcoll = new File(FilenameUtils.concat(fsBaseDirPath, fsTmpCollDir));
		if (!tmpcoll.exists())
			tmpcoll.mkdir();
	}

	/**
	 * It resolves properly collection file directory.
	 * 
	 * @param colName
	 * @param temporal
	 * @return
	 */
	private File getProperCollection(String colName, boolean temporal) {
		String basePath = temporal ? tmpcoll.getAbsolutePath() : basedir
				.getAbsolutePath();
		return new File(FilenameUtils.concat(basePath, colName));
	}

	/**
	 * It stores properly a single file.
	 * 
	 * @param collection
	 * @param file
	 * @throws ImageCollectionException
	 * @throws IOException
	 */
	private void storeFile(File collection, File file)
			throws ImageCollectionException, IOException {
		File dest = new File(FilenameUtils.concat(collection.getAbsolutePath(),
				file.getName()));
		FileUtils.copyFile(file, dest);
	}

	/**
	 * It stores properly a directory, in a recursive way
	 * 
	 * @param collection
	 * @param directory
	 * @throws ImageCollectionException
	 * @throws IOException
	 */
	private void storeDirectory(File collection, File directory)
			throws ImageCollectionException, IOException {
		File dest = new File(FilenameUtils.concat(collection.getAbsolutePath(),
				directory.getName()));
		if (!dest.exists())
			dest.mkdir();

		if (directory.listFiles() != null) {
			for (File file : directory.listFiles()) {
				if (file.isFile())
					storeFile(dest, file);
				else if (file.isDirectory())
					storeDirectory(dest, file);
				else
					throw new ImageCollectionException("Can not manage file : "
							+ file.getName());
			}
		}
	}

	/**
	 * Finds any content into a collection
	 * 
	 * @param collection
	 * @param imageId
	 * @return file content
	 */
	private File findContent(File collection, String imageId) {
		File result = null;

		File[] auxLst = collection.listFiles(icff);
		if (auxLst != null) {
			for (File file : auxLst) {
				String id = StringUtils.substringBeforeLast(file.getName(), FilenameUtils.EXTENSION_SEPARATOR_STR);

				if (id.equals(imageId)) {
					result = file;
					break;
				}
			}
		}

		return result;
	}
}
