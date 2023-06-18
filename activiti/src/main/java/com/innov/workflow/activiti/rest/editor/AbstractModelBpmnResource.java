package com.innov.workflow.activiti.rest.editor;

import com.innov.workflow.activiti.domain.editor.AbstractModel;
import com.innov.workflow.activiti.domain.editor.Model;
import com.innov.workflow.activiti.domain.editor.ModelHistory;
import com.innov.workflow.activiti.service.api.ModelService;
import com.innov.workflow.activiti.service.exception.BadRequestException;
import com.innov.workflow.activiti.service.exception.BaseModelerRestException;
import com.innov.workflow.activiti.service.exception.InternalServerErrorException;
import org.activiti.bpmn.model.BpmnModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;

@RestController
public class AbstractModelBpmnResource {
    private final Logger log = LoggerFactory.getLogger(AbstractModelBpmnResource.class);
    @Autowired
    protected ModelService modelService;

    public AbstractModelBpmnResource() {
    }

    public void getProcessModelBpmn20Xml(HttpServletResponse response, String processModelId) throws IOException {
        if (processModelId == null) {
            throw new BadRequestException("No process model id provided");
        } else {
            Model model = this.modelService.getModel(processModelId);
            this.generateBpmn20Xml(response, model);
        }
    }

    public void getHistoricProcessModelBpmn20Xml(HttpServletResponse response, String processModelId, String processModelHistoryId) throws IOException {
        if (processModelId == null) {
            throw new BadRequestException("No process model id provided");
        } else {
            ModelHistory historicModel = this.modelService.getModelHistory(processModelId, processModelHistoryId);
            this.generateBpmn20Xml(response, historicModel);
        }
    }

    protected void generateBpmn20Xml(HttpServletResponse response, AbstractModel model) {
        String name = model.getName().replaceAll(" ", "_");
        response.setHeader("Content-Disposition", "attachment; filename=" + name + ".bpmn20.xml");
        if (model.getModelEditorJson() != null) {
            try {
                ServletOutputStream servletOutputStream = response.getOutputStream();
                response.setContentType("application/xml");
                BpmnModel bpmnModel = this.modelService.getBpmnModel(model);
                byte[] xmlBytes = this.modelService.getBpmnXML(bpmnModel);
                BufferedInputStream in = new BufferedInputStream(new ByteArrayInputStream(xmlBytes));
                byte[] buffer = new byte[8096];

                while (true) {
                    int count = in.read(buffer);
                    if (count == -1) {
                        servletOutputStream.flush();
                        servletOutputStream.close();
                        break;
                    }

                    servletOutputStream.write(buffer, 0, count);
                }
            } catch (BaseModelerRestException var10) {
                throw var10;
            } catch (Exception var11) {
                this.log.error("Could not generate BPMN 2.0 XML", var11);
                throw new InternalServerErrorException("Could not generate BPMN 2.0 xml");
            }
        }

    }
}
