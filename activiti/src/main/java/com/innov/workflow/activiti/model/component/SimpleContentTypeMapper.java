package com.innov.workflow.activiti.model.component;

import com.innov.workflow.activiti.domain.runtime.RelatedContent;
import org.springframework.stereotype.Component;

@Component
public class SimpleContentTypeMapper {
    public static final String TYPE_WORD = "word";
    public static final String TYPE_EXCEL = "excel";
    public static final String TYPE_POWERPOINT = "powerpoint";
    public static final String TYPE_PDF = "pdf";
    public static final String TYPE_IMAGE = "image";
    public static final String TYPE_FOLDER = "folder";
    public static final String TYPE_GENERIC = "content";
    public static final String MIME_TYPE_DOC = "application/msword";
    public static final String MIME_TYPE_DOCX = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
    public static final String MIME_TYPE_GOOGLE_DOC = "application/vnd.google-apps.document";
    public static final String MIME_TYPE_XLS = "application/vnd.ms-excel";
    public static final String MIME_TYPE_XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    public static final String MIME_TYPE_GOOGLE_PRESENTATION = "application/vnd.google-apps.presentation";
    public static final String MIME_TYPE_GOOGLE_FOLDER = "application/vnd.google-apps.folder";
    public static final String MIME_TYPE_GOOGLE_DRAWING = "application/vnd.google-apps.drawing";
    public static final String MIME_TYPE_GOOGLE_SHEET = "application/vnd.google-apps.spreadsheet";
    public static final String MIME_TYPE_PPT = "application/vnd.ms-powerpoint";
    public static final String MIME_TYPE_PPTX = "application/vnd.openxmlformats-officedocument.presentationml.presentation";
    public static final String MIME_TYPE_PDF = "application/pdf";
    public static final String MIME_TYPE_IMAGE_PNG = "image/png";
    public static final String PREFIX_MIME_TYPE_IMAGE = "image/";

    public SimpleContentTypeMapper() {
    }

    public String getSimpleTypeForMimeType(String mimeType) {
        String result = null;
        if (mimeType != null) {
            if (!"application/msword".equals(mimeType) && !"application/vnd.openxmlformats-officedocument.wordprocessingml.document".equals(mimeType) && !"application/vnd.google-apps.document".equals(mimeType)) {
                if ("application/vnd.ms-excel".equals(mimeType) || "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".equals(mimeType) || "application/vnd.google-apps.spreadsheet".equals(mimeType)) {
                    result = "excel";
                }
            } else {
                result = "word";
            }

            if (!"application/vnd.ms-powerpoint".equals(mimeType) && !"application/vnd.openxmlformats-officedocument.presentationml.presentation".equals(mimeType) && !"application/vnd.google-apps.presentation".equals(mimeType)) {
                if ("application/pdf".equals(mimeType)) {
                    result = "pdf";
                } else if (!"application/vnd.google-apps.drawing".equals(mimeType) && !mimeType.startsWith("image/")) {
                    if ("application/vnd.google-apps.folder".equals(mimeType)) {
                        return "folder";
                    }
                } else {
                    result = "image";
                }
            } else {
                result = "powerpoint";
            }
        }

        if (result == null) {
            result = "content";
        }

        return result;
    }

    public String getSimpleType(RelatedContent relatedContent) {
        return this.getSimpleTypeForMimeType(relatedContent.getMimeType());
    }
}
