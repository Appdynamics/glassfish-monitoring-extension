/**
 * Copyright 2014 AppDynamics, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
