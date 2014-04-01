package com.ubica.docshare;

import java.sql.Timestamp;
import java.util.Date;

public class MyFile {
	
//	private variables
	String folder;
	String name;
	Timestamp dateModified;
	
	public MyFile() {
		
	}
	
	public MyFile(String folder, String name, Timestamp dateModified) {
		this.folder = folder;
		this.name = name;
		this.dateModified = dateModified;
	}

		
	public String getFolder() {
		return this.folder;
	}
	
	public String getName() {
		return this.name;
	}
	
	public Timestamp getDate() {
		return this.dateModified;
	}
	
	public void setFolder(String folder) {
		this.folder = folder;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	
	public void setDate(Date date) {
		this.dateModified = (Timestamp) date;
	}
}
