/*******************************************************************************
 *
 * Copyright (c)北京慕华信息科技有限公司
 *
 * DeviceInfo2Json
 *	获取设备信息并转换成json
 * log.engine.DeviceInfo2Json.java
 * @author: yusheng.li
 * @since:  2015年1月4日
 * @version: 1.0.0
 *
 * @changeLogs:
 *     1.0.0: First created this class.
 *
 ******************************************************************************/
package com.mooc.common.utils;
import android.text.TextUtils;


import com.mooc.common.global.AppGlobals;
import com.mooc.common.utils.sharepreference.SpDefaultUtils;

import org.json.JSONException;
import java.util.HashMap;
import java.util.Map;


/**
 * 获取设备信息并转换成json
 * @author liyusheng
 *
 */
public final class DeviceInfo2Json {
	private static final String TAG = "DeviceInfo2Json";
	public static final String SP_CURRENT_TIME ="currentTime";
	public static final String SP_UUID ="uuid";

	private static HashMap<String,Object> getDeviceInfoMap(boolean allBean){
		
		HashMap<String,Object> devicesInfoMap = new HashMap<String, Object>();
		DeviceInfoBean mDeviceInfoBean  = new DeviceInfoBean();
//		String strChannel = SystemUtils.getChannel();
//		put(devicesInfoMap,"channel", strChannel);
//		mDeviceInfoBean.setStrChannel(strChannel);
		String strAppVersion = PackageUtils.getVersionName();
		put(devicesInfoMap,"appVersion", strAppVersion);
		mDeviceInfoBean.setStrAppVersion(strAppVersion);
		String strCPUCores = SystemUtils.getCpuCount()+"";
		put(devicesInfoMap, "cpuCores", strCPUCores);
		mDeviceInfoBean.setStrCPUCores(strCPUCores);
		String strBrand = SystemUtils.getBrand();
		put(devicesInfoMap, "brand", strBrand);
		mDeviceInfoBean.setStrBrand(strBrand);
		String strCPUFrequency = SystemUtils.getMaxCpuFreq();
		put(devicesInfoMap, "cpuFrequency", strCPUFrequency);
		mDeviceInfoBean.setStrCPUFrequency(strCPUFrequency);
		String strCPUModel = SystemUtils.getCPUModel()+"";
		put(devicesInfoMap, "cpuModel", strCPUModel);
		mDeviceInfoBean.setStrCPUModel(strCPUModel);
		String strCPUName = SystemUtils.getCpuName();
		put(devicesInfoMap, "cpu", strCPUName);
		mDeviceInfoBean.setStrCPUName(strCPUName);
		String strIMEI = SystemUtils.getIMEI();
		put(devicesInfoMap, "imei", strIMEI);
		mDeviceInfoBean.setStrIMEI(strIMEI);
		String strIMSI = SystemUtils.getIMSI();
		put(devicesInfoMap, "imsi", strIMSI);
		mDeviceInfoBean.setStrIMSI(strIMSI);
		String strKernelVersion = SystemUtils.getKernelVersion();
		put(devicesInfoMap, "kernelVersion", strKernelVersion);
		mDeviceInfoBean.setStrKernelVersion(strKernelVersion);
		String strModel = SystemUtils.getDeviceModel();
		put(devicesInfoMap, "model", strModel);
		mDeviceInfoBean.setStrModel(strModel);
		
		String widthDpi = ScreenUtil.getWidthDpi(AppGlobals.INSTANCE.getApplication())+"";
		String heightDpi = ScreenUtil.getHeightDpi(AppGlobals.INSTANCE.getApplication())+"";
		String strScreenDPI ;
		if(TextUtils.isEmpty(widthDpi)&&TextUtils.isEmpty(heightDpi)){
			strScreenDPI = "";
		}
		 strScreenDPI = widthDpi+"*"+heightDpi;
		
		put(devicesInfoMap, "dpi", strScreenDPI);
		mDeviceInfoBean.setStrScreenDPI(strScreenDPI);
		int strScreenHeight = -1;
		int strScreenWidth = -1;
		try {
			strScreenHeight = ScreenUtil.getScreenHeight(AppGlobals.INSTANCE.getApplication());
			strScreenWidth = ScreenUtil.getScreenWidth(AppGlobals.INSTANCE.getApplication());
		} catch (Exception e) {

		}
		mDeviceInfoBean.setStrScreenHeight(String.valueOf(strScreenHeight));
		mDeviceInfoBean.setStrScreenWidth(String.valueOf(strScreenWidth));
		String strResolution = strScreenWidth+"*"+strScreenHeight;
		put(devicesInfoMap, "resolution", strResolution);
		
		String strSystemOSType = "android";
		put(devicesInfoMap, "os", strSystemOSType);
		mDeviceInfoBean.setStrSystemOSType(strSystemOSType);
		String strSystemOSVersion = SystemUtils.getOSVersion();
		put(devicesInfoMap, "osVersion", strSystemOSVersion);
		mDeviceInfoBean.setStrSystemOSVersion(strSystemOSVersion);
		String strSystemRam = SystemUtils.getThresholdMemory()/1024+"";
		put(devicesInfoMap, "ram", strSystemRam);
		mDeviceInfoBean.setStrSystemRam(strSystemRam);
		String strSystemRom = SystemUtils.getTotalMemory()/1024 +"";//返回kb
		put(devicesInfoMap, "rom", strSystemRom);
		mDeviceInfoBean.setStrSystemRom(strSystemRom);
		
//		String strEvent = XTCoreConfig.getEvent();
//		put(devicesInfoMap, "spam", strEvent); //2.2修改event为spam.
		
		
		HashMap<String,Object> deviceJsonMap = new HashMap<String, Object>();
		deviceJsonMap.put("deviceInfo", devicesInfoMap);
		long currentTime = 0;
//    	 currentTime = PreferenceUtils.getPrefLong(AppGlobals.INSTANCE.getApplication(), PreferenceUtils.SP_CURRENT_TIME, 0);
    	 currentTime = SpDefaultUtils.getInstance().getLong(SP_CURRENT_TIME, 0);
    	 if(currentTime == 0){
    		 currentTime = System.currentTimeMillis();
//    		 PreferenceUtils.setPrefLong(AppGlobals.INSTANCE.getApplication(), PreferenceUtils.SP_CURRENT_TIME, currentTime);
    		 SpDefaultUtils.getInstance().putLong(SP_CURRENT_TIME, currentTime);
    	 }
		String strTimeStamp = String.valueOf(currentTime);
		put(deviceJsonMap, "timestamp", strTimeStamp);
		if (allBean) {
			put(deviceJsonMap, "macAddress", SystemUtils.getMacAddress()); //macAddress不影响uuid生成，但是会随deviceInfo上传.
			mDeviceInfoBean.setStrMacAddress(SystemUtils.getMacAddress());
		}
		put(deviceJsonMap, "appName", "xuetangX-Android");
		mDeviceInfoBean.setStrAppName("xuetangX-Android");
		put(deviceJsonMap, "owner", "xuetangX");
		mDeviceInfoBean.setStrOwner("xuetangX");
		mDeviceInfoBean.setStrTimeStamp(strTimeStamp);
		String strUUIDTemp = mDeviceInfoBean.toString() +currentTime;
		strUUIDTemp = Md5Util.getMD5Str(strUUIDTemp);
//		String strUUID  = PreferenceUtils.getPrefString(AppGlobals.INSTANCE.getApplication(),PreferenceUtils.SP_UUID, "");
		String strUUID  = SpDefaultUtils.getInstance().getString(SP_UUID, "");
		if(TextUtils.isEmpty(strUUID) || !strUUIDTemp.equals(strUUID)){
			currentTime = System.currentTimeMillis();
			strUUID = mDeviceInfoBean.toString() + currentTime;
			strUUID = Md5Util.getMD5Str(strUUID);
//			PreferenceUtils.setPrefLong(AppGlobals.INSTANCE.getApplication(), PreferenceUtils.SP_CURRENT_TIME, currentTime);
			SpDefaultUtils.getInstance().putLong(SP_CURRENT_TIME, currentTime);
//			PreferenceUtils.setPrefString(AppGlobals.INSTANCE.getApplication(),PreferenceUtils.SP_UUID, strUUID);
			SpDefaultUtils.getInstance().putString(SP_UUID, strUUID);

			//todo将uuid设置到内存
		}
		put(deviceJsonMap, "uuid", strUUID);
		mDeviceInfoBean.setStrUUID(strUUID);
		return deviceJsonMap;
		
	}
	
	private static void put(Map<String, Object> deviceJsonMap, String key,String value) {
		if(!TextUtils.isEmpty(value)){
			deviceJsonMap.put(key, value);
		}
	}
	public static String getDeviceJson(boolean isAllBean) throws JSONException{
		HashMap<String,Object> map = getDeviceInfoMap(isAllBean);
		return JsonUtils.fromHashMap(map);
		
	}
   
}
