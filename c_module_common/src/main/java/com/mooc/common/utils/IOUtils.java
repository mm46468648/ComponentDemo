package com.mooc.common.utils;

import java.io.Closeable;
import java.io.IOException;

/**
 * 
 * @author liyusheng
 *
 */
public class IOUtils {
	/** 关闭流 */
	public static boolean close(Closeable io) {
		if (io != null) {
			try {
				io.close();
			} catch (IOException e) {
			}
		}
		return true;
	}
}
