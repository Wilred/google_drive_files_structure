package com.tarasenko.google_drive_files_structure.data;

public enum  MimeTypes {
    audio("application/vnd.google-apps.audio"),
    document("application/vnd.google-apps.document"),
    drawing("application/vnd.google-apps.drawing"),
    file("application/vnd.google-apps.file"),
    folder("application/vnd.google-apps.folder"),
    form("application/vnd.google-apps.form"),
    fusiontable("application/vnd.google-apps.fusiontable"),
    map("application/vnd.google-apps.map"),
    photo("application/vnd.google-apps.photo"),
    presentation("application/vnd.google-apps.presentation"),
    script("application/vnd.google-apps.script"),
    site("application/vnd.google-apps.site"),
    spreadsheet("application/vnd.google-apps.spreadsheet"),
    unknown("application/vnd.google-apps.unknown"),
    video("application/vnd.google-apps.video"),
    driveSdk("application/vnd.google-apps.drive-sdk");

    private String value;

    MimeTypes(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
