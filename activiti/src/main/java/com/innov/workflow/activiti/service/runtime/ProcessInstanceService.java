package com.innov.workflow.activiti.service.runtime;

import org.activiti.engine.HistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProcessInstanceService {
    @Autowired
    protected RelatedContentService relatedContentService;
    @Autowired
    protected CommentService commentService;
    @Autowired
    protected HistoryService historyService;

    public ProcessInstanceService() {
    }

    @Transactional
    public void deleteProcessInstance(String processInstanceId) {
        //this.relatedContentService.deleteContentForProcessInstance(processInstanceId);
        //this.commentService.deleteAllCommentsForProcessInstance(processInstanceId);
        //this.historyService.deleteHistoricProcessInstance(processInstanceId);
    }
}
