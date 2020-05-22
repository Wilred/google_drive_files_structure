package com.tarasenko.google_drive_files_structure.services;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.Request;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.tarasenko.google_drive_files_structure.data.Dimension;
import com.tarasenko.google_drive_files_structure.data.Indices;
import com.tarasenko.google_drive_files_structure.data.SheetData;

import java.util.List;

public interface SheetsService {
    Spreadsheet createSheet(Credential credential, String title);

    void valuesGet(Credential credential, String spreadsheetId, String range);

    void valuesBatchGet(Credential credential, String spreadsheetId, List<String> range);

    void valuesUpdate(Credential credential, String spreadsheetId, String range, SheetData values);

    void valuesBatchUpdate(Credential credential, String spreadsheetId, String range, SheetData values);

    void spreadsheetsUpdate(Credential credential, String spreadsheetId, Dimension dimension, int startIndex, int endIndex);

    void spreadsheetsUpdate(Credential credential, String spreadsheetId, Dimension dimension, List<Indices> indices);

    void spreadsheetsDelete(Credential credential, String spreadsheetId);
}
