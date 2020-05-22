package com.tarasenko.google_drive_files_structure.services;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.tarasenko.google_drive_files_structure.data.Dimension;
import com.tarasenko.google_drive_files_structure.data.SheetData;
import com.tarasenko.google_drive_files_structure.data.ValueInputOption;
import com.tarasenko.google_drive_files_structure.services.impl.SheetsServiceImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.Arrays;

@SpringBootTest
public class SheetUtilsTests {

    private static final String USER_IDENTIFIER_KEY = "MY_DUMMY_USER";

    @Autowired
    SheetsService sheetsService;

    private SheetData sheetData;
    private GoogleAuthorizationCodeFlow flow;

    @BeforeEach
    void initialize(@Qualifier("initialGoogleAuthorizationCodeFlow") GoogleAuthorizationCodeFlow flow) {
        this.sheetData = new SheetData();
        this.sheetData.setValue(Arrays.asList(
                Arrays.asList("Hello", "my name", "is", "Andrey"),
                Arrays.asList("1", "2", "3", "4")
        ));
        this.sheetData.setDimension(Dimension.COLUMNS);
        this.sheetData.setValueInputOption(ValueInputOption.USER_ENTERED);
        this.flow = flow;
    }

    @Test
    public void main() throws IOException {
        Spreadsheet spreadsheet = sheetsService.createSheet(flow.loadCredential(USER_IDENTIFIER_KEY), "Sheet1");
        sheetsService.valuesUpdate(flow.loadCredential(USER_IDENTIFIER_KEY), spreadsheet.getSpreadsheetId(), null, this.sheetData);
        sheetsService.valuesGet(flow.loadCredential(USER_IDENTIFIER_KEY), spreadsheet.getSpreadsheetId(), "A1:A2");
        sheetsService.valuesBatchUpdate(flow.loadCredential(USER_IDENTIFIER_KEY), spreadsheet.getSpreadsheetId(), null, sheetData);
        sheetsService.valuesBatchGet(flow.loadCredential(USER_IDENTIFIER_KEY), spreadsheet.getSpreadsheetId(), null);
    }



}
