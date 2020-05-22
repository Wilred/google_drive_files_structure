package com.tarasenko.google_drive_files_structure.controllers;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.services.drive.model.File;
import com.tarasenko.google_drive_files_structure.adapters.FilesDataAdapter;
import com.tarasenko.google_drive_files_structure.data.Dimension;
import com.tarasenko.google_drive_files_structure.data.FilesData;
import com.tarasenko.google_drive_files_structure.data.ValueInputOption;
import com.tarasenko.google_drive_files_structure.services.DriveService;
import com.tarasenko.google_drive_files_structure.services.FilesService;
import com.tarasenko.google_drive_files_structure.services.SheetsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Set;

@RestController()
@RequestMapping("/drive")
public class DriveController {

    private static final String USER_IDENTIFIER_KEY = "MY_DUMMY_USER";

    private DriveService driveService;
    private GoogleAuthorizationCodeFlow flow;

    @Autowired
    DriveController(@Qualifier("initialGoogleAuthorizationCodeFlow") GoogleAuthorizationCodeFlow flow, DriveService driveService) {
        this.flow = flow;
        this.driveService = driveService;
    }

    @GetMapping("/sheetUpdateWithFilesStructure")
    public String sheetUpdateWithFilesStructure(@RequestParam String spreadsheetId) throws IOException {
        driveService.sheetUpdateWithFilesStructure(flow.loadCredential(USER_IDENTIFIER_KEY), spreadsheetId);
        return "good";
    }

    @GetMapping("/sheetCreateWithFilesStructure")
    public String sheetCreateWithFilesStructure() throws IOException {
        driveService.sheetCreateWithFilesStructure(flow.loadCredential(USER_IDENTIFIER_KEY));
        return "good";
    }
}
