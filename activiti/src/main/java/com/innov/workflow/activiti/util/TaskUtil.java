package com.innov.workflow.activiti.util;

import com.innov.workflow.activiti.model.idm.GroupRepresentation;
import com.innov.workflow.activiti.model.idm.UserRepresentation;
import com.innov.workflow.activiti.model.runtime.TaskRepresentation;
import com.innov.workflow.activiti.old.service.IdentityService;
import com.innov.workflow.core.domain.entity.User;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.ExtensionElement;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.UserTask;
import org.activiti.editor.language.json.converter.util.CollectionUtils;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.history.HistoricIdentityLink;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.task.TaskInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.*;

public class TaskUtil {
    public TaskUtil() {
    }

    public static void fillPermissionInformation(TaskRepresentation taskRepresentation, TaskInfo task, User currentUser, IdentityService identityService, HistoryService historyService, RepositoryService repositoryService) {
        String processInstanceStartUserId = null;
        boolean initiatorCanCompleteTask = true;
        boolean isMemberOfCandidateGroup = false;
        boolean isMemberOfCandidateUsers = false;
        if (task.getProcessInstanceId() != null) {
            HistoricProcessInstance historicProcessInstance = (HistoricProcessInstance) historyService.createHistoricProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).singleResult();
            if (historicProcessInstance != null) {
                processInstanceStartUserId = historicProcessInstance.getStartUserId();
                BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());
                FlowElement flowElement = bpmnModel.getFlowElement(task.getTaskDefinitionKey());
                List links;
                if (flowElement != null && flowElement instanceof UserTask) {
                    UserTask userTask = (UserTask) flowElement;
                    links = (List) userTask.getExtensionElements().get("initiator-can-complete");
                    if (CollectionUtils.isNotEmpty(links)) {
                        String value = ((ExtensionElement) links.get(0)).getElementText();
                        if (StringUtils.isNotEmpty(value)) {
                            initiatorCanCompleteTask = Boolean.valueOf(value);
                        }
                    }

                    Map<String, Object> variableMap = new HashMap();
                    List groups;
                    if (CollectionUtils.isNotEmpty(userTask.getCandidateGroups()) && userTask.getCandidateGroups().size() == 1 && ((String) userTask.getCandidateGroups().get(0)).contains("${taskAssignmentBean.assignTaskToCandidateGroups('") || CollectionUtils.isNotEmpty(userTask.getCandidateUsers()) && userTask.getCandidateUsers().size() == 1 && ((String) userTask.getCandidateUsers().get(0)).contains("${taskAssignmentBean.assignTaskToCandidateUsers('")) {
                        groups = historyService.createHistoricVariableInstanceQuery().processInstanceId(task.getProcessInstanceId()).list();
                        if (CollectionUtils.isNotEmpty(groups)) {
                            Iterator i$ = groups.iterator();

                            while (i$.hasNext()) {
                                HistoricVariableInstance historicVariableInstance = (HistoricVariableInstance) i$.next();
                                variableMap.put(historicVariableInstance.getVariableName(), historicVariableInstance.getValue());
                            }
                        }
                    }

                    if (CollectionUtils.isNotEmpty(userTask.getCandidateGroups())) {

                        List<String> groupIds = identityService.getGroupsIds(currentUser);
                        groups = identityService.getGroupsIds(currentUser);
                        if (CollectionUtils.isNotEmpty(groupIds)) {

                            if (userTask.getCandidateGroups().size() == 1 && ((String) userTask.getCandidateGroups().get(0)).contains("${taskAssignmentBean.assignTaskToCandidateGroups('")) {
                                String candidateGroupString = (String) userTask.getCandidateGroups().get(0);
                                candidateGroupString = candidateGroupString.replace("${taskAssignmentBean.assignTaskToCandidateGroups('", "");
                                candidateGroupString = candidateGroupString.replace("', execution)}", "");
                                String[] groupsArray = candidateGroupString.split(",");
                                String[] arr$ = groupsArray;
                                int len$ = groupsArray.length;

                                for (int i$ = 0; i$ < len$; ++i$) {
                                    String group = arr$[i$];
                                    if (group.contains("field(")) {
                                        String fieldCandidate = group.trim().substring(6, group.length() - 1);
                                        Object fieldValue = variableMap.get(fieldCandidate);
                                        if (fieldValue != null && NumberUtils.isNumber(fieldValue.toString())) {
                                            groupIds.add(fieldValue.toString());
                                        }
                                    } else {
                                        groupIds.add(group);
                                    }
                                }
                            } else {
                                groupIds.addAll(userTask.getCandidateGroups());
                            }

                            Iterator i$ = groups.iterator();

                            while (i$.hasNext()) {
                                String groupId = (String) i$.next();
                                if (groupIds.contains(groupId)) {
                                    isMemberOfCandidateGroup = true;
                                    break;
                                }
                            }
                        }
                    }

                    if (CollectionUtils.isNotEmpty(userTask.getCandidateUsers())) {
                        if (userTask.getCandidateUsers().size() == 1 && ((String) userTask.getCandidateUsers().get(0)).contains("${taskAssignmentBean.assignTaskToCandidateUsers('")) {
                            String candidateUserString = (String) userTask.getCandidateUsers().get(0);
                            candidateUserString = candidateUserString.replace("${taskAssignmentBean.assignTaskToCandidateUsers('", "");
                            candidateUserString = candidateUserString.replace("', execution)}", "");
                            String[] users = candidateUserString.split(",");
                            String[] arr$ = users;
                            int len$ = users.length;

                            for (int i$ = 0; i$ < len$; ++i$) {
                                String user = arr$[i$];
                                if (user.contains("field(")) {
                                    String fieldCandidate = user.substring(6, user.length() - 1);
                                    Object fieldValue = variableMap.get(fieldCandidate);
                                    if (fieldValue != null && NumberUtils.isNumber(fieldValue.toString()) && String.valueOf(currentUser.getId()).equals(fieldValue.toString())) {
                                        isMemberOfCandidateGroup = true;
                                        break;
                                    }
                                } else if (user.equals(String.valueOf(currentUser.getId()))) {
                                    isMemberOfCandidateGroup = true;
                                    break;
                                }
                            }
                        } else if (userTask.getCandidateUsers().contains(String.valueOf(currentUser.getId()))) {
                            isMemberOfCandidateUsers = true;
                        }
                    }
                }

                if (!isMemberOfCandidateGroup && !isMemberOfCandidateUsers) {
                    List<String> candidateGroupIds = new ArrayList();
                    links = historyService.getHistoricIdentityLinksForTask(task.getId());
                    Iterator i$ = links.iterator();

                    label106:
                    while (true) {
                        while (i$.hasNext()) {
                            HistoricIdentityLink historicIdentityLink = (HistoricIdentityLink) i$.next();
                            if (!isMemberOfCandidateUsers && StringUtils.isNotEmpty(historicIdentityLink.getUserId()) && String.valueOf(currentUser.getId()).equals(historicIdentityLink.getUserId()) && "candidate".equalsIgnoreCase(historicIdentityLink.getType())) {
                                isMemberOfCandidateUsers = true;
                            } else if (StringUtils.isNotEmpty(historicIdentityLink.getGroupId()) && "candidate".equalsIgnoreCase(historicIdentityLink.getType())) {
                                candidateGroupIds.add(historicIdentityLink.getGroupId());
                            }
                        }

                        List<GroupRepresentation> groups2 = (new UserRepresentation(currentUser)).getGroups();
                        if (groups2 != null) {
                            Iterator v$ = groups2.iterator();

                            while (i$.hasNext()) {
                                GroupRepresentation group3 = (GroupRepresentation) v$.next();
                                if (candidateGroupIds.contains(group3.getId().toString())) {
                                    isMemberOfCandidateGroup = true;
                                    break label106;
                                }
                            }
                        }
                        break;
                    }
                }
            }
        }

        taskRepresentation.setProcessInstanceStartUserId(processInstanceStartUserId);
        taskRepresentation.setInitiatorCanCompleteTask(initiatorCanCompleteTask);
        taskRepresentation.setMemberOfCandidateGroup(isMemberOfCandidateGroup);
        taskRepresentation.setMemberOfCandidateUsers(isMemberOfCandidateUsers);
    }
}
