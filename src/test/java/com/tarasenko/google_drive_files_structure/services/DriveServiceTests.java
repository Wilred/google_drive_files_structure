package com.tarasenko.google_drive_files_structure.services;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
public class DriveServiceTests {

    private static final String USER_IDENTIFIER_KEY = "MY_DUMMY_USER";

    GoogleAuthorizationCodeFlow flow;
    DriveService driveService;
    SheetsService sheetsService;

    @Autowired
    DriveServiceTests(@Qualifier("initialGoogleAuthorizationCodeFlow") GoogleAuthorizationCodeFlow flow,
            DriveService driveService, SheetsService sheetsService) {
        this.flow = flow;
        this.driveService = driveService;
        this.sheetsService = sheetsService;
    }

    @Test
    public void main() throws IOException {
        Spreadsheet filesStructure = sheetsService.createSheet(flow.loadCredential(USER_IDENTIFIER_KEY), "Files Structure");
        driveService.sheetUpdateWithFilesStructure(flow.loadCredential(USER_IDENTIFIER_KEY),filesStructure.getSpreadsheetId());
    }

    @Test
    public void createSheet_FillFilesStructure_DeleteSheet() throws IOException {
        Spreadsheet spreadsheet = sheetsService.createSheet(flow.loadCredential(USER_IDENTIFIER_KEY), "File Structure");
        driveService.sheetUpdateWithFilesStructure(flow.loadCredential(USER_IDENTIFIER_KEY), spreadsheet.getSpreadsheetId());
        sheetsService.spreadsheetsDelete(flow.loadCredential(USER_IDENTIFIER_KEY), spreadsheet.getSpreadsheetId());
    }

}
