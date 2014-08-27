# AppDynamics GlassFish Monitoring Extension

This extension works only with the standalone machine agent.

##Use Case

GlassFish is the reference implementation of Java EE and as such supports Enterprise JavaBeans, JPA, JavaServer Faces, JMS, RMI, JavaServer Pages, servlets, etc. This allows developers to create enterprise applications that are portable and scalable, and that integrate with legacy technologie.

##Prerequisites

Please enable JMX for the glassfish container if not already enabled.

##Installation

1. Run "mvn clean install"
2. Download and unzip the file 'target/GlassFishMonitor.zip' to \<machineagent install dir\}/monitors
3. Open <b>monitor.xml</b> and configure the GlassFish arguments

<pre>
&lt;!-- The configuration file which lists out the servar details and metrics to be included from monitoring on controller--&gt;
&lt;argument name="config-file" is-required="true" default-value="monitors/GlassFishMonitor/config.yml" /&gt;
</pre>

##Metrics
The following metrics are reported.

###connector-connection-pool
	ConnectionCreationRetryAttempts
	ConnectionCreationRetryIntervalInSeconds
	IdleTimeoutInSeconds
	MaxConnectionUsageCount
	MaxPoolSize
	MaxWaitTimeInMillis
	PoolResizeQuantity
	SteadyPoolSize
	
###ejb-container
	CacheIdleTimeoutInSeconds
	CacheResizeQuantity
	MaxCacheSize
	MaxPoolSize
	PoolIdleTimeoutInSeconds
	PoolResizeQuantity
	RemovalTimeoutInSeconds
	SteadyPoolSize
	
###http
	CompressionMinSizeBytes
	ConnectionUploadTimeoutMillis
	HeaderBufferLengthBytes
	MaxConnections
	MaxPostSizeBytes
	MaxRequestHeaders
	MaxResponseHeaders
	RequestTimeoutSeconds
	SendBufferSizeBytes
	TimeoutSeconds
	
###jdbc-connection-pool
	ConnectionCreationRetryAttempts
	ConnectionCreationRetryIntervalInSeconds
	ConnectionLeakTimeoutInSeconds
	IdleTimeoutInSeconds
	MaxConnectionUsageCount
	MaxPoolSize
	MaxWaitTimeInMillis
	PoolResizeQuantity
	StatementCacheSize
	StatementLeakTimeoutInSeconds
	SteadyPoolSize

###thread-pool
	IdleThreadTimeoutSeconds
	MaxQueueSize
	MaxThreadPoolSize
	MinThreadPoolSize

#Custom Dashboard
![](https://github.com/Appdynamics/glassfish-monitoring-extension/raw/master/glassfish-monitor.png)

##Contributing

Always feel free to fork and contribute any changes directly here on GitHub.

##Community

Find out more in the [AppSphere]() community.

##Support

For any questions or feature request, please contact [AppDynamics Center of Excellence](mailto:ace-request@appdynamics.com).
