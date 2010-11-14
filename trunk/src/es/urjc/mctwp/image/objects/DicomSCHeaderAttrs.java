package es.urjc.mctwp.image.objects;

import java.io.Serializable;
import java.util.Date;

import org.dcm4che2.data.UID;

public class DicomSCHeaderAttrs implements Serializable {
	private static final long serialVersionUID = -7249649647899472200L;
	private String sopInstanceUID = UID.SecondaryCaptureImageStorage;
	private Date studyDate = null;
	private String seriesDate = null;
	private Date imageDate = null;
	private Date studyTime = null;
	private String accesionNumber = null;
	private String institutionName = null;
	private String studyDescription = null;
	private String seriesDescription = null;
	private String patientName = null;
	private String patientId = null;
	private Date patientBirth = null;
	private String patientSex = null;
	private String studyInstanceUID = null;
	private String seriesInstanceUID = null;
	private String seriesNumber = null;
	private String imageNumber = null;

	public String getSopInstanceUID() {
		return sopInstanceUID;
	}

	public void setSopInstanceUID(String sopInstanceUID) {
		this.sopInstanceUID = sopInstanceUID;
	}

	public Date getStudyDate() {
		return studyDate;
	}

	public void setStudyDate(Date studyDate) {
		this.studyDate = studyDate;
	}

	public String getSeriesDate() {
		return seriesDate;
	}

	public void setSeriesDate(String seriesDate) {
		this.seriesDate = seriesDate;
	}

	public Date getImageDate() {
		return imageDate;
	}

	public void setImageDate(Date imageDate) {
		this.imageDate = imageDate;
	}

	public Date getStudyTime() {
		return studyTime;
	}

	public void setStudyTime(Date studyTime) {
		this.studyTime = studyTime;
	}

	public String getAccesionNumber() {
		return accesionNumber;
	}

	public void setAccesionNumber(String accesionNumber) {
		this.accesionNumber = accesionNumber;
	}

	public String getInstitutionName() {
		return institutionName;
	}

	public void setInstitutionName(String institutionName) {
		this.institutionName = institutionName;
	}

	public String getStudyDescription() {
		return studyDescription;
	}

	public void setStudyDescription(String studyDescription) {
		this.studyDescription = studyDescription;
	}

	public String getSeriesDescription() {
		return seriesDescription;
	}

	public void setSeriesDescription(String seriesDescription) {
		this.seriesDescription = seriesDescription;
	}

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public Date getPatientBirth() {
		return patientBirth;
	}

	public void setPatientBirth(Date patientBirth) {
		this.patientBirth = patientBirth;
	}

	public String getPatientSex() {
		return patientSex;
	}

	public void setPatientSex(String patientSex) {
		this.patientSex = patientSex;
	}

	public String getStudyInstanceUID() {
		return studyInstanceUID;
	}

	public void setStudyInstanceUID(String studyInstanceUID) {
		this.studyInstanceUID = studyInstanceUID;
	}

	public String getSeriesInstanceUID() {
		return seriesInstanceUID;
	}

	public void setSeriesInstanceUID(String seriesInstanceUID) {
		this.seriesInstanceUID = seriesInstanceUID;
	}

	public String getSeriesNumber() {
		return seriesNumber;
	}

	public void setSeriesNumber(String seriesNumbre) {
		this.seriesNumber = seriesNumbre;
	}

	public String getImageNumber() {
		return imageNumber;
	}

	public void setImageNumber(String imageNumber) {
		this.imageNumber = imageNumber;
	}
}
