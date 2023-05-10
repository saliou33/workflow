package com.innov.workflow.app.controller.activiti;

import com.innov.workflow.app.dto.PaginationDTO;
import com.innov.workflow.app.service.ActivitiService;
import com.innov.workflow.core.domain.ApiResponse;
import com.innov.workflow.core.exception.ApiException;
import com.innov.workflow.core.service.UserService;
import com.innov.workflow.core.utils.StringUtils;
import com.innov.workflow.core.domain.activiti.TaskInfo;
import lombok.AllArgsConstructor;
import org.activiti.bpmn.model.FormProperty;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/activiti/task/")
@AllArgsConstructor
public class TaskController {

    private final RuntimeService runtimeService;

    private final TaskService taskService;

    private final UserService userService;

    private final HistoryService historyService;

    private final ActivitiService activitiService;

    @PostMapping("/all")
    public ResponseEntity getTasks(PaginationDTO page, @RequestBody TaskInfo param)
    {
        TaskQuery condition = taskService.createTaskQuery();
        if (StringUtils.isNotEmpty(param.getProcessDefinitionId())) {
            condition.processDefinitionId(param.getProcessDefinitionId());
        }
        if (StringUtils.isNotEmpty(param.getProcessInstanceId())) {
            condition.processInstanceId(param.getProcessInstanceId());
        }
        int total = condition.active().orderByTaskCreateTime().desc().list().size();

        List<Task> taskList = condition.active().orderByTaskCreateTime().desc().listPage(page.getStart(), page.getPageSize());
        List<TaskInfo> tasks = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        taskList.stream().forEach(a->{
            tasks.add(activitiService.taskMap(a));
        });


        return ApiResponse.success(tasks);
    }

    @GetMapping("/{taskId}/form")
    public ResponseEntity getFormProperties(@PathVariable String taskId) {
        List<FormProperty> formProperties = activitiService.getFormProperties(taskId);

        return ApiResponse.success(formProperties);
    }


    @RequestMapping(value = "/complete/{taskId}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity completeTask(String username, @PathVariable("taskId") String taskId, @RequestBody(required=false) Map<String, Object> variables) {

        if(!userService.existsByUsername(username)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "user not found with username " + username);
        }

        taskService.setAssignee(taskId, username);

        String processInstanceId = taskService.createTaskQuery().taskId(taskId).singleResult().getProcessInstanceId();
        if (variables == null) {
            taskService.complete(taskId);
        } else {
            if (variables.get("comment") != null) {
                taskService.addComment(taskId, processInstanceId, (String) variables.get("comment"));
                variables.remove("comment");
            }
            taskService.complete(taskId, variables);
        }
        return ApiResponse.success("Task completed");
    }

//    @GetMapping(value = "/history/{taskId}")
//    public ResponseEntity history(@PathVariable String taskId) {
//        String processInstanceId = taskService.createTaskQuery().taskId(taskId).singleResult().getProcessInstanceId();
//        List<HistoricActivityInstance> history = historyService.createHistoricActivityInstanceQuery().processInstanceId(processInstanceId).activityType("userTask").orderByHistoricActivityInstanceStartTime().asc().list();
//        List<TaskInfo> infos  = new ArrayList<>();
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        history.stream().forEach(h->{
//            TaskInfo info = new TaskInfo();
//            info.setProcessInstanceId(h.getProcessInstanceId());
//            info.setStartTime(sdf.format(h.getStartTime()));
//            if (h.getEndTime() != null) {
//                info.setEndTime(sdf.format(h.getEndTime()));
//            }
//            info.setAssignee(h.getAssignee());
//            info.setTaskName(h.getActivityName());
//            List<Comment> comments = taskService.getTaskComments(h.getTaskId());
//            if (comments.size() > 0) {
//                info.setComment(comments.get(0).getFullMessage());
//            }
//            infos.add(info);
//        });
//
//        return  ApiResponse.success(infos);
//    }
}
