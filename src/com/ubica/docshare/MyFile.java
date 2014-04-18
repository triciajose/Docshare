package com.ubica.docshare;

import java.sql.Timestamp;
import java.util.Date;

public class MyFile {
	
//	private variables
	String folder;
	String name;
	int version;
	
	public MyFile() {
		
	}
	
	public MyFile(String folder, String name, int version) {
		this.folder = folder;
		this.name = name;
		this.version = version;
	}

		
	public String getFolder() {
		return this.folder;
	}
	
	public String getName() {
		return this.name;
	}
	
	public int getVersion() {
		return this.version;
	}
	
	
	public void setFolder(String folder) {
		this.folder = folder;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	
	public void setVersion(int version) {
		this.version = version;
	}
}
