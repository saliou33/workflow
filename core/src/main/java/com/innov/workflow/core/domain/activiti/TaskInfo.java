package com.innov.workflow.core.domain.activiti;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class TaskInfo {
	
	private String id;

	private String name;

	private String owner;

	private String assignee;

	private String formKey;

	private String processDefinitionId;

	private String  processInstanceId;

	private String description;

	private String executionId;

	private String category;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date claimTime;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date createTime;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date dueDate;

}
