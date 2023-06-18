package com.innov.workflow.core.utils.file;

import com.innov.workflow.core.constant.Constants;
import com.innov.workflow.core.utils.DateUtils;
import com.innov.workflow.core.utils.StringUtils;
import com.innov.workflow.core.utils.uuid.IdUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;


public class FileUtils {
    public static String FILENAME_PATTERN = "[a-zA-Z0-9_\\-\\|\\.\\u4e00-\\u9fa5]+";


    public static void writeBytes(String filePath, OutputStream os) throws IOException {
        FileInputStream fis = null;
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                throw new FileNotFoundException(filePath);
            }
            fis = new FileInputStream(file);
            byte[] b = new byte[1024];
            int length;
            while ((length = fis.read(b)) > 0) {
                os.write(b, 0, length);
            }
        } catch (IOException e) {
            throw e;
        } finally {
            IOUtils.close(os);
            IOUtils.close(fis);
        }
    }


    public static String writeImportBytes(byte[] data) throws IOException {
        return writeBytes(data, Constants.IMPORT_PATH);
    }


    public static String writeBytes(byte[] data, String uploadDir) throws IOException {
        FileOutputStream fos = null;
        String pathName = "";
        try {
            String extension = getFileExtendName(data);
            pathName = DateUtils.datePath() + "/" + IdUtils.fastUUID() + "." + extension;
            File file = FileUploadUtils.getAbsoluteFile(uploadDir, pathName);
            fos = new FileOutputStream(file);
            fos.write(data);
        } finally {
            IOUtils.close(fos);
        }
        return FileUploadUtils.getPathFileName(uploadDir, pathName);
    }


    public static boolean deleteFile(String filePath) {
        boolean flag = false;
        File file = new File(filePath);

        if (file.isFile() && file.exists()) {
            file.delete();
            flag = true;
        }
        return flag;
    }


    public static boolean isValidFilename(String filename) {
        return filename.matches(FILENAME_PATTERN);
    }


    public static boolean checkAllowDownload(String resource) {

        if (StringUtils.contains(resource, "..")) {
            return false;
        }


        return ArrayUtils.contains(MimeTypeUtils.DEFAULT_ALLOWED_EXTENSION, FileTypeUtils.getFileType(resource));
    }


    public static String setFileDownloadHeader(HttpServletRequest request, String fileName) throws UnsupportedEncodingException {
        final String agent = request.getHeader("USER-AGENT");
        String filename = fileName;
        if (agent.contains("MSIE")) {

            filename = URLEncoder.encode(filename, "utf-8");
            filename = filename.replace("+", " ");
        } else if (agent.contains("Firefox")) {

            filename = new String(fileName.getBytes(), "ISO8859-1");
        } else if (agent.contains("Chrome")) {

            filename = URLEncoder.encode(filename, "utf-8");
        } else {

            filename = URLEncoder.encode(filename, "utf-8");
        }
        return filename;
    }


    public static void setAttachmentResponseHeader(HttpServletResponse response, String realFileName) throws UnsupportedEncodingException {
        String percentEncodedFileName = percentEncode(realFileName);

        String contentDispositionValue = "attachment; filename=" +
                percentEncodedFileName +
                ";" +
                "filename*=" +
                "utf-8''" +
                percentEncodedFileName;

        response.setHeader("Content-disposition", contentDispositionValue);
    }


    public static String percentEncode(String s) throws UnsupportedEncodingException {
        String encode = URLEncoder.encode(s, StandardCharsets.UTF_8.toString());
        return encode.replaceAll("\\+", "%20");
    }


    public static String getFileExtendName(byte[] photoByte) {
        String strFileExtendName = "jpg";
        if ((photoByte[0] == 71) && (photoByte[1] == 73) && (photoByte[2] == 70) && (photoByte[3] == 56)
                && ((photoByte[4] == 55) || (photoByte[4] == 57)) && (photoByte[5] == 97)) {
            strFileExtendName = "gif";
        } else if ((photoByte[6] == 74) && (photoByte[7] == 70) && (photoByte[8] == 73) && (photoByte[9] == 70)) {
            strFileExtendName = "jpg";
        } else if ((photoByte[0] == 66) && (photoByte[1] == 77)) {
            strFileExtendName = "bmp";
        } else if ((photoByte[1] == 80) && (photoByte[2] == 78) && (photoByte[3] == 71)) {
            strFileExtendName = "png";
        }
        return strFileExtendName;
    }


    public static String getName(String fileName) {
        if (fileName == null) {
            return null;
        }
        int lastUnixPos = fileName.lastIndexOf('/');
        int lastWindowsPos = fileName.lastIndexOf('\\');
        int index = Math.max(lastUnixPos, lastWindowsPos);
        return fileName.substring(index + 1);
    }
}
