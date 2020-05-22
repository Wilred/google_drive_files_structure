package com.tarasenko.google_drive_files_structure.controllers;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.tarasenko.google_drive_files_structure.services.FilesService;
import com.tarasenko.google_drive_files_structure.services.SheetsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.server.PathParam;
import java.io.IOException;

@RestController
public class SheetsController {

    private static final String USER_IDENTIFIER_KEY = "MY_DUMMY_USER";

    private GoogleAuthorizationCodeFlow flow;
    private SheetsService service;

    @Autowired
    SheetsController(@Qualifier("initialGoogleAuthorizationCodeFlow") GoogleAuthorizationCodeFlow flow, SheetsService service) {
        this.flow = flow;
        this.service = service;
    }

    @GetMapping("valuesUpdate")
    public String valuesUpdate(@RequestParam String spreadsheetId) throws IOException {
        this.service.valuesUpdate(flow.loadCredential(USER_IDENTIFIER_KEY), spreadsheetId, null, null);
        return "good";
    }
}
