package com.tarasenko.google_drive_files_structure.controllers;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.services.drive.model.File;
import com.tarasenko.google_drive_files_structure.services.FilesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/files")
public class FilesController {

    private static final String USER_IDENTIFIER_KEY = "MY_DUMMY_USER";

    FilesService filesService;
    GoogleAuthorizationCodeFlow flow;

    @Autowired
    FilesController(@Qualifier("initialGoogleAuthorizationCodeFlow") GoogleAuthorizationCodeFlow flow,
                    @Qualifier("FilesServiceImpl1") FilesService filesService) {
        this.filesService = filesService;
        this.flow = flow;
    }

    @GetMapping("/searchFiles")
    public List<File> searchFiles() throws IOException {
        return filesService.searchFiles(flow.loadCredential(USER_IDENTIFIER_KEY), null, null, false, true);
    }
}
