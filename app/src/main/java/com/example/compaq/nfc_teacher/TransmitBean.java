package com.example.compaq.nfc_teacher;

/**
 * 用于蓝牙传输的数据类
 */

import java.io.Serializable;

public class TransmitBean implements Serializable{

	private static final long serialVersionUID = 1L;
	private String msg = "";
	private String filename = "";
	private String filepath = "";
	private String uppercent = "";
	private String tspeed = "";
	private boolean showflag ;
	private byte[] file ;

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getMsg() {
		return this.msg;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public byte[] getFile() {
		return file;
	}

	public void setFile(byte[] file) {
		this.file = file;
	}

	public String getFilepath() {
		return filepath;
	}

	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}

	public String getUppercent() {
		return uppercent;
	}

	public void setUppercent(String uppercent) {
		this.uppercent = uppercent;
	}

	public String getTspeed() {
		return tspeed;
	}

	public void setTspeed(String tspeed) {
		this.tspeed = tspeed;
	}

	public boolean isShowflag() {
		return showflag;
	}

	public void setShowflag(boolean showflag) {
		this.showflag = showflag;
	}


}
