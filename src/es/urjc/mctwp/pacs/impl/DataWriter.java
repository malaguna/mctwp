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

import org.dcm4che2.io.DicomInputStream;
import org.dcm4che2.io.DicomOutputStream;
import org.dcm4che2.io.TranscoderInputHandler;
import org.dcm4che2.net.PDVOutputStream;

/**
 * From DcmSnd - dcm4che2
 * 
 */
public class DataWriter  implements org.dcm4che2.net.DataWriter {

	private FileInfo info;
	private int transcoderBufferSize = 1024;
	
	public void setTranscoderBufferSize(int tbs){
		transcoderBufferSize = tbs;
	}
	
	public DataWriter(FileInfo info) {
		this.info = info;
	}
	
	public void writeTo(PDVOutputStream out, String tsuid) throws IOException {
		if (tsuid.equals(info.tsuid)) {
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
}
