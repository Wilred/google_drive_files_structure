package com.tarasenko.google_drive_files_structure.comparators;

import com.google.api.services.drive.model.File;

import java.util.Comparator;

public class FileNameComparator implements Comparator<File> {
    @Override
    public int compare(File f1, File f2) {
        return f1.getName().compareTo(f2.getName());
    }
}
