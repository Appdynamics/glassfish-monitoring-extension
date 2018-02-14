/*
 *   Copyright 2018. AppDynamics LLC and its affiliates.
 *   All Rights Reserved.
 *   This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 *   The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */
package com.appdynamics.monitors.glassfish.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MBeanData {

    private String domainName;
    private Map<String, List<String>> types = new HashMap<String, List<String>>();

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public Map<String, List<String>> getTypes() {
        return types;
    }

    public void setTypes(Map<String, List<String>> types) {
        this.types = types;
    }
}
