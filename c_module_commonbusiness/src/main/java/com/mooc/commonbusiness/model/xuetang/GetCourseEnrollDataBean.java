package com.mooc.commonbusiness.model.xuetang;

import java.io.Serializable;

/**
 * 加课模式.
 * @author gaoyan
 * version 2.9.0 add isCredentialApplySuccess liyusheng
 *
 */
public class GetCourseEnrollDataBean implements Serializable {

	/**
	 * Version ID.
	 */
	private static final long serialVersionUID = -6857208693186371111L;
	private boolean isVerified;
	private boolean isEnrolled;
	private String strEnrollMode;
	private boolean isCredentialApplySuccess;//是否成功申请过证书

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public boolean isCredentialApplySuccess() {
		return isCredentialApplySuccess;
	}

	public void setCredentialApplySuccess(boolean credentialApplySuccess) {
		isCredentialApplySuccess = credentialApplySuccess;
	}

	public boolean isVerified() {
		return isVerified;
	}
	public void setVerified(boolean isVerified) {
		this.isVerified = isVerified;
	}
	public boolean isEnrolled() {
		return isEnrolled;
	}
	public void setEnrolled(boolean isEnrolled) {
		this.isEnrolled = isEnrolled;
	}
	public String getStrEnrollMode() {
		return strEnrollMode;
	}
	public void setStrEnrollMode(String strEnrollMode) {
		this.strEnrollMode = strEnrollMode;
	}

	@Override
	public String toString() {
		return "GetCourseEnrollDataBean{" +
				"isVerified=" + isVerified +
				", isEnrolled=" + isEnrolled +
				", strEnrollMode='" + strEnrollMode + '\'' +
				", isCredentialApplySuccess=" + isCredentialApplySuccess +
				'}';
	}
}
