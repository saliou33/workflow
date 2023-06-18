package com.innov.workflow.activiti.service.runtime;

import com.innov.workflow.activiti.domain.runtime.RelatedContent;
import lombok.extern.slf4j.Slf4j;
import org.activiti.content.storage.api.ContentStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
@Slf4j
public class RelatedContentStreamProvider {
    @Autowired
    private ContentStorage contentStorage;

    public RelatedContentStreamProvider() {
    }

    public InputStream getContentStream(RelatedContent content) {
        return this.contentStorage.getContentObject(content.getContentStoreId()).getContent();
    }
}
