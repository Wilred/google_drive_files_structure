package com.tarasenko.google_drive_files_structure.services;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.services.drive.model.File;
import com.tarasenko.google_drive_files_structure.data.FilesData;
import com.tarasenko.google_drive_files_structure.data.MimeTypes;

import java.io.IOException;
import java.util.List;

public interface FilesService {

    List<File> searchFiles(Credential credential,
                                  String googleFolderIdParent,
                                  MimeTypes mimeType,
                                  Boolean includeTrashed,
                                  Boolean sharedWithMe ) throws IOException;

    List<File> sort(List<File> fileList);

    List<FilesData> group(Credential credential, List<File> files);

    void outFiles(List<FilesData> gFiles);

    String findMainDriveId(Credential credential);
}
