package com.tarasenko.google_drive_files_structure.data;

public enum ValueInputOption {
    RAW("RAW"),
    USER_ENTERED("USER_ENTERED");

    String value;

    ValueInputOption(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
