package com.tarasenko.google_drive_files_structure.controllers;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.DriveScopes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;

@Controller
public class HomepageController {

    private static final String USER_IDENTIFIER_KEY = "MY_DUMMY_USER";
    public static final String APPLICATION_NAME = "google drive files structure";

    @Value("${google.oauth.callback.url}")
    private String CALLBACK_URL;

    private GoogleAuthorizationCodeFlow flow;

    @Autowired
    HomepageController(@Qualifier("initialGoogleAuthorizationCodeFlow") GoogleAuthorizationCodeFlow flow) {
        this.flow = flow;
    }

    @GetMapping(value = {"/"})
    public String showHomePage(Model model) throws Exception {
        boolean isUserAuthenticated = false;

        Credential credential = flow.loadCredential(USER_IDENTIFIER_KEY);
        if (credential != null) {
            boolean tokenValid = credential.refreshToken();
            if (tokenValid) {
                isUserAuthenticated = true;
            }
        }
//        return isUserAuthenticated ? new Document("dashboard.html").html() : new Document("index.html").html();
        return isUserAuthenticated ? "dashboard.html" : "index.html";
    }

    @GetMapping(value = {"/googlesignin"})
    public void doGoogleSignIn(HttpServletResponse response) throws Exception {
        GoogleAuthorizationCodeRequestUrl url = flow.newAuthorizationUrl();
        String redirectURL = url.setRedirectUri(CALLBACK_URL).setAccessType("offline").build();
        response.sendRedirect(redirectURL);
    }

    @GetMapping(value = {"/oauth"})
    public String saveAuthorizationCode(HttpServletRequest request) throws Exception{
        String code = request.getParameter("code");
        if (code != null) {
            saveToken(code);

            return "dashboard.html";
        }

        return "index.html";
    }

    private void saveToken(String code) throws Exception{
        GoogleTokenResponse response = flow.newTokenRequest(code).setRedirectUri(CALLBACK_URL).execute();
        flow.createAndStoreCredential(response, USER_IDENTIFIER_KEY);
    }
}
