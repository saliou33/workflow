package com.innov.workflow.activiti.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.springframework.stereotype.Service;


@Service
@Slf4j
@RequiredArgsConstructor
public class CustomActivitiEventListener implements ActivitiEventListener {


//    protected BpmnJsonConverter bpmnJsonConverter = new BpmnJsonConverter();
//    protected BpmnXMLConverter bpmnXMLConverter = new BpmnXMLConverter();
//
//    @Autowired
//    protected ObjectMapper objectMapper;
//
//    private final FormRepositoryService formRepositoryService;
//
//    private final ModelRepository modelRepository;


    @Override
    public void onEvent(ActivitiEvent event) {
        log.info("Received event: {}", event.getType());

        switch (event.getType()) {
            case PROCESS_STARTED:
                log.info("Process Started: {}", event.getProcessInstanceId());

                break;
            case PROCESS_COMPLETED:
                log.info("Process completed: {}", event.getProcessInstanceId());
                break;

        }
    }

//    private void createFormDeployment(String processDefinitionId) {
//
//
//        // Get the BPMN model for the process definition
//        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinition.getId());
//
//        // Retrieve form keys and properties from the BPMN model
//        ArrayList<String> formKeys = new ArrayList<>();
//        for (FlowElement flowElement : bpmnModel.getMainProcess().getFlowElements()) {
//            if (flowElement instanceof UserTask) {
//                UserTask userTask = (UserTask) flowElement;
//                formKeys.add(userTask.getFormKey());
//            }
//        }
//
//        List<FormDefinition> formDefinitions = new ArrayList<>();
//
//        for (String formKey: formKeys) {
//            FormDefinition formDefinition = formRepositoryService.getFormDefinitionByKey(formKey);
//
//            if (formDefinition == null) {
//                Model model = modelRepository.findModelsByKeyAndType(formKey, 2).get(0);
//
//                formDefinition = new FormDefinition();
//                formDefinition.setKey(model.getKey());
//                formDefinition.setDescription(model.getDescription());
//                formDefinition.setVersion(model.getVersion());
//
//                FormDeployment formDeployment = formRepositoryService.createDeployment()
//                        .name(model.getName())
//                        .parentDeploymentId(processDefinitionId)
//                        .addFormDefinition("form-" + model.getKey() + ".form", formDefinition)
//                        .deploy();
//
//                log.info("Form Deployed : {}", formDeployment.getName());
//            }
//        }
//    }
//
//    public BpmnModel getBpmnModel(AbstractModel model) {
//        BpmnModel bpmnModel = null;
//
//        try {
//            Map<String, Model> formMap = new HashMap();
//            Map<String, Model> decisionTableMap = new HashMap();
//            List<Model> referencedModels = this.modelRepository.findModelsByParentModelId(model.getId());
//            Iterator i$ = referencedModels.iterator();
//
//            while(i$.hasNext()) {
//                Model childModel = (Model)i$.next();
//                if (2 == childModel.getModelType()) {
//                    formMap.put(childModel.getId(), childModel);
//                } else if (4 == childModel.getModelType()) {
//                    decisionTableMap.put(childModel.getId(), childModel);
//                }
//            }
//
//            bpmnModel = this.getBpmnModel(model, formMap, decisionTableMap);
//            return bpmnModel;
//        } catch (Exception var8) {
//            this.log.error("Could not generate BPMN 2.0 model for " + model.getId(), var8);
//            throw new InternalServerErrorException("Could not generate BPMN 2.0 model");
//        }
//    }
//
//    public BpmnModel getBpmnModel(AbstractModel model, Map<String, Model> formMap, Map<String, Model> decisionTableMap) {
//        try {
//            ObjectNode editorJsonNode = (ObjectNode)this.objectMapper.readTree(model.getModelEditorJson());
//            Map<String, String> formKeyMap = new HashMap();
//            Iterator i$ = formMap.values().iterator();
//
//            while(i$.hasNext()) {
//                Model formModel = (Model)i$.next();
//                formKeyMap.put(formModel.getId(), formModel.getKey());
//            }
//
//            Map<String, String> decisionTableKeyMap = new HashMap();
//            Iterator j$ = decisionTableMap.values().iterator();
//
//            while(j$.hasNext()) {
//                Model decisionTableModel = (Model)j$.next();
//                decisionTableKeyMap.put(decisionTableModel.getId(), decisionTableModel.getKey());
//            }
//
//            return this.bpmnJsonConverter.convertToBpmnModel(editorJsonNode, formKeyMap, decisionTableKeyMap);
//        } catch (Exception var9) {
//            this.log.error("Could not generate BPMN 2.0 model for " + model.getId(), var9);
//            throw new InternalServerErrorException("Could not generate BPMN 2.0 model");
//        }
//    }


    @Override
    public boolean isFailOnException() {
        return false;
    }

}
