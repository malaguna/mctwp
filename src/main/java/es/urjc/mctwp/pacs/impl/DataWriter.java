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

//This file has been created using source code of dcm4che2 project created 
//by Gunter Zeilinger. Project dcm4che2

package es.urjc.mctwp.pacs.impl;

import java.io.FileInputStream;
import java.io.IOException;

import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.dcm4che2.io.DicomInputStream;
import org.dcm4che2.io.DicomOutputStream;
import org.dcm4che2.io.StopTagInputHandler;
import org.dcm4che2.io.TranscoderInputHandler;
import org.dcm4che2.net.PDVOutputStream;

import es.urjc.mctwp.image.objects.DicomSCHeaderAttrs;

/**
 * From DcmSnd - dcm4che2
 * 
 */
public class DataWriter implements org.dcm4che2.net.DataWriter {
	DicomSCHeaderAttrs header = null;

	private FileInfo info;
	private int transcoderBufferSize = 1024;
	
	public void setTranscoderBufferSize(int tbs){
		transcoderBufferSize = tbs;
	}
	
	public DicomSCHeaderAttrs getHeader() {
		return header;
	}

	public void setHeader(DicomSCHeaderAttrs header) {
		this.header = header;
	}

	public DataWriter(FileInfo info) {
		this.info = info;
	}
	
	public void writeTo(PDVOutputStream out, String tsuid) throws IOException {
        if (header != null) {
            DicomObject attrs;
            DicomInputStream dis = new DicomInputStream(info.f);
            try {
                dis.setHandler(new StopTagInputHandler(Tag.PixelData));
                attrs = dis.readDicomObject();

                //Override dicom header attributes
                attrs.putString(Tag.AccessionNumber, null, header.getAccesionNumber());
                attrs.putDate(Tag.Date, null, header.getImageDate());
                //attrs.putString(Tag., null, header.getImageNumber());
                attrs.putString(Tag.InstitutionName, null, header.getInstitutionName());
                attrs.putDate(Tag.PatientBirthDate, null, header.getPatientBirth());
                attrs.putString(Tag.PatientID, null, header.getPatientId());
                attrs.putString(Tag.PatientName, null, header.getPatientName());
                attrs.putString(Tag.PatientSex, null, header.getPatientSex());
                attrs.putString(Tag.SeriesDate, null, header.getSeriesDate());
                attrs.putString(Tag.SeriesDescription, null, header.getSeriesDescription());
                attrs.putString(Tag.SeriesInstanceUID, null, header.getSeriesInstanceUID());
                attrs.putString(Tag.SeriesNumber, null, header.getSeriesNumber());
                attrs.putString(Tag.SOPInstanceUID, null, header.getSopInstanceUID());
                attrs.putDate(Tag.StudyDate, null, header.getStudyDate());
                attrs.putString(Tag.StudyDescription, null, header.getStudyDescription());
                attrs.putString(Tag.StudyInstanceUID, null, header.getStudyInstanceUID());
                attrs.putDate(Tag.StudyTime, null, header.getStudyTime());
                
                DicomOutputStream dos = new DicomOutputStream(out);
                dos.writeDataset(attrs, tsuid);
                if (dis.tag() >= Tag.PixelData) {
                    copyPixelData(dis, dos, out);
                    // copy attrs after PixelData
                    attrs = dis.readDicomObject();
                    dos.writeDataset(attrs, tsuid);
                }
            } finally {
                dis.close();
            }
        }else if (tsuid.equals(info.tsuid)) {
			FileInputStream fis = new FileInputStream(info.f);
			try {
				long skip = info.fmiEndPos;
				while (skip > 0)
					skip -= fis.skip(skip);
				out.copyFrom(fis);
			} finally {
				fis.close();
			}
		} else {
			DicomInputStream dis = new DicomInputStream(info.f);
			try {
				DicomOutputStream dos = new DicomOutputStream(out);
				dos.setTransferSyntax(tsuid);
				TranscoderInputHandler h = new TranscoderInputHandler(dos, transcoderBufferSize);
				dis.setHandler(h);
				dis.readDicomObject();
			} finally {
				dis.close();
			}
		}
	}

    private void copyPixelData(DicomInputStream dis, DicomOutputStream dos,
            PDVOutputStream out) throws IOException {
        int vallen = dis.valueLength();
        dos.writeHeader(dis.tag(), dis.vr(), vallen);
        if (vallen == -1) {
            int tag;
            do {
                tag = dis.readHeader();
                vallen = dis.valueLength();
                dos.writeHeader(tag, null, vallen);
                out.copyFrom(dis, vallen);
            } while (tag == Tag.Item);
        } else {
            out.copyFrom(dis, vallen);
        }
    }
}
