package com.tarasenko.google_drive_files_structure.quickstart;

import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.tarasenko.google_drive_files_structure.utils.ServiceUtils;
import org.springframework.stereotype.Service;

@Service
public class SheetsQuickstart {
    private static final String APPLICATION_NAME = "Google Sheets API Java Quickstart";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */

    private ServiceUtils serviceUtils;
    private Sheets service;

//    @Autowired
//    public SheetsQuickstart(DriveUtils driveUtils) {
//        this.driveUtils = driveUtils;
//        try {
//            service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, driveUtils.getCredentials())
//                    .setApplicationName(APPLICATION_NAME)
//                    .build();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    public UpdateValuesResponse updateValues(String spreadsheetId, String range,
//                                             String valueInputOption, List<List<Object>> _values)
//            throws IOException, GeneralSecurityException {
//        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
//
//
//        // [START sheets_update_values]
//        List<List<Object>> values = Arrays.asList(
//                Arrays.asList(
//                        // Cell values ...
//                )
//                // Additional rows ...
//        );
//        // [START_EXCLUDE silent]
//        values = _values;
//        // [END_EXCLUDE]
//        ValueRange body = new ValueRange()
//                .setValues(values);
//        UpdateValuesResponse result =
//                service.spreadsheets().values().update(spreadsheetId, range, body)
//                        .setValueInputOption(valueInputOption)
//                        .execute();
//        System.out.printf("%d cells updated.", result.getUpdatedCells());
//        // [END sheets_update_values]
//        return result;
//    }
//
//    /**
//     * Prints the names and majors of students in a sample spreadsheet:
//     * https://docs.google.com/spreadsheets/d/1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms/edit
//     */
//    public void main(String... args) throws IOException, GeneralSecurityException {
//        // Build a new authorized API client service.
//        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
//        final String spreadsheetId = "1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms";
//        final String range = "Class Data!A2:E";
//
//        ValueRange response = service.spreadsheets().values()
//                .get(spreadsheetId, range)
//                .execute();
//        List<List<Object>> values = response.getValues();
//        if (values == null || values.isEmpty()) {
//            System.out.println("No data found.");
//        } else {
//            System.out.println("Name, Major");
//            for (List row : values) {
//                // Print columns A and E, which correspond to indices 0 and 4.
//                System.out.printf("%s, %s\n", row.get(0), row.get(4));
//            }
//        }
//    }
}
