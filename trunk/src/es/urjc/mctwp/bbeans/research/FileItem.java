package es.urjc.mctwp.bbeans.research;

import java.io.Serializable;
import java.util.Date;

import es.urjc.mctwp.modelo.File;

public class FileItem implements Serializable {
	private static final long serialVersionUID = 5065667334719261658L;

	private Integer code = null;
	private String name = null;
	private String comment = null;
	private Date stamp = null;
	private String contentType = null;
	private Long size = null;
	
	public FileItem(){
	}
	
	public FileItem(File file){
		if(file != null){
			this.code = file.getCode();
			this.name = file.getName();
			this.comment = file.getComment();
			this.stamp = file.getStamp();
			this.contentType = file.getContentType();
			this.size = file.getSize();
		}
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public Integer getCode() {
		return code;
	}

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
}
