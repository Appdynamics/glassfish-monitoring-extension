/*
 *   Copyright 2018. AppDynamics LLC and its affiliates.
 *   All Rights Reserved.
 *   This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 *   The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */

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
