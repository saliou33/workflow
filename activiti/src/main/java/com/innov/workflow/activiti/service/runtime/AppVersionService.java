package com.innov.workflow.activiti.service.runtime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Service
public class AppVersionService {
    private static final Logger logger = LoggerFactory.getLogger(AppVersionService.class);
    private static final String VERSION_FILE = "/version.properties";
    private static final String TYPE = "type";
    private static final String MAJOR_VERSION = "version.major";
    private static final String MINOR_VERSION = "version.minor";
    private static final String REVISION_VERSION = "version.revision";
    private static final String EDITION = "version.edition";
    private static final String MAVEN_VERSION = "maven.version";
    private static final String GIT_VERSION = "git.version";
    private Map<String, String> versionInfo;

    public AppVersionService() {
    }

    public Map<String, String> getVersionInfo() {
        if (this.versionInfo == null) {
            Properties properties = new Properties();

            try {
                properties.load(this.getClass().getResourceAsStream("/version.properties"));
            } catch (IOException var3) {
                logger.warn("Could not load version.properties", var3);
            }

            Map<String, String> temp = new HashMap();
            this.putIfExists(properties, "type", temp, "type");
            this.putIfExists(properties, "version.major", temp, "majorVersion");
            this.putIfExists(properties, "version.minor", temp, "minorVersion");
            this.putIfExists(properties, "version.revision", temp, "revisionVersion");
            this.putIfExists(properties, "version.edition", temp, "edition");
            this.putIfExists(properties, "maven.version", temp, "mavenVersion");
            this.putIfExists(properties, "git.version", temp, "gitVersion");
            this.versionInfo = temp;
        }

        return this.versionInfo;
    }

    protected void putIfExists(Properties properties, String property, Map<String, String> map, String mapKey) {
        String value = properties.getProperty(property);
        if (value != null) {
            map.put(mapKey, value);
        }

    }
}
