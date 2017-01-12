package com.example.administrator.downloadtest.model;

/**
 * Created by Administrator on 2016/11/25 0025.
 */

public class DownLoadBean {

    //下载状态
    public static final int DOWNLOCADBEAN_STATUS_NOMAL = -1;
    public static final int DOWNLOCADBEAN_STATUS_START = 0;
    public static final int DOWNLOCADBEAN_STATUS_PROGRESS = 1;
    public static final int DOWNLOCADBEAN_STATUS_FINISH = 2;
    public static final int DOWNLOCADBEAN_STATUS_CANCEL = 3;
    public static final int DOWNLOCADBEAN_STATUS_ERROR = 4;
    public static final int DOWNLOCADBEAN_STATUS_WARIT = 5;


    private int id;
    private String url;
    private String fileFolder;
    private String filename;
    private String msg;
    private int status;
    private int downProgress;
    private long allCount;
    private long beforeLength;
    private long fileCount;
    private String filePath;

//    public static final String COLUMN_ID = "_id";
//    public static final String COLUMN_URL = "url";
//    public static final String COLUMN_FILEFOLDER = "fileFolder";
//    public static final String COLUMN_FILENAME = "fileName";
//    public static final String COLUMN_MSG = "msg";
//    public static final String COLUMN_STATUS = "status";
//    public static final String COLUMN_DOWNPROGRESS = "downProgress";
//    public static final String COLUMN_ALLCOUNT = "allCount";
//    public static final String COLUMN_BEFORELENGTH = "beforeLength";
//    public static final String COLUMN_FILECOUNT = "fileCount";
//    public static final String COLUMN_FILEPATH = "filePath";


    public DownLoadBean() {

    }

    public DownLoadBean(int id, String url, String fileFolder, String filename) {
        this.url = url;
        this.id = id;
        this.fileFolder = fileFolder;
        this.filename = filename;
        this.status = DOWNLOCADBEAN_STATUS_NOMAL;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFileFolder() {
        return fileFolder;
    }

    public void setFileFolder(String fileFolder) {
        this.fileFolder = fileFolder;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getDownProgress() {
        return downProgress;
    }

    public void setDownProgress(int downProgress) {
        this.downProgress = downProgress;
    }

    public long getAllCount() {
        return allCount;
    }

    public void setAllCount(long allCount) {
        this.allCount = allCount;
    }

    public long getBeforeLength() {
        return beforeLength;
    }

    public void setBeforeLength(long beforeLength) {
        this.beforeLength = beforeLength;
    }

    public long getFileCount() {
        return fileCount;
    }

    public void setFileCount(long fileCount) {
        this.fileCount = fileCount;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public String toString() {
        return "DownLoadBean{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", fileFolder='" + fileFolder + '\'' +
                ", filename='" + filename + '\'' +
                ", msg='" + msg + '\'' +
                ", status=" + status +
                ", downProgress=" + downProgress +
                ", allCount=" + allCount +
                ", beforeLength=" + beforeLength +
                ", fileCount=" + fileCount +
                ", filePath='" + filePath + '\'' +
                '}';
    }
}
