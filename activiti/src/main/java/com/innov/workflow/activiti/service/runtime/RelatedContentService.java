package com.innov.workflow.activiti.service.runtime;

import com.innov.workflow.activiti.domain.runtime.RelatedContent;
import com.innov.workflow.activiti.repository.runtime.RelatedContentRepository;
import com.innov.workflow.core.domain.entity.User;
import org.activiti.content.storage.api.ContentObject;
import org.activiti.content.storage.api.ContentStorage;
import org.activiti.engine.runtime.Clock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.io.InputStream;
import java.util.*;

@Service
public class RelatedContentService {
    private static final int RELATED_CONTENT_INTERNAL_BATCH_SIZE = 256;
    @Autowired
    protected RelatedContentRepository contentRepository;
    @Autowired
    protected ContentStorage contentStorage;
    @Autowired
    protected Clock clock;

    public RelatedContentService() {
    }

    public Page<RelatedContent> getRelatedContent(String source, String sourceId, int pageSize, int page) {
        PageRequest paging = PageRequest.of(page, pageSize);
        return this.contentRepository.findAllRelatedBySourceAndSourceId(source, sourceId, paging);
    }

    public Page<RelatedContent> getRelatedContentForTask(String taskId, int pageSize, int page) {
        PageRequest paging = PageRequest.of(page, pageSize);
        return this.contentRepository.findAllRelatedByTaskId(taskId, paging);
    }

    public Page<RelatedContent> getRelatedContentForProcessInstance(String processInstanceId, int pageSize, int page) {
        PageRequest paging = PageRequest.of(page, pageSize);
        return this.contentRepository.findAllRelatedByProcessInstanceId(processInstanceId, paging);
    }

    public Page<RelatedContent> getFieldContentForProcessInstance(String processInstanceId, String field, int pageSize, int page) {
        PageRequest paging = PageRequest.of(page, pageSize);
        return this.contentRepository.findAllByProcessInstanceIdAndField(processInstanceId, field, paging);
    }


    public Page<RelatedContent> getFieldContentForTask(String taskId, int pageSize, int page) {
        PageRequest paging = PageRequest.of(page, pageSize);
        return this.contentRepository.findAllFieldBasedContentByTaskId(taskId, paging);
    }

    public Page<RelatedContent> getAllFieldContentForProcessInstance(String processInstanceId, int pageSize, int page) {
        PageRequest paging = PageRequest.of(page, pageSize);
        return this.contentRepository.findAllFieldBasedContentByProcessInstanceId(processInstanceId, paging);
    }

    public Page<RelatedContent> getAllFieldContentForTask(String taskId, String field, int pageSize, int page) {
        PageRequest paging = PageRequest.of(page, pageSize);
        return this.contentRepository.findAllByTaskIdAndField(taskId, field, paging);
    }

    @Transactional
    public RelatedContent createRelatedContent(User user, String name, String source, String sourceId, String taskId, String processId, String field, String mimeType, InputStream data, Long lengthHint) {
        return this.createRelatedContent(user, name, source, sourceId, taskId, processId, mimeType, data, lengthHint, false, false, field);
    }

    @Transactional
    public RelatedContent createRelatedContent(User user, String name, String source, String sourceId, String taskId, String processId, String mimeType, InputStream data, Long lengthHint, boolean relatedContent, boolean link) {
        return this.createRelatedContent(user, name, source, sourceId, taskId, processId, mimeType, data, lengthHint, relatedContent, link, (String) null);
    }

    protected RelatedContent createRelatedContent(User user, String name, String source, String sourceId, String taskId, String processId, String mimeType, InputStream data, Long lengthHint, boolean relatedContent, boolean link, String field) {
        Date timestamp = this.clock.getCurrentTime();
        RelatedContent newContent = new RelatedContent();
        newContent.setName(name);
        newContent.setSource(source);
        newContent.setSourceId(sourceId);
        newContent.setTaskId(taskId);
        newContent.setProcessInstanceId(processId);
        newContent.setCreatedBy(user.getId());
        newContent.setCreated(timestamp);
        newContent.setLastModifiedBy(user.getId());
        newContent.setLastModified(timestamp);
        newContent.setMimeType(mimeType);
        newContent.setRelatedContent(relatedContent);
        newContent.setLink(link);
        newContent.setField(field);
        if (data != null) {
            ContentObject createContentObject = this.contentStorage.createContentObject(data, lengthHint);
            newContent.setContentStoreId(createContentObject.getId());
            newContent.setContentAvailable(true);
            newContent.setContentSize(createContentObject.getContentLength());
        } else if (link) {
            newContent.setContentAvailable(true);
        } else {
            newContent.setContentAvailable(false);
        }

        this.contentRepository.save(newContent);
        return newContent;
    }

    public RelatedContent getRelatedContent(Long id, boolean includeOwner) {
        RelatedContent content = (RelatedContent) this.contentRepository.findById(id).orElse(null);
        if (content != null && includeOwner) {
            content.getCheckoutOwner();
            content.getLockOwner();
        }

        return content;
    }

    @Transactional
    public void deleteRelatedContent(RelatedContent content) {
        if (content.getContentStoreId() != null) {
            final String storeId = content.getContentStoreId();
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                public void afterCommit() {
                    RelatedContentService.this.contentStorage.deleteContentObject(storeId);
                }
            });
        }

        this.contentRepository.delete(content);
    }

    @Transactional
    public boolean lockContent(RelatedContent content, int timeOut, User user) {
        content.setLockDate(this.clock.getCurrentTime());
        content.setLocked(true);
        content.setLockOwner(user.getId().toString());
        Calendar expiration = Calendar.getInstance();
        expiration.setTime(content.getLockDate());
        expiration.add(13, timeOut);
        content.setLockExpirationDate(expiration.getTime());
        this.contentRepository.save(content);
        return true;
    }

    @Transactional
    public boolean checkout(RelatedContent content, User user, boolean toLocal) {
        content.setCheckoutDate(this.clock.getCurrentTime());
        content.setCheckedOut(true);
        content.setCheckedOutToLocal(toLocal);
        content.setCheckoutOwner(user.getId().toString());
        this.contentRepository.save(content);
        return true;
    }

    @Transactional
    public boolean unlock(RelatedContent content) {
        content.setLockDate((Date) null);
        content.setLockExpirationDate((Date) null);
        content.setLockOwner((String) null);
        content.setLocked(false);
        this.contentRepository.save(content);
        return true;
    }

    @Transactional
    public boolean uncheckout(RelatedContent content) {
        content.setCheckoutDate((Date) null);
        content.setCheckedOut(false);
        content.setCheckedOutToLocal(false);
        content.setCheckoutOwner((String) null);
        this.contentRepository.save(content);
        return true;
    }

    @Transactional
    public boolean checkin(RelatedContent content, String comment, boolean keepCheckedOut) {
        if (!keepCheckedOut) {
            content.setCheckoutDate((Date) null);
            content.setCheckedOut(false);
            content.setCheckoutOwner((String) null);
            this.contentRepository.save(content);
            return true;
        } else {
            return false;
        }
    }

    @Transactional
    public void updateRelatedContentData(Long relatedContentId, String contentStoreId, InputStream contentStream, Long lengthHint, User user) {
        Date timestamp = this.clock.getCurrentTime();
        this.contentStorage.updateContentObject(contentStoreId, contentStream, lengthHint);
        RelatedContent relatedContent = (RelatedContent) this.contentRepository.findById(relatedContentId).orElse(null);
        relatedContent.setLastModifiedBy(user.getId());
        relatedContent.setLastModified(timestamp);
        relatedContent.setContentSize(lengthHint);
        this.contentRepository.save(relatedContent);
    }

    @Transactional
    public void updateName(Long relatedContentId, String newName) {
        RelatedContent relatedContent = (RelatedContent) this.contentRepository.findById(relatedContentId).orElse(null);
        relatedContent.setName(newName);
        this.contentRepository.save(relatedContent);
    }

    @Transactional
    public void setContentField(Long relatedContentId, String field, String processInstanceId, String taskId) {
        RelatedContent relatedContent = (RelatedContent) this.contentRepository.findById(relatedContentId).orElse(null);
        relatedContent.setProcessInstanceId(processInstanceId);
        relatedContent.setTaskId(taskId);
        relatedContent.setRelatedContent(false);
        relatedContent.setField(field);
        this.contentRepository.save(relatedContent);
    }

    @Transactional
    public void storeRelatedContent(RelatedContent relatedContent) {
        this.contentRepository.save(relatedContent);
    }

    public ContentStorage getContentStorage() {
        return this.contentStorage;
    }

    @Transactional
    public void deleteContentForProcessInstance(String processInstanceId) {
        int page = 0;
        Page<RelatedContent> content = this.contentRepository.findAllContentByProcessInstanceId(processInstanceId, PageRequest.of(page, 256));
        final Set<String> storageIds = new HashSet();

        while (content != null) {
            Iterator i$ = content.getContent().iterator();

            while (i$.hasNext()) {
                RelatedContent relatedContent = (RelatedContent) i$.next();
                if (relatedContent.getContentStoreId() != null) {
                    storageIds.add(relatedContent.getContentStoreId());
                }
            }

            if (!content.isLast()) {
                ++page;
                content = this.contentRepository.findAllContentByProcessInstanceId(processInstanceId, PageRequest.of(page, 256));
            } else {
                content = null;
            }
        }

        if (!storageIds.isEmpty()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                public void afterCommit() {
                    Iterator i$ = storageIds.iterator();

                    while (i$.hasNext()) {
                        String id = (String) i$.next();
                        RelatedContentService.this.contentStorage.deleteContentObject(id);
                    }

                }
            });
        }

        this.contentRepository.deleteAllContentByProcessInstanceId(processInstanceId);
    }
}
