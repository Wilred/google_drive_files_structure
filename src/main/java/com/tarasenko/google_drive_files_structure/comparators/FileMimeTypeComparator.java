package com.tarasenko.google_drive_files_structure.comparators;

import com.google.api.services.drive.model.File;
import com.tarasenko.google_drive_files_structure.data.MimeTypes;

import java.util.Comparator;

public class FileMimeTypeComparator implements Comparator<File> {
    @Override
    public int compare(File f1, File f2) {
        if (!f1.getMimeType().equals(MimeTypes.folder.getValue()) && f2.getMimeType().equals(MimeTypes.folder.getValue())) {
            return 1;
        } else if (f1.getMimeType().equals(MimeTypes.folder.getValue()) && !f2.getMimeType().equals(MimeTypes.folder.getValue())) {
            return -1;
        } else {
            return 0;
        }
    }
}
