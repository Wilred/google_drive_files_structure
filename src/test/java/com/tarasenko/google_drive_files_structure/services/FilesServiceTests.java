package com.tarasenko.google_drive_files_structure.services;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.services.drive.model.File;
import com.tarasenko.google_drive_files_structure.data.FilesData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@SpringBootTest
public class FilesServiceTests {

    private static final String USER_IDENTIFIER_KEY = "MY_DUMMY_USER";

    @Autowired
    @Qualifier(value = "FilesServiceImpl2")
    FilesService filesService;

    private GoogleAuthorizationCodeFlow flow;

    FilesServiceTests(@Qualifier("initialGoogleAuthorizationCodeFlow") GoogleAuthorizationCodeFlow flow) {
        this.flow = flow;
    }

    @Test
    public void main() throws IOException {
        List<File> files = filesService.searchFiles(flow.loadCredential(USER_IDENTIFIER_KEY), null, null, false, true);
        List<FilesData> gFiles = filesService.group(flow.loadCredential(USER_IDENTIFIER_KEY), files);
        filesService.outFiles(gFiles);
    }

    @Test
    public void getRoot() throws IOException {
        String result = filesService.findMainDriveId(flow.loadCredential(USER_IDENTIFIER_KEY));
        System.out.println(result);
    }
}
