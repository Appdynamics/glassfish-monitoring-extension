package com.appdynamics.monitors.glassfish.config;

public enum GlassFishMBeanKeyPropertyEnum {
    TYPE("type"),
    NAME("name");
	
	private String name;
	
	private GlassFishMBeanKeyPropertyEnum(String name) {
		this.name = name;
	}
	
	public String toString(){
        return name;
    }
}
