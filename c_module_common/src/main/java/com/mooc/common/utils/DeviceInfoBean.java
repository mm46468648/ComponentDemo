/*******************************************************************************
 *
 * Copyright (c)北京慕华信息科技有限公司
 *
 * DeviceInfoBean
 * 
 * log.engine.DeviceInfoBean.java
 * @author: yusheng.li
 * @since:  2015年1月4日
 * @version: 1.0.0
 *
 * @changeLogs:
 *     1.0.0: First created this class.
 *
 ******************************************************************************/

package com.mooc.common.utils;

import java.io.Serializable;

/**
 * 系统空间信息参数javaBean
 * @author liyusheng
 *
 */
public class DeviceInfoBean implements Serializable {

	private static final long serialVersionUID = 1L;
	/**
	 * 设备空间信息唯一id
	 */
	private String strUUID;
	/**
	 * 生成这段数据的时间，如果不小心上传了多次，以timestamp大的为准
	 */
	private String strTimeStamp;
	/**
	 * 操作系统类型(android/ios/wp)
	 */
	private String strSystemOSType;
	/**
	 * 设备品牌
	 */
	private String strBrand;
	/**
	 * 设备型号
	 */
	private String strModel;
	/**
	 * 当前设备的imei
	 */
	private String strIMEI;
	/**
	 * 当前设备的imsi
	 */
	private String strIMSI;
	/**
	 * 当前操作系统版本号
	 */
	private String strSystemOSVersion;
	/**
	 * 当前操作系统内核版本信息
	 */
	private String strKernelVersion;
	/**
	 * 手机屏幕宽度
	 */
	private String strScreenWidth;
	/**
	 * 手机屏幕高度
	 */
	private String strScreenHeight;
	/**
	 * 手机屏幕分辨率
	 */
	private String strScreenDPI;
	/**
	 * 手机cpu名称
	 */
	private String strCPUName;
	/**
	 * 手机cpu频率
	 */
	private String strCPUFrequency;
	/**
	 * 手机cpu型号
	 */
	private String strCPUModel;
	/**
	 * 手机cpu内核数
	 */
	private String strCPUCores;
	/**
	 * 手机运行内存
	 */
	private String strSystemRam;
	/**
	 * 手机机身内存
	 */
	private String strSystemRom;
	/**
	 * 当前应用版本号
	 */
	private String strAppVersion;
	/**
	 * 当前应用渠道号
	 */
	private String strChannel;
	/**
	 * app拥有者，spoc或者mooc，加入uuid生成.
	 */
	private String strOwner;
	/**
	 * appName，区分同一个平台的多个app,加入uuid生成.
	 */
	private String strAppName;
	/**
	 * 网卡地址，不加入uuid生成.
	 */
	private String strMacAddress;
	
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public String getStrUUID() {
		return strUUID;
	}
	public String getStrTimeStamp() {
		return strTimeStamp;
	}
	public String getStrSystemOSType() {
		return strSystemOSType;
	}
	public String getStrBrand() {
		return strBrand;
	}
	public String getStrModel() {
		return strModel;
	}
	public String getStrIMEI() {
		return strIMEI;
	}
	public String getStrIMSI() {
		return strIMSI;
	}
	public String getStrSystemOSVersion() {
		return strSystemOSVersion;
	}
	public String getStrKernelVersion() {
		return strKernelVersion;
	}
	public String getStrScreenWidth() {
		return strScreenWidth;
	}
	public String getStrScreenHeight() {
		return strScreenHeight;
	}
	public String getStrScreenDPI() {
		return strScreenDPI;
	}
	public String getStrCPUName() {
		return strCPUName;
	}
	public String getStrCPUFrequency() {
		return strCPUFrequency;
	}
	public String getStrCPUModel() {
		return strCPUModel;
	}
	public String getStrCPUCores() {
		return strCPUCores;
	}
	public String getStrSystemRam() {
		return strSystemRam;
	}
	public String getStrSystemRom() {
		return strSystemRom;
	}
	public String getStrAppVersion() {
		return strAppVersion;
	}
	public String getStrChannel() {
		return strChannel;
	}
	public void setStrUUID(String strUUID) {
		this.strUUID = strUUID;
	}
	public void setStrTimeStamp(String strTimeStamp) {
		this.strTimeStamp = strTimeStamp;
	}
	public void setStrSystemOSType(String strSystemOSType) {
		this.strSystemOSType = strSystemOSType;
	}
	public void setStrBrand(String strBrand) {
		this.strBrand = strBrand;
	}
	public void setStrModel(String strModel) {
		this.strModel = strModel;
	}
	public void setStrIMEI(String strIMEI) {
		this.strIMEI = strIMEI;
	}
	public void setStrIMSI(String strIMSI) {
		this.strIMSI = strIMSI;
	}
	public void setStrSystemOSVersion(String strSystemOSVersion) {
		this.strSystemOSVersion = strSystemOSVersion;
	}
	public void setStrKernelVersion(String strKernelVersion) {
		this.strKernelVersion = strKernelVersion;
	}
	public void setStrScreenWidth(String strScreenWidth) {
		this.strScreenWidth = strScreenWidth;
	}
	public void setStrScreenHeight(String strScreenHeight) {
		this.strScreenHeight = strScreenHeight;
	}
	public void setStrScreenDPI(String strScreenDPI) {
		this.strScreenDPI = strScreenDPI;
	}
	public void setStrCPUName(String strCPUName) {
		this.strCPUName = strCPUName;
	}
	public void setStrCPUFrequency(String strCPUFrequency) {
		this.strCPUFrequency = strCPUFrequency;
	}
	public void setStrCPUModel(String strCPUModel) {
		this.strCPUModel = strCPUModel;
	}
	public void setStrCPUCores(String strCPUCores) {
		this.strCPUCores = strCPUCores;
	}
	public void setStrSystemRam(String strSystemRam) {
		this.strSystemRam = strSystemRam;
	}
	public void setStrSystemRom(String strSystemRom) {
		this.strSystemRom = strSystemRom;
	}
	public void setStrAppVersion(String strAppVersion) {
		this.strAppVersion = strAppVersion;
	}
	public void setStrChannel(String strChannel) {
		this.strChannel = strChannel;
	}
	
	public String getStrOwner() {
		return strOwner;
	}
	public void setStrOwner(String strOwner) {
		this.strOwner = strOwner;
	}
	public String getStrAppName() {
		return strAppName;
	}
	public void setStrAppName(String strAppName) {
		this.strAppName = strAppName;
	}
	public String getStrMacAddress() {
		return strMacAddress;
	}
	public void setStrMacAddress(String strMacAddress) {
		this.strMacAddress = strMacAddress;
	}
	@Override
	public String toString() {
		return "SystemParamsBean [ strSystemOSType=" + strSystemOSType
				+ ", strBrand=" + strBrand + ", strModel=" + strModel
				+ ", strIMEI=" + strIMEI + ", strIMSI=" + strIMSI
				+ ", strSystemOSVersion=" + strSystemOSVersion
				+ ", strKernelVersion=" + strKernelVersion
				+ ", strScreenWidth=" + strScreenWidth + ", strScreenHeight="
				+ strScreenHeight + ", strScreenDPI=" + strScreenDPI
				+ ", strCPUName=" + strCPUName + ", strCPUFrequency="
				+ strCPUFrequency + ", strCPUModel=" + strCPUModel
				+ ", strCPUCores=" + strCPUCores + ", strSystemRam="
				+ strSystemRam + ", strSystemRom=" + strSystemRom
				+ ", strAppVersion=" + strAppVersion + ", strChannel="
				+ strChannel + ", strAppName=" + strAppName + ", strOwner=" + strOwner + "]";
	}
	
	 
}
