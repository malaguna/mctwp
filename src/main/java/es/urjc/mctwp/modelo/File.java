package es.urjc.mctwp.modelo;

import java.sql.Blob;
import java.util.Date;

public class File extends DomainObject {
	private static final long serialVersionUID = 5065667104719261658L;
	private String name = null;
	private String comment = null;
	private Date stamp = null;
	private String contentType = null;
	private Long size = null;
	private Blob file = null;
	private ImageContainer source = null;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Date getStamp() {
		return stamp;
	}

	public void setStamp(Date stamp) {
		this.stamp = stamp;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
	}

	public Blob getFile() {
		return file;
	}

	public void setFile(Blob file) {
		this.file = file;
	}

	public void setSource(ImageContainer source) {
		this.source = source;
	}

	public ImageContainer getSource() {
		return source;
	}
}
