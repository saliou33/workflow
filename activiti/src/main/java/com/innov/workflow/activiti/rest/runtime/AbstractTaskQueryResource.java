package com.innov.workflow.activiti.rest.runtime;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.util.ISO8601Utils;
import com.innov.workflow.activiti.custom.service.IdentityService;
import com.innov.workflow.activiti.model.common.ResultListDataRepresentation;
import com.innov.workflow.activiti.model.idm.UserRepresentation;
import com.innov.workflow.activiti.model.runtime.TaskRepresentation;
import com.innov.workflow.activiti.service.exception.BadRequestException;
import com.innov.workflow.activiti.service.runtime.PermissionService;
import com.innov.workflow.core.domain.entity.User;
import org.activiti.editor.language.json.converter.util.CollectionUtils;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.TaskInfo;
import org.activiti.engine.task.TaskInfoQueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.*;

@Service
public abstract class AbstractTaskQueryResource {
    private static final String SORT_CREATED_ASC = "created-asc";
    private static final String SORT_CREATED_DESC = "created-desc";
    private static final String SORT_DUE_ASC = "due-asc";
    private static final String SORT_DUE_DESC = "due-desc";
    private static final int DEFAULT_PAGE_SIZE = 25;

    @Autowired
    protected RepositoryService repositoryService;
    @Autowired
    protected TaskService taskService;
    @Autowired
    protected RuntimeService runtimeService;
    @Autowired
    protected HistoryService historyService;
    @Autowired
    protected PermissionService permissionService;
    @Autowired
    protected IdentityService identityService;

    public AbstractTaskQueryResource() {
    }


    public Map<String, Long> countTasks() {
        User user = identityService.getCurrentUserObject();
        Long nbActive = taskService.createTaskQuery().taskAssignee(user.getId()).active().count();
        Long nbSuspended = taskService.createTaskQuery().taskAssignee(user.getId()).suspended().count();
        Long nbCompleted = historyService.createHistoricTaskInstanceQuery().taskAssignee(user.getId()).finished().count();

        Map<String, Long> counts = new HashMap<>();
        counts.put("Finished", nbCompleted);
        counts.put("Suspended", nbSuspended);
        counts.put("Active", nbActive);

        return counts;
    }

    public ResultListDataRepresentation listTasks(ObjectNode requestNode) {
        if (requestNode == null) {
            throw new BadRequestException("No request found");
        } else {
            User currentUser = identityService.getCurrentUserObject();
            JsonNode stateNode = requestNode.get("state");
            TaskInfoQueryWrapper taskInfoQueryWrapper = null;
            if (stateNode != null && "completed".equals(stateNode.asText())) {
                HistoricTaskInstanceQuery historicTaskInstanceQuery = this.historyService.createHistoricTaskInstanceQuery();
                historicTaskInstanceQuery.finished();
                taskInfoQueryWrapper = new TaskInfoQueryWrapper(historicTaskInstanceQuery);
            } else {
                taskInfoQueryWrapper = new TaskInfoQueryWrapper(this.taskService.createTaskQuery());
            }

            JsonNode deploymentKeyNode = requestNode.get("deploymentKey");
            if (deploymentKeyNode != null && !deploymentKeyNode.isNull()) {
                List<Deployment> deployments = this.repositoryService.createDeploymentQuery().deploymentKey(deploymentKeyNode.asText()).list();
                List<String> deploymentIds = new ArrayList(deployments.size());
                Iterator i$ = deployments.iterator();

                while (i$.hasNext()) {
                    Deployment deployment = (Deployment) i$.next();
                    deploymentIds.add(deployment.getId());
                }

                taskInfoQueryWrapper.getTaskInfoQuery().or().deploymentIdIn(deploymentIds).taskCategory(deploymentKeyNode.asText()).endOr();
            }

            JsonNode processInstanceIdNode = requestNode.get("processInstanceId");
            if (processInstanceIdNode != null && !processInstanceIdNode.isNull()) {
                this.handleProcessInstanceFiltering(currentUser, taskInfoQueryWrapper, processInstanceIdNode);
            }

            JsonNode textNode = requestNode.get("text");
            if (textNode != null && !textNode.isNull()) {
                this.handleTextFiltering(taskInfoQueryWrapper, textNode);
            }

            JsonNode assignmentNode = requestNode.get("assignment");
            if (assignmentNode != null && !assignmentNode.isNull()) {
                this.handleAssignment(taskInfoQueryWrapper, assignmentNode, currentUser);
            }

            JsonNode processDefinitionNode = requestNode.get("processDefinitionId");
            if (processDefinitionNode != null && !processDefinitionNode.isNull()) {
                this.handleProcessDefinition(taskInfoQueryWrapper, processDefinitionNode);
            }

            JsonNode dueBeforeNode = requestNode.get("dueBefore");
            if (dueBeforeNode != null && !dueBeforeNode.isNull()) {
                this.handleDueBefore(taskInfoQueryWrapper, dueBeforeNode);
            }

            JsonNode dueAfterNode = requestNode.get("dueAfter");
            if (dueAfterNode != null && !dueAfterNode.isNull()) {
                this.handleDueAfter(taskInfoQueryWrapper, dueAfterNode);
            }

            JsonNode sortNode = requestNode.get("sort");
            if (sortNode != null) {
                this.handleSorting(taskInfoQueryWrapper, sortNode);
            }

            int page = 0;
            JsonNode pageNode = requestNode.get("page");
            if (pageNode != null && !pageNode.isNull()) {
                page = pageNode.asInt(0);
            }

            int size = 25;
            JsonNode sizeNode = requestNode.get("size");
            if (sizeNode != null && !sizeNode.isNull()) {
                size = sizeNode.asInt(25);
            }

            List<? extends TaskInfo> tasks = taskInfoQueryWrapper.getTaskInfoQuery().listPage(page * size, size);
            JsonNode includeProcessInstanceNode = requestNode.get("includeProcessInstance");
            Map<String, String> processInstancesNames = new HashMap();
            if (includeProcessInstanceNode != null) {
                this.handleIncludeProcessInstance(taskInfoQueryWrapper, includeProcessInstanceNode, tasks, processInstancesNames);
            }

            ResultListDataRepresentation result = new ResultListDataRepresentation(this.convertTaskInfoList(tasks, processInstancesNames));
            if (page != 0 || tasks.size() == size) {
                Long totalCount = taskInfoQueryWrapper.getTaskInfoQuery().count();
                result.setTotal((long) totalCount.intValue());
                result.setStart(page * size);
            }

            return result;
        }
    }

    private void handleProcessInstanceFiltering(User currentUser, TaskInfoQueryWrapper taskInfoQueryWrapper, JsonNode processInstanceIdNode) {
        String processInstanceId = processInstanceIdNode.asText();
        taskInfoQueryWrapper.getTaskInfoQuery().processInstanceId(processInstanceId);
    }

    private void handleTextFiltering(TaskInfoQueryWrapper taskInfoQueryWrapper, JsonNode textNode) {
        String text = textNode.asText();
        taskInfoQueryWrapper.getTaskInfoQuery().taskNameLikeIgnoreCase("%" + text + "%");
    }

    private void handleAssignment(TaskInfoQueryWrapper taskInfoQueryWrapper, JsonNode assignmentNode, User currentUser) {
        String assignment = assignmentNode.asText();
        if (assignment.length() > 0) {
            String currentUserId = String.valueOf(currentUser.getId());
            if ("assignee".equals(assignment)) {
                taskInfoQueryWrapper.getTaskInfoQuery().taskAssignee(currentUserId);
            } else if ("candidate".equals(assignment)) {
                taskInfoQueryWrapper.getTaskInfoQuery().taskCandidateUser(currentUserId);
            } else if (assignment.startsWith("group_")) {
                String groupIdString = assignment.replace("group_", "");

                try {
                    Long.valueOf(groupIdString);
                } catch (NumberFormatException var8) {
                    throw new BadRequestException("Invalid group id");
                }

                taskInfoQueryWrapper.getTaskInfoQuery().taskCandidateGroup(groupIdString);
            } else {
                taskInfoQueryWrapper.getTaskInfoQuery().taskInvolvedUser(currentUserId);
            }
        }

    }

    private void handleProcessDefinition(TaskInfoQueryWrapper taskInfoQueryWrapper, JsonNode processDefinitionIdNode) {
        String processDefinitionId = processDefinitionIdNode.asText();
        taskInfoQueryWrapper.getTaskInfoQuery().processDefinitionId(processDefinitionId);
    }

    private void handleDueBefore(TaskInfoQueryWrapper taskInfoQueryWrapper, JsonNode dueBeforeNode) {
        String date = dueBeforeNode.asText();
        Date d = null;
        try {
            d = ISO8601Utils.parse(date, null);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        taskInfoQueryWrapper.getTaskInfoQuery().taskDueBefore(d);
    }

    private void handleDueAfter(TaskInfoQueryWrapper taskInfoQueryWrapper, JsonNode dueAfterNode) {
        String date = dueAfterNode.asText();
        Date d = null;
        try {
            d = ISO8601Utils.parse(date, null);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        taskInfoQueryWrapper.getTaskInfoQuery().taskDueAfter(d);
    }

    private void handleSorting(TaskInfoQueryWrapper taskInfoQueryWrapper, JsonNode sortNode) {
        String sort = sortNode.asText();
        if ("created-asc".equals(sort)) {
            taskInfoQueryWrapper.getTaskInfoQuery().orderByTaskCreateTime().asc();
        } else if ("created-desc".equals(sort)) {
            taskInfoQueryWrapper.getTaskInfoQuery().orderByTaskCreateTime().desc();
        } else if ("due-asc".equals(sort)) {
            taskInfoQueryWrapper.getTaskInfoQuery().orderByDueDateNullsLast().asc();
        } else if ("due-desc".equals(sort)) {
            taskInfoQueryWrapper.getTaskInfoQuery().orderByDueDateNullsLast().desc();
        } else {
            taskInfoQueryWrapper.getTaskInfoQuery().orderByTaskCreateTime().desc();
        }

    }

    private void handleIncludeProcessInstance(TaskInfoQueryWrapper taskInfoQueryWrapper, JsonNode includeProcessInstanceNode, List<? extends TaskInfo> tasks, Map<String, String> processInstanceNames) {
        if (includeProcessInstanceNode.asBoolean() && CollectionUtils.isNotEmpty(tasks)) {
            Set<String> processInstanceIds = new HashSet();
            Iterator i$ = tasks.iterator();

            while (i$.hasNext()) {
                TaskInfo task = (TaskInfo) i$.next();
                if (task.getProcessInstanceId() != null) {
                    processInstanceIds.add(task.getProcessInstanceId());
                }
            }

            if (CollectionUtils.isNotEmpty(processInstanceIds)) {
                List processInstances;
                Iterator j$;
                if (taskInfoQueryWrapper.getTaskInfoQuery() instanceof HistoricTaskInstanceQuery) {
                    processInstances = this.historyService.createHistoricProcessInstanceQuery().processInstanceIds(processInstanceIds).list();
                    j$ = processInstances.iterator();

                    while (j$.hasNext()) {
                        HistoricProcessInstance processInstance = (HistoricProcessInstance) i$.next();
                        processInstanceNames.put(processInstance.getId(), processInstance.getName());
                    }
                } else {
                    processInstances = this.runtimeService.createProcessInstanceQuery().processInstanceIds(processInstanceIds).list();
                    j$ = processInstances.iterator();

                    while (j$.hasNext()) {
                        ProcessInstance processInstance = (ProcessInstance) i$.next();
                        processInstanceNames.put(processInstance.getId(), processInstance.getName());
                    }
                }
            }
        }
    }

    protected List<TaskRepresentation> convertTaskInfoList(List<? extends TaskInfo> tasks, Map<String, String> processInstanceNames) {
        List<TaskRepresentation> result = new ArrayList();
        TaskRepresentation representation;
        if (CollectionUtils.isNotEmpty(tasks)) {
            for (Iterator i$ = tasks.iterator(); i$.hasNext(); result.add(representation)) {
                TaskInfo task = (TaskInfo) i$.next();
                ProcessDefinitionEntity processDefinition = null;
                if (task.getProcessDefinitionId() != null) {
                    processDefinition = (ProcessDefinitionEntity) this.repositoryService.getProcessDefinition(task.getProcessDefinitionId());
                }

                representation = new TaskRepresentation(task, processDefinition, processInstanceNames.get(task.getProcessInstanceId()));
                if (StringUtils.isNotEmpty(task.getAssignee())) {

                    User user = this.identityService.getUser(task.getAssignee());
                    if (user != null) {
                        representation.setAssignee(new UserRepresentation(user));
                    }
                }
            }
        }

        return result;
    }
}
