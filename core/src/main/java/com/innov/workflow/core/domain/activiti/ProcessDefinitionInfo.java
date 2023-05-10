package com.innov.workflow.core.domain.activiti;

import lombok.Data;

@Data
public class ProcessDefinitionInfo {
	private String id;

	private String deploymentId;

	private String name;

	private String resourceName;

	private String key;

	private Integer version;

	private Integer appVersion;

	private String diagramResourceName;

}
