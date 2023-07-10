package com.mooc.commonbusiness.scheme;

import android.text.TextUtils;

import java.io.Serializable;
import java.util.HashMap;

public class SchemasData implements Serializable{
	private String strSchemas;
	private String strRawData;
	private HashMap<String, String> mapData;
	public SchemasData() {
		strSchemas = "";
		mapData = new HashMap<String, String>();
	}
	public SchemasData(String content) {
		strSchemas = "";
		mapData = new HashMap<String, String>();
		init(content);
		strRawData = content;
	}
	/**
	 * 解析协议和内容.
	 * @param content
	 */
	public void init(String content) {
		if (!TextUtils.isEmpty(content)) {
			String[] data = content.split("://", 2);
			if (data.length >= 1) {
				strSchemas = data[0].toLowerCase(); //协议已经获取
				if (data.length > 1) { //开始获取键值对
					initMap(data[1]);
				}
			}
		}
	}
	/**
	 * 解析内容.
	 * @param strMap
	 */
	private void initMap(String strMap) {
			String[] maps = strMap.split("&");
			for (String map: maps) {
				if (!TextUtils.isEmpty(map)) {
					String[] keyValue = map.split("=");
					if (keyValue.length >= 1) {
						mapData.put(keyValue[0].toLowerCase(), keyValue.length == 1? "":valueDecode(keyValue[1]));
					}
				}
			}
		}

	private String valueDecode(String string) {
		try {
			//return URLDecoder.decode(string,"UTF-8");
			return android.net.Uri.decode(string); //修复url里带 "+"被转换成空格的bug
			//return  string;
			//TODO:XXXXXX
		}catch(Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	/**
	 * 返回解析到的协议
	 * @return
	 */
	public String getStrSchemas() {
		return strSchemas;
	}
	public void setStrSchemas(String strSchemas) {
		this.strSchemas = strSchemas;
	}
	/**
	 * 获取对应的值.
	 * @param strTitle
	 * @return
	 */
	public String getStrValue(String strTitle) {
		String value = mapData.get(strTitle) == null ? "": mapData.get(strTitle);
		return value;
	}
	public void setKeyValue(String strTitle, String strValue) {
		mapData.put(strTitle, strValue);
	}
	public HashMap<String, String> getKeyValue() {
		return mapData;
	}
	/**
	 * 获取原始数据.
	 * @return
	 */
	public String getStrRawData() {
		return strRawData;
	}

}
