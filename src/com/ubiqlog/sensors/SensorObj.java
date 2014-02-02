package com.ubiqlog.sensors;

/**
 * 
 * @author rezar
 *
 */
public class SensorObj {
	private String sensorName;
	private String className;
	private String[] configData;
	private String annotationCalss;

	public String getAnnotationCalss() {
		return annotationCalss;
	}

	public void setAnnotationCalss(String annotationCalss) {
		this.annotationCalss = annotationCalss;
	}

	public String getSensorName() {
		return sensorName;
	}

	public void setSensorName(String sensorName) {
		this.sensorName = sensorName;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String[] getConfigData() {
		return configData;
	}

	public void setConfigData(String[] configData) {
		this.configData = configData;
	}


}
