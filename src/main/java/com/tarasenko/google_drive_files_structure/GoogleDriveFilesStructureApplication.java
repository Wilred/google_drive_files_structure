package com.tarasenko.google_drive_files_structure;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GoogleDriveFilesStructureApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(GoogleDriveFilesStructureApplication.class) ;
        application.setAdditionalProfiles("ssl");
        application.run(args);
    }

}
