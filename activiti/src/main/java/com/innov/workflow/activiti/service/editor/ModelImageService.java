package com.innov.workflow.activiti.service.editor;

import com.activiti.image.ImageGenerator;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.innov.workflow.activiti.domain.editor.Model;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.*;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

@Service
public class ModelImageService {
    private static float THUMBNAIL_WIDTH = 300.0F;
    private final Logger log = LoggerFactory.getLogger(ModelImageService.class);
    protected BpmnJsonConverter bpmnJsonConverter = new BpmnJsonConverter();

    public ModelImageService() {
    }

    public void generateThumbnailImage(Model model, ObjectNode editorJsonNode) {
        try {
            BpmnModel bpmnModel = this.bpmnJsonConverter.convertToBpmnModel(editorJsonNode);
            double scaleFactor = 1.0;
            GraphicInfo diagramInfo = this.calculateDiagramSize(bpmnModel);
            if (diagramInfo.getWidth() > (double) THUMBNAIL_WIDTH) {
                scaleFactor = diagramInfo.getWidth() / (double) THUMBNAIL_WIDTH;
                this.scaleDiagram(bpmnModel, scaleFactor);
            }

            BufferedImage modelImage = ImageGenerator.createImage(bpmnModel, scaleFactor);

            if (modelImage != null) {
                byte[] thumbnailBytes = ImageGenerator.createByteArrayForImage(modelImage, "png");
                model.setThumbnail(thumbnailBytes);
            }
        } catch (Exception var9) {
            this.log.error("Error creating thumbnail image " + model.getId(), var9);
        }

    }

    protected GraphicInfo calculateDiagramSize(BpmnModel bpmnModel) {
        GraphicInfo diagramInfo = new GraphicInfo();
        Iterator i$ = bpmnModel.getPools().iterator();

        while (i$.hasNext()) {
            Pool pool = (Pool) i$.next();
            GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(pool.getId());
            double elementMaxX = graphicInfo.getX() + graphicInfo.getWidth();
            double elementMaxY = graphicInfo.getY() + graphicInfo.getHeight();
            if (elementMaxX > diagramInfo.getWidth()) {
                diagramInfo.setWidth(elementMaxX);
            }

            if (elementMaxY > diagramInfo.getHeight()) {
                diagramInfo.setHeight(elementMaxY);
            }
        }

        i$ = bpmnModel.getProcesses().iterator();

        while (i$.hasNext()) {
            Process process = (Process) i$.next();
            this.calculateWidthForFlowElements(process.getFlowElements(), bpmnModel, diagramInfo);
            this.calculateWidthForArtifacts(process.getArtifacts(), bpmnModel, diagramInfo);
        }

        return diagramInfo;
    }

    protected void scaleDiagram(BpmnModel bpmnModel, double scaleFactor) {
        Iterator i$ = bpmnModel.getPools().iterator();

        while (i$.hasNext()) {
            Pool pool = (Pool) i$.next();
            GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(pool.getId());
            this.scaleGraphicInfo(graphicInfo, scaleFactor);
        }

        i$ = bpmnModel.getProcesses().iterator();

        while (i$.hasNext()) {
            Process process = (Process) i$.next();
            this.scaleFlowElements(process.getFlowElements(), bpmnModel, scaleFactor);
            this.scaleArtifacts(process.getArtifacts(), bpmnModel, scaleFactor);
            Iterator j$ = process.getLanes().iterator();

            while (j$.hasNext()) {
                Lane lane = (Lane) j$.next();
                this.scaleGraphicInfo(bpmnModel.getGraphicInfo(lane.getId()), scaleFactor);
            }
        }

    }

    protected void calculateWidthForFlowElements(Collection<FlowElement> elementList, BpmnModel bpmnModel, GraphicInfo diagramInfo) {
        ArrayList graphicInfoList;
        for (Iterator i$ = elementList.iterator(); i$.hasNext(); this.processGraphicInfoList(graphicInfoList, diagramInfo)) {
            FlowElement flowElement = (FlowElement) i$.next();
            graphicInfoList = new ArrayList();
            if (flowElement instanceof SequenceFlow) {
                List<GraphicInfo> flowGraphics = bpmnModel.getFlowLocationGraphicInfo(flowElement.getId());
                if (flowGraphics != null && flowGraphics.size() > 0) {
                    graphicInfoList.addAll(flowGraphics);
                }
            } else {
                GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowElement.getId());
                if (graphicInfo != null) {
                    graphicInfoList.add(graphicInfo);
                }
            }
        }

    }

    protected void calculateWidthForArtifacts(Collection<Artifact> artifactList, BpmnModel bpmnModel, GraphicInfo diagramInfo) {
        ArrayList graphicInfoList;
        for (Iterator i$ = artifactList.iterator(); i$.hasNext(); this.processGraphicInfoList(graphicInfoList, diagramInfo)) {
            Artifact artifact = (Artifact) i$.next();
            graphicInfoList = new ArrayList();
            if (artifact instanceof Association) {
                graphicInfoList.addAll(bpmnModel.getFlowLocationGraphicInfo(artifact.getId()));
            } else {
                graphicInfoList.add(bpmnModel.getGraphicInfo(artifact.getId()));
            }
        }

    }

    protected void processGraphicInfoList(List<GraphicInfo> graphicInfoList, GraphicInfo diagramInfo) {
        Iterator i$ = graphicInfoList.iterator();

        while (i$.hasNext()) {
            GraphicInfo graphicInfo = (GraphicInfo) i$.next();
            double elementMaxX = graphicInfo.getX() + graphicInfo.getWidth();
            double elementMaxY = graphicInfo.getY() + graphicInfo.getHeight();
            if (elementMaxX > diagramInfo.getWidth()) {
                diagramInfo.setWidth(elementMaxX);
            }

            if (elementMaxY > diagramInfo.getHeight()) {
                diagramInfo.setHeight(elementMaxY);
            }
        }

    }

    protected void scaleFlowElements(Collection<FlowElement> elementList, BpmnModel bpmnModel, double scaleFactor) {
        Iterator i$ = elementList.iterator();

        while (i$.hasNext()) {
            FlowElement flowElement = (FlowElement) i$.next();
            List<GraphicInfo> graphicInfoList = new ArrayList();
            if (flowElement instanceof SequenceFlow) {
                List<GraphicInfo> flowList = bpmnModel.getFlowLocationGraphicInfo(flowElement.getId());
                if (flowList != null) {
                    graphicInfoList.addAll(flowList);
                }
            } else {
                graphicInfoList.add(bpmnModel.getGraphicInfo(flowElement.getId()));
            }

            this.scaleGraphicInfoList(graphicInfoList, scaleFactor);
            if (flowElement instanceof SubProcess) {
                SubProcess subProcess = (SubProcess) flowElement;
                this.scaleFlowElements(subProcess.getFlowElements(), bpmnModel, scaleFactor);
            }
        }

    }

    protected void scaleArtifacts(Collection<Artifact> artifactList, BpmnModel bpmnModel, double scaleFactor) {
        ArrayList graphicInfoList;
        for (Iterator i$ = artifactList.iterator(); i$.hasNext(); this.scaleGraphicInfoList(graphicInfoList, scaleFactor)) {
            Artifact artifact = (Artifact) i$.next();
            graphicInfoList = new ArrayList();
            if (artifact instanceof Association) {
                List<GraphicInfo> flowList = bpmnModel.getFlowLocationGraphicInfo(artifact.getId());
                if (flowList != null) {
                    graphicInfoList.addAll(flowList);
                }
            } else {
                graphicInfoList.add(bpmnModel.getGraphicInfo(artifact.getId()));
            }
        }

    }

    protected void scaleGraphicInfoList(List<GraphicInfo> graphicInfoList, double scaleFactor) {
        Iterator i$ = graphicInfoList.iterator();

        while (i$.hasNext()) {
            GraphicInfo graphicInfo = (GraphicInfo) i$.next();
            this.scaleGraphicInfo(graphicInfo, scaleFactor);
        }

    }

    protected void scaleGraphicInfo(GraphicInfo graphicInfo, double scaleFactor) {
        graphicInfo.setX(graphicInfo.getX() / scaleFactor);
        graphicInfo.setY(graphicInfo.getY() / scaleFactor);
        graphicInfo.setWidth(graphicInfo.getWidth() / scaleFactor);
        graphicInfo.setHeight(graphicInfo.getHeight() / scaleFactor);
    }
}
