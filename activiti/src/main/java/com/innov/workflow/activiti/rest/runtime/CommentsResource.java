package com.innov.workflow.activiti.rest.runtime;

import com.innov.workflow.activiti.model.common.ResultListDataRepresentation;
import com.innov.workflow.activiti.model.runtime.CommentRepresentation;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class CommentsResource extends AbstractCommentsResource {
    public CommentsResource() {
    }

    @RequestMapping(
            value = {"/activiti/tasks/{taskId}/comments"},
            method = {RequestMethod.GET},
            produces = {"application/json"}
    )
    public ResultListDataRepresentation getTaskComments(@PathVariable("taskId") String taskId, @RequestParam(value = "latestFirst", required = false) Boolean latestFirst) {
        return super.getTaskComments(taskId, latestFirst);
    }

    @RequestMapping(
            value = {"/activiti/tasks/{taskId}/comments"},
            method = {RequestMethod.POST},
            produces = {"application/json"}
    )
    public CommentRepresentation addTaskComment(@RequestBody CommentRepresentation commentRequest, @PathVariable("taskId") String taskId) {
        return super.addTaskComment(commentRequest, taskId);
    }

    @RequestMapping(
            value = {"/activiti/process-instances/{processInstanceId}/comments"},
            method = {RequestMethod.GET},
            produces = {"application/json"}
    )
    public ResultListDataRepresentation getProcessInstanceComments(@PathVariable("processInstanceId") String processInstanceId, @RequestParam(value = "latestFirst", required = false) Boolean latestFirst) {
        return super.getProcessInstanceComments(processInstanceId, latestFirst);
    }

    @RequestMapping(
            value = {"/activiti/process-instances/{processInstanceId}/comments"},
            method = {RequestMethod.POST},
            produces = {"application/json"}
    )
    public CommentRepresentation addProcessInstanceComment(@RequestBody CommentRepresentation commentRequest, @PathVariable("processInstanceId") String processInstanceId) {
        return super.addProcessInstanceComment(commentRequest, processInstanceId);
    }
}
