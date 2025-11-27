package com.example.demo.response;

public class FileUploadResponse {
    private String fileName;
    private String savedName;
    private String path;

    public FileUploadResponse(String fileName, String savedName, String path) {
        this.fileName = fileName;
        this.savedName = savedName;
        this.path = path;
    }

    // Getters and setters

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getSavedName() {
        return savedName;
    }

    public void setSavedName(String savedName) {
        this.savedName = savedName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "FileUploadResponse{" +
                "fileName='" + fileName + '\'' +
                ", savedName='" + savedName + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}