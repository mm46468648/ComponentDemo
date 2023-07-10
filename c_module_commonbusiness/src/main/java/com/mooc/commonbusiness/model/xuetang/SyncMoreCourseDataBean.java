package com.mooc.commonbusiness.model.xuetang;

import java.io.Serializable;

public class SyncMoreCourseDataBean implements Serializable {

	private static final long serialVersionUID = -9015204701291349706L;
	private String strCourseID;
	private int intChapterPosition;
	private String strChapterID;
	private  int intSequentialPosition;
	private String strSequentialID;
	private String strLastModify;
	private String strLastEnter;

	public String getStrLastEnter() {
		return strLastEnter;
	}

	public void setStrLastEnter(String strLastEnter) {
		this.strLastEnter = strLastEnter;
	}

	public String getLongLastModify() {
		return strLastModify;
	}

	public void setLongLastModify(String strLastModify) {
		this.strLastModify = strLastModify;
	}

	public String getStrCourseID() {
		return strCourseID;
	}
	public void setStrCourseID(String strCourseID) {
		this.strCourseID = strCourseID;
	}
	public int getIntChapterPosition() {
		return intChapterPosition;
	}
	public void setIntChapterPosition(int intChapterPosition) {
		this.intChapterPosition = intChapterPosition;
	}
	public String getStrChapterID() {
		return strChapterID;
	}
	public void setStrChapterID(String strChapterID) {
		this.strChapterID = strChapterID;
	}
	public int getIntSequentialPosition() {
		return intSequentialPosition;
	}
	public void setIntSequentialPosition(int intSequentialPosition) {
		this.intSequentialPosition = intSequentialPosition;
	}
	public String getStrSequentialID() {
		return strSequentialID;
	}
	public void setStrSequentialID(String strSequentialID) {
		this.strSequentialID = strSequentialID;
	}
	@Override
	public String toString() {
		return "SyncMoreCourseDataBean [strCourseID=" + strCourseID + ", strChapterPosition=" + intChapterPosition
				+ ", strChapterID=" + strChapterID + ", intSequentialPosition=" + intSequentialPosition
				+ ", strSequentialID=" + strSequentialID + "]";
	}
	
}
