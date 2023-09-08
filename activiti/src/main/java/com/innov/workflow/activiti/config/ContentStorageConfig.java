package com.innov.workflow.activiti.config;

import com.innov.workflow.core.constant.Constants;
import lombok.extern.slf4j.Slf4j;
import org.activiti.content.storage.api.ContentStorage;
import org.activiti.content.storage.fs.FileSystemContentStorage;
import org.activiti.spring.boot.ActivitiProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.io.File;

@Configuration
@EnableConfigurationProperties(ActivitiProperties.class)
@Slf4j
public class ContentStorageConfig {
    private static final String PROP_FS_ROOT = "contentstorage.fs.rootFolder";
    private static final String PROP_FS_DEPTH = "contentstorage.fs.depth";
    private static final String PROP_FS_BLOCK_SIZE = "contentstorage.fs.blockSize";
    private static final String PROP_FS_CREATE_ROOT = "contentstorage.fs.createRoot";
    private static final Integer DEFAULT_FS_DEPTH = 4;
    private static final Integer DEFAULT_FS_BLOCK_SIZE = 1024;

    @Autowired
    private Environment env;

    public ContentStorageConfig() {
    }

    @Bean
    public ContentStorage contentStorage() {
        String fsRoot = this.env.getProperty("contentstorage.fs.rootFolder", Constants.UPLOAD_PATH);
        ContentStorageConfig.log.info("Using file-system based content storage (" + fsRoot + ")");
        Integer iterationDepth = this.env.getProperty("contentstorage.fs.depth", Integer.class, DEFAULT_FS_DEPTH);
        Integer blockSize = this.env.getProperty("contentstorage.fs.blockSize", Integer.class, DEFAULT_FS_BLOCK_SIZE);
        File root = new File(fsRoot);
        if (this.env.getProperty("contentstorage.fs.createRoot", Boolean.class, Boolean.FALSE) && !root.exists()) {
            ContentStorageConfig.log.info("Creating content storage root and possible missing parents: " + root.getAbsolutePath());
            root.mkdirs();
        }

        if (root != null && root.exists()) {
            ContentStorageConfig.log.info("File system root : " + root.getAbsolutePath());
        }

        return new FileSystemContentStorage(root, blockSize, iterationDepth);
    }
}
