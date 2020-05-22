package com.tarasenko.google_drive_files_structure.services;

import com.google.api.client.auth.oauth2.Credential;

import java.io.IOException;

public interface DriveService {
    void sheetUpdateWithFilesStructure(Credential credential, String spreadsSheetId) throws IOException;
    void sheetCreateWithFilesStructure(Credential credential) throws IOException;
}
