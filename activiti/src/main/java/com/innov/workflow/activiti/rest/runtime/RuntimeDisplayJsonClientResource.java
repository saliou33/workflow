package com.innov.workflow.activiti.rest.runtime;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.innov.workflow.activiti.old.service.IdentityService;
import com.innov.workflow.activiti.service.editor.mapper.*;
import com.innov.workflow.activiti.service.exception.BadRequestException;
import com.innov.workflow.activiti.service.exception.InternalServerErrorException;
import com.innov.workflow.activiti.service.exception.NotPermittedException;
import com.innov.workflow.activiti.service.runtime.PermissionService;
import com.innov.workflow.core.domain.entity.User;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.*;
import org.activiti.editor.language.json.converter.util.CollectionUtils;
import org.activiti.engine.HistoryService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.Job;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/app")
public class RuntimeDisplayJsonClientResource {
    @Autowired
    protected RepositoryService repositoryService;
    @Autowired
    protected RuntimeService runtimeService;
    @Autowired
    protected HistoryService historyService;
    @Autowired
    protected ManagementService managementService;
    @Autowired
    protected PermissionService permissionService;
    @Autowired
    protected IdentityService identityService;
    protected ObjectMapper objectMapper = new ObjectMapper();
    protected List<String> eventElementTypes = new ArrayList();
    protected Map<String, InfoMapper> propertyMappers = new HashMap();

    public RuntimeDisplayJsonClientResource() {
        this.eventElementTypes.add("StartEvent");
        this.eventElementTypes.add("EndEvent");
        this.eventElementTypes.add("BoundaryEvent");
        this.eventElementTypes.add("IntermediateCatchEvent");
        this.eventElementTypes.add("ThrowEvent");
        this.propertyMappers.put("BoundaryEvent", new EventInfoMapper());
        this.propertyMappers.put("EndEvent", new EventInfoMapper());
        this.propertyMappers.put("IntermediateCatchEvent", new EventInfoMapper());
        this.propertyMappers.put("ReceiveTask", new ReceiveTaskInfoMapper());
        this.propertyMappers.put("StartEvent", new EventInfoMapper());
        this.propertyMappers.put("SequenceFlow", new SequenceFlowInfoMapper());
        this.propertyMappers.put("ServiceTask", new ServiceTaskInfoMapper());
        this.propertyMappers.put("ThrowEvent", new EventInfoMapper());
        this.propertyMappers.put("UserTask", new UserTaskInfoMapper());
    }

    @RequestMapping(
            value = {"/rest/process-instances/{processInstanceId}/model-json"},
            method = {RequestMethod.GET},
            produces = {"application/json"}
    )
    public JsonNode getModelJSON(@PathVariable String processInstanceId) {
        User currentUser = identityService.getCurrentUserObject();
        if (!this.permissionService.hasReadPermissionOnProcessInstance(currentUser, processInstanceId)) {
            throw new NotPermittedException();
        } else {
            ProcessInstance processInstance = (ProcessInstance) this.runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
            if (processInstance == null) {
                throw new BadRequestException("No process instance found with id " + processInstanceId);
            } else {
                BpmnModel pojoModel = this.repositoryService.getBpmnModel(processInstance.getProcessDefinitionId());
                if (pojoModel != null && !pojoModel.getLocationMap().isEmpty()) {
                    List<HistoricActivityInstance> activityInstances = this.historyService.createHistoricActivityInstanceQuery().processInstanceId(processInstanceId).list();
                    Set<String> completedActivityInstances = new HashSet();
                    Set<String> currentElements = new HashSet();
                    if (CollectionUtils.isNotEmpty(activityInstances)) {
                        Iterator i$ = activityInstances.iterator();

                        while (i$.hasNext()) {
                            HistoricActivityInstance activityInstance = (HistoricActivityInstance) i$.next();
                            if (activityInstance.getEndTime() != null) {
                                completedActivityInstances.add(activityInstance.getActivityId());
                            } else {
                                currentElements.add(activityInstance.getActivityId());
                            }
                        }
                    }

                    List<Job> jobs = this.managementService.createJobQuery().processInstanceId(processInstanceId).list();
                    List completedFlows;
                    if (CollectionUtils.isNotEmpty(jobs)) {
                        completedFlows = this.runtimeService.createExecutionQuery().processInstanceId(processInstanceId).list();
                        Map<String, Execution> executionMap = new HashMap();
                        Iterator i$ = completedFlows.iterator();

                        while (i$.hasNext()) {
                            Execution execution = (Execution) i$.next();
                            executionMap.put(execution.getId(), execution);
                        }

                        i$ = jobs.iterator();

                        while (i$.hasNext()) {
                            Job job = (Job) i$.next();
                            if (executionMap.containsKey(job.getExecutionId())) {
                                currentElements.add(((Execution) executionMap.get(job.getExecutionId())).getActivityId());
                            }
                        }
                    }

                    completedFlows = this.gatherCompletedFlows(completedActivityInstances, currentElements, pojoModel);
                    Set<String> completedElements = new HashSet(completedActivityInstances);
                    completedElements.addAll(completedFlows);
                    ObjectNode displayNode = this.processProcessElements(pojoModel, completedElements, currentElements);
                    Iterator i$;
                    String current;
                    ArrayNode completedSequenceFlows;
                    if (completedActivityInstances != null) {
                        completedSequenceFlows = displayNode.putArray("completedActivities");
                        i$ = completedActivityInstances.iterator();

                        while (i$.hasNext()) {
                            current = (String) i$.next();
                            completedSequenceFlows.add(current);
                        }
                    }

                    if (currentElements != null) {
                        completedSequenceFlows = displayNode.putArray("currentActivities");
                        i$ = currentElements.iterator();

                        while (i$.hasNext()) {
                            current = (String) i$.next();
                            completedSequenceFlows.add(current);
                        }
                    }

                    if (completedFlows != null) {
                        completedSequenceFlows = displayNode.putArray("completedSequenceFlows");
                        i$ = completedFlows.iterator();

                        while (i$.hasNext()) {
                            current = (String) i$.next();
                            completedSequenceFlows.add(current);
                        }
                    }

                    return displayNode;
                } else {
                    throw new InternalServerErrorException("Process definition could not be found with id " + processInstance.getProcessDefinitionId());
                }
            }
        }
    }

    @RequestMapping(
            value = {"/rest/process-definitions/{processDefinitionId}/model-json"},
            method = {RequestMethod.GET},
            produces = {"application/json"}
    )
    public JsonNode getModelJSONForProcessDefinition(@PathVariable String processDefinitionId) {
        BpmnModel pojoModel = this.repositoryService.getBpmnModel(processDefinitionId);
        if (pojoModel != null && !pojoModel.getLocationMap().isEmpty()) {
            return this.processProcessElements(pojoModel, (Set) null, (Set) null);
        } else {
            throw new InternalServerErrorException("Process definition could not be found with id " + processDefinitionId);
        }
    }

    @RequestMapping(
            value = {"/rest/process-instances/history/{processInstanceId}/model-json"},
            method = {RequestMethod.GET},
            produces = {"application/json"}
    )
    public JsonNode getModelHistoryJSON(@PathVariable String processInstanceId) {
        User currentUser = identityService.getCurrentUserObject();
        if (!this.permissionService.hasReadPermissionOnProcessInstance(currentUser, processInstanceId)) {
            throw new NotPermittedException();
        } else {
            HistoricProcessInstance processInstance = (HistoricProcessInstance) this.historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
            if (processInstance == null) {
                throw new BadRequestException("No process instance found with id " + processInstanceId);
            } else {
                BpmnModel pojoModel = this.repositoryService.getBpmnModel(processInstance.getProcessDefinitionId());
                if (pojoModel != null && !pojoModel.getLocationMap().isEmpty()) {
                    List<HistoricActivityInstance> activityInstances = this.historyService.createHistoricActivityInstanceQuery().processInstanceId(processInstanceId).list();
                    Set<String> completedActivityInstances = new HashSet();
                    Set<String> currentActivityinstances = new HashSet();
                    if (CollectionUtils.isNotEmpty(activityInstances)) {
                        Iterator i$ = activityInstances.iterator();

                        while (i$.hasNext()) {
                            HistoricActivityInstance activityInstance = (HistoricActivityInstance) i$.next();
                            if (activityInstance.getEndTime() != null) {
                                completedActivityInstances.add(activityInstance.getActivityId());
                            } else {
                                currentActivityinstances.add(activityInstance.getActivityId());
                            }
                        }
                    }

                    ObjectNode displayNode = this.processProcessElements(pojoModel, completedActivityInstances, currentActivityinstances);
                    return displayNode;
                } else {
                    throw new InternalServerErrorException("Process definition could not be found with id " + processInstance.getProcessDefinitionId());
                }
            }
        }
    }

    protected ObjectNode processProcessElements(BpmnModel pojoModel, Set<String> completedElements, Set<String> currentElements) {
        ObjectNode displayNode = this.objectMapper.createObjectNode();
        GraphicInfo diagramInfo = new GraphicInfo();
        ArrayNode elementArray = this.objectMapper.createArrayNode();
        ArrayNode flowArray = this.objectMapper.createArrayNode();
        if (CollectionUtils.isNotEmpty(pojoModel.getPools())) {
            ArrayNode poolArray = this.objectMapper.createArrayNode();
            boolean firstElement = true;

            for (Iterator i$ = pojoModel.getPools().iterator(); i$.hasNext(); firstElement = false) {
                Pool pool = (Pool) i$.next();
                ObjectNode poolNode = this.objectMapper.createObjectNode();
                poolNode.put("id", pool.getId());
                poolNode.put("name", pool.getName());
                GraphicInfo poolInfo = pojoModel.getGraphicInfo(pool.getId());
                this.fillGraphicInfo(poolNode, poolInfo, true);
                Process process = pojoModel.getProcess(pool.getId());
                if (process != null && CollectionUtils.isNotEmpty(process.getLanes())) {
                    ArrayNode laneArray = this.objectMapper.createArrayNode();
                    Iterator j$ = process.getLanes().iterator();

                    while (j$.hasNext()) {
                        Lane lane = (Lane) j$.next();
                        ObjectNode laneNode = this.objectMapper.createObjectNode();
                        laneNode.put("id", lane.getId());
                        laneNode.put("name", lane.getName());
                        this.fillGraphicInfo(laneNode, pojoModel.getGraphicInfo(lane.getId()), true);
                        laneArray.add(laneNode);
                    }

                    poolNode.put("lanes", laneArray);
                }

                poolArray.add(poolNode);
                double rightX = poolInfo.getX() + poolInfo.getWidth();
                double bottomY = poolInfo.getY() + poolInfo.getHeight();
                double middleX = poolInfo.getX() + poolInfo.getWidth() / 2.0;
                if (firstElement || middleX < diagramInfo.getX()) {
                    diagramInfo.setX(middleX);
                }

                if (firstElement || poolInfo.getY() < diagramInfo.getY()) {
                    diagramInfo.setY(poolInfo.getY());
                }

                if (rightX > diagramInfo.getWidth()) {
                    diagramInfo.setWidth(rightX);
                }

                if (bottomY > diagramInfo.getHeight()) {
                    diagramInfo.setHeight(bottomY);
                }
            }

            displayNode.put("pools", poolArray);
        } else {
            diagramInfo.setX(9999.0);
            diagramInfo.setY(1000.0);
        }

        Iterator i$ = pojoModel.getProcesses().iterator();

        while (i$.hasNext()) {
            Process process = (Process) i$.next();
            this.processElements(process.getFlowElements(), pojoModel, elementArray, flowArray, diagramInfo, completedElements, currentElements);
            this.processArtifacts(process.getArtifacts(), pojoModel, elementArray, flowArray, diagramInfo);
        }

        displayNode.put("elements", elementArray);
        displayNode.put("flows", flowArray);
        displayNode.put("diagramBeginX", diagramInfo.getX());
        displayNode.put("diagramBeginY", diagramInfo.getY());
        displayNode.put("diagramWidth", diagramInfo.getWidth());
        displayNode.put("diagramHeight", diagramInfo.getHeight());
        return displayNode;
    }

    protected void processElements(Collection<FlowElement> elementList, BpmnModel model, ArrayNode elementArray, ArrayNode flowArray, GraphicInfo diagramInfo, Set<String> completedElements, Set<String> currentElements) {
        Iterator i$ = elementList.iterator();

        while (true) {
            FlowElement element;
            ObjectNode elementNode;
            List flowInfo;
            label59:
            do {
                while (i$.hasNext()) {
                    element = (FlowElement) i$.next();
                    elementNode = this.objectMapper.createObjectNode();
                    if (completedElements != null) {
                        elementNode.put("completed", completedElements.contains(element.getId()));
                    }

                    if (currentElements != null) {
                        elementNode.put("current", currentElements.contains(element.getId()));
                    }

                    if (element instanceof SequenceFlow) {
                        SequenceFlow flow = (SequenceFlow) element;
                        elementNode.put("id", flow.getId());
                        elementNode.put("type", "sequenceFlow");
                        elementNode.put("sourceRef", flow.getSourceRef());
                        elementNode.put("targetRef", flow.getTargetRef());
                        flowInfo = model.getFlowLocationGraphicInfo(flow.getId());
                        continue label59;
                    }

                    elementNode.put("id", element.getId());
                    elementNode.put("name", element.getName());
                    GraphicInfo graphicInfo = model.getGraphicInfo(element.getId());
                    if (graphicInfo != null) {
                        this.fillGraphicInfo(elementNode, graphicInfo, true);
                        this.fillDiagramInfo(graphicInfo, diagramInfo);
                    }

                    String className = element.getClass().getSimpleName();
                    elementNode.put("type", className);
                    this.fillEventTypes(className, element, elementNode);
                    if (element instanceof ServiceTask) {
                        ServiceTask serviceTask = (ServiceTask) element;
                        if ("mail".equals(serviceTask.getType())) {
                            elementNode.put("taskType", "mail");
                        } else if ("camel".equals(serviceTask.getType())) {
                            elementNode.put("taskType", "camel");
                        } else if ("mule".equals(serviceTask.getType())) {
                            elementNode.put("taskType", "mule");
                        }
                    }

                    if (this.propertyMappers.containsKey(className)) {
                        elementNode.put("properties", ((InfoMapper) this.propertyMappers.get(className)).map(element));
                    }

                    elementArray.add(elementNode);
                    if (element instanceof SubProcess) {
                        SubProcess subProcess = (SubProcess) element;
                        this.processElements(subProcess.getFlowElements(), model, elementArray, flowArray, diagramInfo, completedElements, currentElements);
                        this.processArtifacts(subProcess.getArtifacts(), model, elementArray, flowArray, diagramInfo);
                    }
                }

                return;
            } while (!CollectionUtils.isNotEmpty(flowInfo));

            ArrayNode waypointArray = this.objectMapper.createArrayNode();
            Iterator j$ = flowInfo.iterator();

            while (j$.hasNext()) {
                GraphicInfo graphicInfo = (GraphicInfo) j$.next();
                ObjectNode pointNode = this.objectMapper.createObjectNode();
                this.fillGraphicInfo(pointNode, graphicInfo, false);
                waypointArray.add(pointNode);
                this.fillDiagramInfo(graphicInfo, diagramInfo);
            }

            elementNode.put("waypoints", waypointArray);
            String className = element.getClass().getSimpleName();
            if (this.propertyMappers.containsKey(className)) {
                elementNode.put("properties", ((InfoMapper) this.propertyMappers.get(className)).map(element));
            }

            flowArray.add(elementNode);
        }
    }

    protected void processArtifacts(Collection<Artifact> artifactList, BpmnModel model, ArrayNode elementArray, ArrayNode flowArray, GraphicInfo diagramInfo) {
        Iterator i$ = artifactList.iterator();

        while (i$.hasNext()) {
            Artifact artifact = (Artifact) i$.next();
            ObjectNode elementNode;
            if (artifact instanceof Association) {
                elementNode = this.objectMapper.createObjectNode();
                Association flow = (Association) artifact;
                elementNode.put("id", flow.getId());
                elementNode.put("type", "association");
                elementNode.put("sourceRef", flow.getSourceRef());
                elementNode.put("targetRef", flow.getTargetRef());
                this.fillWaypoints(flow.getId(), model, elementNode, diagramInfo);
                flowArray.add(elementNode);
            } else {
                elementNode = this.objectMapper.createObjectNode();
                elementNode.put("id", artifact.getId());
                if (artifact instanceof TextAnnotation) {
                    TextAnnotation annotation = (TextAnnotation) artifact;
                    elementNode.put("text", annotation.getText());
                }

                GraphicInfo graphicInfo = model.getGraphicInfo(artifact.getId());
                if (graphicInfo != null) {
                    this.fillGraphicInfo(elementNode, graphicInfo, true);
                    this.fillDiagramInfo(graphicInfo, diagramInfo);
                }

                String className = artifact.getClass().getSimpleName();
                elementNode.put("type", className);
                elementArray.add(elementNode);
            }
        }

    }

    protected void fillWaypoints(String id, BpmnModel model, ObjectNode elementNode, GraphicInfo diagramInfo) {
        List<GraphicInfo> flowInfo = model.getFlowLocationGraphicInfo(id);
        ArrayNode waypointArray = this.objectMapper.createArrayNode();
        Iterator i$ = flowInfo.iterator();

        while (i$.hasNext()) {
            GraphicInfo graphicInfo = (GraphicInfo) i$.next();
            ObjectNode pointNode = this.objectMapper.createObjectNode();
            this.fillGraphicInfo(pointNode, graphicInfo, false);
            waypointArray.add(pointNode);
            this.fillDiagramInfo(graphicInfo, diagramInfo);
        }

        elementNode.put("waypoints", waypointArray);
    }

    protected void fillEventTypes(String className, FlowElement element, ObjectNode elementNode) {
        if (this.eventElementTypes.contains(className)) {
            Event event = (Event) element;
            if (CollectionUtils.isNotEmpty(event.getEventDefinitions())) {
                EventDefinition eventDef = (EventDefinition) event.getEventDefinitions().get(0);
                ObjectNode eventNode = this.objectMapper.createObjectNode();
                if (eventDef instanceof TimerEventDefinition) {
                    TimerEventDefinition timerDef = (TimerEventDefinition) eventDef;
                    eventNode.put("type", "timer");
                    if (StringUtils.isNotEmpty(timerDef.getTimeCycle())) {
                        eventNode.put("timeCycle", timerDef.getTimeCycle());
                    }

                    if (StringUtils.isNotEmpty(timerDef.getTimeDate())) {
                        eventNode.put("timeDate", timerDef.getTimeDate());
                    }

                    if (StringUtils.isNotEmpty(timerDef.getTimeDuration())) {
                        eventNode.put("timeDuration", timerDef.getTimeDuration());
                    }
                } else if (eventDef instanceof ErrorEventDefinition) {
                    ErrorEventDefinition errorDef = (ErrorEventDefinition) eventDef;
                    eventNode.put("type", "ref");
                    if (StringUtils.isNotEmpty(errorDef.getErrorRef())) {
                        eventNode.put("errorCode", errorDef.getErrorRef());
                    }
                } else if (eventDef instanceof SignalEventDefinition) {
                    SignalEventDefinition signalDef = (SignalEventDefinition) eventDef;
                    eventNode.put("type", "signal");
                    if (StringUtils.isNotEmpty(signalDef.getSignalRef())) {
                        eventNode.put("signalRef", signalDef.getSignalRef());
                    }
                } else if (eventDef instanceof MessageEventDefinition) {
                    MessageEventDefinition messageDef = (MessageEventDefinition) eventDef;
                    eventNode.put("type", "message");
                    if (StringUtils.isNotEmpty(messageDef.getMessageRef())) {
                        eventNode.put("messageRef", messageDef.getMessageRef());
                    }
                }

                elementNode.put("eventDefinition", eventNode);
            }
        }

    }

    protected void fillGraphicInfo(ObjectNode elementNode, GraphicInfo graphicInfo, boolean includeWidthAndHeight) {
        this.commonFillGraphicInfo(elementNode, graphicInfo.getX(), graphicInfo.getY(), graphicInfo.getWidth(), graphicInfo.getHeight(), includeWidthAndHeight);
    }

    protected void commonFillGraphicInfo(ObjectNode elementNode, double x, double y, double width, double height, boolean includeWidthAndHeight) {
        elementNode.put("x", x);
        elementNode.put("y", y);
        if (includeWidthAndHeight) {
            elementNode.put("width", width);
            elementNode.put("height", height);
        }

    }

    protected void fillDiagramInfo(GraphicInfo graphicInfo, GraphicInfo diagramInfo) {
        double rightX = graphicInfo.getX() + graphicInfo.getWidth();
        double bottomY = graphicInfo.getY() + graphicInfo.getHeight();
        double middleX = graphicInfo.getX() + graphicInfo.getWidth() / 2.0;
        if (middleX < diagramInfo.getX()) {
            diagramInfo.setX(middleX);
        }

        if (graphicInfo.getY() < diagramInfo.getY()) {
            diagramInfo.setY(graphicInfo.getY());
        }

        if (rightX > diagramInfo.getWidth()) {
            diagramInfo.setWidth(rightX);
        }

        if (bottomY > diagramInfo.getHeight()) {
            diagramInfo.setHeight(bottomY);
        }

    }

    protected List<String> gatherCompletedFlows(Set<String> completedActivityInstances, Set<String> currentActivityinstances, BpmnModel pojoModel) {
        List<String> completedFlows = new ArrayList();
        List<String> activities = new ArrayList(completedActivityInstances);
        activities.addAll(currentActivityinstances);
        Iterator i$ = pojoModel.getMainProcess().getFlowElements().iterator();

        while (true) {
            FlowElement activity;
            int index;
            do {
                do {
                    do {
                        if (!i$.hasNext()) {
                            return completedFlows;
                        }

                        activity = (FlowElement) i$.next();
                    } while (!(activity instanceof FlowNode));

                    index = activities.indexOf(activity.getId());
                } while (index < 0);
            } while (index + 1 >= activities.size());

            List<SequenceFlow> outgoingFlows = ((FlowNode) activity).getOutgoingFlows();
            Iterator j$ = outgoingFlows.iterator();

            while (j$.hasNext()) {
                SequenceFlow flow = (SequenceFlow) j$.next();
                String destinationFlowId = flow.getTargetRef();
                if (destinationFlowId.equals(activities.get(index + 1))) {
                    completedFlows.add(flow.getId());
                }
            }
        }
    }
}
