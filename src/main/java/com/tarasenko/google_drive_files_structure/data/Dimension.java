package com.tarasenko.google_drive_files_structure.data;

public enum Dimension {
    ROWS("ROWS"),
    COLUMNS("COLUMNS");

    String value;

    Dimension(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
