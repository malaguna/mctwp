package es.urjc.mctwp.image;

import java.io.File;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;

public class ImageUtils {
	/**
	 * Try to get the file extension. If there is no extension it returns null,
	 * that is because some ImagePlugins can process images without extension.
	 * 
	 * @param file
	 * @return file extension
	 */
	public static String getFileExtension(File file) {
		String ext = StringUtils.substringAfterLast(file.getName(), FilenameUtils.EXTENSION_SEPARATOR_STR);
		return normalizeExtension(ext);
	}

	/**
	 * Try to get the file name.
	 * 
	 * @param file
	 * @return file name
	 */
	public static String getFileName(File file) {
		return StringUtils.substringBeforeLast(file.getName(), FilenameUtils.EXTENSION_SEPARATOR_STR);
	}

	/**
	 * 
	 * @param extension
	 * @return
	 */
	public static String normalizeExtension(String extension) {
		String result = "";

		if (extension != null)
			result = StringUtils.lowerCase(extension);

		return result;
	}
}
