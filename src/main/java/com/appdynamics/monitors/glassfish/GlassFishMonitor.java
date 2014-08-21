package com.appdynamics.monitors.glassfish;

import com.appdynamics.extensions.PathResolver;
import com.appdynamics.extensions.jmx.JMXConnectionConfig;
import com.appdynamics.extensions.jmx.JMXConnectionUtil;
import com.appdynamics.monitors.glassfish.config.ConfigUtil;
import com.appdynamics.monitors.glassfish.config.Configuration;
import com.appdynamics.monitors.glassfish.config.GlassFishMBeanKeyPropertyEnum;
import com.appdynamics.monitors.glassfish.config.GlassFishMonitorConstants;
import com.appdynamics.monitors.glassfish.config.MBeanData;
import com.appdynamics.monitors.glassfish.config.Server;
import com.google.common.base.Strings;
import com.singularity.ee.agent.systemagent.api.AManagedMonitor;
import com.singularity.ee.agent.systemagent.api.MetricWriter;
import com.singularity.ee.agent.systemagent.api.TaskExecutionContext;
import com.singularity.ee.agent.systemagent.api.TaskOutput;
import com.singularity.ee.agent.systemagent.api.exception.TaskExecutionException;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import org.apache.log4j.Logger;

public class GlassFishMonitor extends AManagedMonitor {
    private static final Logger logger = Logger.getLogger(GlassFishMonitor.class);

    public static final String METRICS_SEPARATOR = "|";
    private static final String CONFIG_ARG = "config-file";
    private static final String FILE_NAME = "monitors/GlassFishMonitor/config.yml";

    private static final ConfigUtil<Configuration> configUtil = new ConfigUtil<Configuration>();

    public GlassFishMonitor() {
        String details = GlassFishMonitor.class.getPackage().getImplementationTitle();
        String msg = "Using Monitor Version [" + details + "]";
        logger.info(msg);
        System.out.println(msg);
    }

    public TaskOutput execute(Map<String, String> taskArgs, TaskExecutionContext taskExecutionContext) throws TaskExecutionException {
        if (taskArgs != null) {
            logger.info("Starting the GlassFish Monitoring task.");
            String configFilename = getConfigFilename(taskArgs.get(CONFIG_ARG));
            try {
                Configuration config = configUtil.readConfig(configFilename, Configuration.class);
                Map<String, Number> metrics = populateStats(config);
                printStats(config, metrics);
                logger.info("Completed the GlassFish Monitoring Task successfully");
                return new TaskOutput("GlassFish Monitor executed successfully");
            } catch (FileNotFoundException e) {
                logger.error("Config File not found: " + configFilename, e);
            } catch (Exception e) {
                logger.error("Metrics Collection Failed: ", e);
            }
        }
        throw new TaskExecutionException("GlassFish Monitor completed with failures");
    }

    private Map<String, Number> populateStats(Configuration config) throws Exception {
        JMXConnectionUtil jmxConnector = null;
        Map<String, Number> metrics = new HashMap<String, Number>();
        Server server = config.getServer();
        MBeanData mbeanData = config.getMbeans();
        try {
            jmxConnector = new JMXConnectionUtil(new JMXConnectionConfig(server.getHost(), server.getPort(), server.getUsername(),
                    server.getPassword()));
            if (jmxConnector.connect() != null) {
                Set<ObjectInstance> allMbeans = jmxConnector.getAllMBeans();
                if (allMbeans != null) {
                    metrics = extractMetrics(jmxConnector, mbeanData, allMbeans);
                    metrics.put(GlassFishMonitorConstants.METRICS_COLLECTED, GlassFishMonitorConstants.SUCCESS_VALUE);
                }
            }
        } catch (Exception e) {
            logger.error("Error JMX-ing into GlassFish Server ", e);
            metrics.put(GlassFishMonitorConstants.METRICS_COLLECTED, GlassFishMonitorConstants.ERROR_VALUE);
        } finally {
            if(jmxConnector != null) {
                jmxConnector.close();
            }
        }
        return metrics;
    }

    private Map<String, Number> extractMetrics(JMXConnectionUtil jmxConnector, MBeanData mbeanData, Set<ObjectInstance> allMbeans) {
        Map<String, Number> metrics = new HashMap<String, Number>();
        for (ObjectInstance mbean : allMbeans) {
            ObjectName objectName = mbean.getObjectName();
            if (isDomainAndKeyPropertyConfigured(objectName, mbeanData)) {

                String beanName = objectName.getKeyProperty(GlassFishMBeanKeyPropertyEnum.TYPE.toString());
                List<String> beanProperties = mbeanData.getTypes().get(beanName);
                for (String beanProperty : beanProperties) {
                    Object attribute = jmxConnector.getMBeanAttribute(objectName, beanProperty);
                    if (attribute != null) {
                        String metricKey = getMetricsKey(objectName, beanProperty);
                        try {
                            Long aLong = Double.valueOf(attribute.toString()).longValue();
                            metrics.put(metricKey, aLong);
                        } catch (NumberFormatException e) {
                            logger.error("Unable to parse property[" + beanProperty + "] of bean +[" + beanName + "]");
                        }
                    } else {
                        logger.error("Unable to fetch property[" + beanProperty + "] of bean +[" + beanName + "]");
                    }
                }
            }
        }
        return metrics;
    }

    private boolean isDomainAndKeyPropertyConfigured(ObjectName objectName, MBeanData mbeanData) {
        String domain = objectName.getDomain();
        String keyProperty = objectName.getKeyProperty(GlassFishMBeanKeyPropertyEnum.TYPE.toString());
        Set<String> types = mbeanData.getTypes().keySet();
        boolean configured = mbeanData.getDomainName().equals(domain) && types.contains(keyProperty);
        return configured;
    }

    private String getMetricsKey(ObjectName objectName, String propertyName) {
        String type = objectName.getKeyProperty(GlassFishMBeanKeyPropertyEnum.TYPE.toString());
        String name = objectName.getKeyProperty(GlassFishMBeanKeyPropertyEnum.NAME.toString());

        StringBuilder metricsKey = new StringBuilder();
        metricsKey.append(Strings.isNullOrEmpty(type) ? "" : type + METRICS_SEPARATOR);
        metricsKey.append(Strings.isNullOrEmpty(name) ? "" : name + METRICS_SEPARATOR);
        metricsKey.append(propertyName);

        return metricsKey.toString();
    }

    private String getConfigFilename(String filename) {
        if (filename == null) {
            return "";
        }

        if ("".equals(filename)) {
            filename = FILE_NAME;
        }
        // for absolute paths
        if (new File(filename).exists()) {
            return filename;
        }
        // for relative paths
        File jarPath = PathResolver.resolveDirectory(AManagedMonitor.class);
        String configFileName = "";
        if (!Strings.isNullOrEmpty(filename)) {
            configFileName = jarPath + File.separator + filename;
        }
        return configFileName;
    }

    private void printStats(Configuration config, Map<String, Number> metrics) {
        String metricPath = config.getMetricPrefix();
        for (Map.Entry<String, Number> entry : metrics.entrySet()) {
            printMetric(metricPath + entry.getKey(), entry.getValue());
        }
    }

    private void printMetric(String metricPath, Number metricValue) {
        printMetric(metricPath, metricValue, MetricWriter.METRIC_AGGREGATION_TYPE_AVERAGE, MetricWriter.METRIC_TIME_ROLLUP_TYPE_AVERAGE,
                MetricWriter.METRIC_CLUSTER_ROLLUP_TYPE_COLLECTIVE);
    }

    private void printMetric(String metricPath, Number metricValue, String aggregation, String timeRollup, String cluster) {
        MetricWriter metricWriter = super.getMetricWriter(metricPath, aggregation, timeRollup, cluster);
        if (metricValue != null) {
            if (logger.isDebugEnabled()) {
                logger.debug("Metric [" + metricPath + " = " + metricValue + "]");
            }
            if (metricValue instanceof Double) {
                metricWriter.printMetric(String.valueOf(Math.round((Double) metricValue)));
            } else if (metricValue instanceof Float) {
                metricWriter.printMetric(String.valueOf(Math.round((Float) metricValue)));
            } else {
                metricWriter.printMetric(String.valueOf(metricValue));
            }
        }
    }

    public static void main(String[] args) throws TaskExecutionException {

        Map<String, String> taskArgs = new HashMap<String, String>();
        taskArgs.put(CONFIG_ARG, "/home/satish/AppDynamics/Code/extensions/glassfish-monitoring-extension/src/main/resources/config/config.yml");

        GlassFishMonitor glassFishMonitor = new GlassFishMonitor();
        glassFishMonitor.execute(taskArgs, null);
    }
}
