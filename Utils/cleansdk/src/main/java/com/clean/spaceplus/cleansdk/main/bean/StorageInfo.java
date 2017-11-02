package com.clean.spaceplus.cleansdk.main.bean;

public class StorageInfo {
	
	public long allSize;
	public long freeSize;

	public StorageInfo() {
		allSize = 0;
		freeSize = 0;
	}
	
	public void reset(){
		allSize = 0;
		freeSize = 0;
	}

	@Override
	public String toString() {
		return "StorageInfo{" +
				"allSize=" + allSize +
				", freeSize=" + freeSize +
				'}';
	}
}
