package com.tarasenko.google_drive_files_structure.data;

import com.google.api.services.drive.model.File;

import java.util.*;

public class FilesData {
    private Integer level = 0;
    private Integer position = 0;
    private File file;
    private List<FilesData> containFile = new ArrayList<>();

    public FilesData(File file) {
        this.file = file;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public List<FilesData> getContainFile() {
        return containFile;
    }

    public void setContainFile(List<FilesData> containFile) {
        this.containFile = containFile;
    }

    public FilesData addContainFile(FilesData file) {
        this.containFile.add(file);
        return file;
    }


}

