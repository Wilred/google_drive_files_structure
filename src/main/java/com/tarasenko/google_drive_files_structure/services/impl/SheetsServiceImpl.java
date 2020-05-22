package com.tarasenko.google_drive_files_structure.services.impl;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.*;
import com.tarasenko.google_drive_files_structure.data.Dimension;
import com.tarasenko.google_drive_files_structure.data.Indices;
import com.tarasenko.google_drive_files_structure.data.SheetData;
import com.tarasenko.google_drive_files_structure.services.SheetsService;
import com.tarasenko.google_drive_files_structure.utils.ServiceUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SheetsServiceImpl implements SheetsService {

    @Value("${google.sheets.api.quota.size.write.requests}")
    Integer limit;

    public Spreadsheet createSheet(Credential credential, String title) {
        Sheets service = ServiceUtils.buildSheetsService(credential);
        Spreadsheet spreadsheet = new Spreadsheet()
                .setProperties(new SpreadsheetProperties().setTitle(title));
        try {
            spreadsheet = service.spreadsheets().create(spreadsheet).setFields("spreadsheetId").execute();
            spreadsheet = service.spreadsheets().get(spreadsheet.getSpreadsheetId()).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Spreadsheet ID: " + spreadsheet.getSpreadsheetId());
        return spreadsheet;
    }

    private String prepareRange(Spreadsheet spreadsheet, String range) {
        StringBuilder builder = new StringBuilder();
        builder.append("'").append(spreadsheet.getSheets().get(0).getProperties().getTitle()).append("'");
        if (range != null) builder.append("!").append(range);
        return builder.toString();
    }

    private List<String> prepareRange(Spreadsheet spreadsheet, List<String> range) {
        StringBuilder builder = new StringBuilder();
        builder.append("'").append(spreadsheet.getSheets().get(0).getProperties().getTitle()).append("'");
        if (range != null) builder.append("!").append(range);
        return Collections.singletonList(builder.toString());
    }

    public Spreadsheet getSpreadsheet(Credential credential, String spreadsheetId) {
        Sheets service = ServiceUtils.buildSheetsService(credential);
        Spreadsheet spreadsheet = null;
        try {
            spreadsheet = service.spreadsheets().get(spreadsheetId).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert spreadsheet != null;
        return spreadsheet;
    }

    public void valuesGet(Credential credential, String spreadsheetId, String range) {
        Sheets service = ServiceUtils.buildSheetsService(credential);
        Spreadsheet spreadsheet = getSpreadsheet(credential, spreadsheetId);

        ValueRange result = new ValueRange();
        int numRows;
        range = prepareRange(spreadsheet, range);

        try {
            result = service.spreadsheets().values().get(spreadsheet.getSpreadsheetId(), range).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        numRows = result.getValues() != null ? result.getValues().size() : 0;
        System.out.printf("%d rows retrieved.\n", numRows);
        System.out.println(result.getValues());
    }

    public void valuesBatchGet(Credential credential, String spreadsheetId, List<String> range) {
        Sheets service = ServiceUtils.buildSheetsService(credential);
        Spreadsheet spreadsheet = getSpreadsheet(credential, spreadsheetId);

        BatchGetValuesResponse result = new BatchGetValuesResponse();
        int numRows;
        range = prepareRange(spreadsheet, range);

        try {
            result = service.spreadsheets().values().batchGet(spreadsheet.getSpreadsheetId()).setRanges(range).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        numRows = result.getValueRanges() != null ? result.getValueRanges().get(0).getValues().size() : 0;
        System.out.printf("%d rows retrieved.\n", numRows);
        System.out.println(result.getValueRanges().get(0).getValues());
    }

    public void valuesUpdate(Credential credential, String spreadsheetId, String range, SheetData sheetData) {
        Sheets service = ServiceUtils.buildSheetsService(credential);
        Spreadsheet spreadsheet = getSpreadsheet(credential, spreadsheetId);

        UpdateValuesResponse result = new UpdateValuesResponse();


        range = prepareRange(spreadsheet, range);

        ValueRange body = new ValueRange().setValues(sheetData.getValue()).setMajorDimension(sheetData.getDimension().getValue());
        try {
            result = service.spreadsheets().values().update(spreadsheet.getSpreadsheetId(), range, body)
                    .setValueInputOption(sheetData.getValueInputOption().getValue()).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.printf("%d cells updated.\n", result.getUpdatedCells());
    }

    public void valuesBatchUpdate(Credential credential, String spreadsheetId, String range, SheetData sheetData) {
        Sheets service = ServiceUtils.buildSheetsService(credential);
        Spreadsheet spreadsheet = getSpreadsheet(credential, spreadsheetId);

        BatchUpdateValuesResponse result = new BatchUpdateValuesResponse();
        sheetData.setValue(Arrays.asList(
                Arrays.asList("Hello", "my name", "is", "=HYPERLINK(\"https://drive.google.com/open?id=1HAjfp8p5VzsXGBc23am4x17oHA_4wKe37qWddPL59Rw\"; \"Andrey\")"),
                Arrays.asList("1", "2", "3", "4")
        ));
        range = prepareRange(spreadsheet, range);

        List<ValueRange> data = new ArrayList<>();
        data.add(new ValueRange().setRange(range).setValues(sheetData.getValue()).setMajorDimension(sheetData.getDimension().getValue()));

        BatchUpdateValuesRequest body = new BatchUpdateValuesRequest().setValueInputOption(sheetData.getValueInputOption().getValue()).setData(data);
        try {
            result = service.spreadsheets().values().batchUpdate(spreadsheet.getSpreadsheetId(), body).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.printf("%d cells updated.\n", result.getTotalUpdatedCells());
    }

    public void spreadsheetsUpdate(Credential credential, String spreadsheetId, Dimension dimension, int startIndex, int endIndex) {
        Spreadsheet spreadsheet = getSpreadsheet(credential, spreadsheetId);
        List<Request> requests = new ArrayList<>(createRequest(spreadsheet, dimension, startIndex, endIndex));
        executeRequest(credential, spreadsheet, requests);
    }

    public void spreadsheetsUpdate(Credential credential, String spreadsheetId, Dimension dimension, List<Indices> indices) {
        Spreadsheet spreadsheet = getSpreadsheet(credential, spreadsheetId);
        List<Request> requests = new ArrayList<>(createRequest(spreadsheet, dimension, indices));

//        requests.add(getCollapses());
        List<List<Request>> limitedRequests = limitingRequests(requests, this.limit);
        limitedRequests.forEach(oneRequestsList -> {
            executeRequest(credential, spreadsheet, oneRequestsList);
            try {
                Thread.sleep(100000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    public void spreadsheetsDelete(Credential credential, String spreadsheetId) {
        Spreadsheet spreadsheet = getSpreadsheet(credential, spreadsheetId);
        List<Request> requests = new ArrayList<>();
        requests.add(new Request().setDeleteSheet(
                new DeleteSheetRequest().setSheetId(spreadsheet.getSheets().get(0).getProperties().getSheetId())
        ));

        executeRequest(credential, spreadsheet, requests);
    }

    private Request getCollapses() {
        return new Request().setUpdateDimensionGroup(
                new UpdateDimensionGroupRequest().setFields("*")
                        .setDimensionGroup(new DimensionGroup()
                                .setCollapsed(true)
                                .setDepth(10)
                                .setRange(new DimensionRange()
                                        .setDimension(Dimension.ROWS.getValue())
                                        .setStartIndex(1)
                                        .setEndIndex(10095)))
        );
    }

    private List<List<Request>> limitingRequests(List<Request> requests, int limit) {
        List<List<Request>> limitedRequests = new ArrayList<>();
        return limitingRequests(limitedRequests, requests, limit);
    }

    private List<List<Request>> limitingRequests(List<List<Request>> limitedRequests, List<Request> requests, int limit) {
        while (!CollectionUtils.isEmpty(requests)) {
            List<Request> kindOfRequests = requests.stream().limit(limit).collect(Collectors.toList());
            requests.removeAll(kindOfRequests);
            limitedRequests.add(kindOfRequests);
        }
        return limitedRequests;
    }

    private List<Request> createRequest(Spreadsheet spreadsheet, Dimension dimension, int startIndex, int endIndex) {
        List<Request> requests = new ArrayList<>();

        requests.add(new Request().setAddDimensionGroup(
                new AddDimensionGroupRequest()
                        .setRange(new DimensionRange()
                                .setSheetId(spreadsheet.getSheets().get(0).getProperties().getSheetId())
                                .setDimension(dimension.getValue())
                                .setStartIndex(startIndex)
                                .setEndIndex(endIndex))));
        return requests;
    }

    private List<Request> createRequest(Spreadsheet spreadsheet, Dimension dimension, List<Indices> indices) {
        List<Request> requests = new ArrayList<>();

        indices.forEach(i -> requests.add(new Request().setAddDimensionGroup(
                new AddDimensionGroupRequest()
                        .setRange(new DimensionRange()
                                .setSheetId(spreadsheet.getSheets().get(0).getProperties().getSheetId())
                                .setDimension(dimension.getValue())
                                .setStartIndex(i.getStartIndex())
                                .setEndIndex(i.getEndIndex())))));

        return requests;
    }

    private void executeRequest(Credential credential, Spreadsheet spreadsheet, List<Request> requests) {
        Sheets service = ServiceUtils.buildSheetsService(credential);
        BatchUpdateSpreadsheetRequest body =
                new BatchUpdateSpreadsheetRequest().setRequests(requests);
        try {
            service.spreadsheets().batchUpdate(spreadsheet.getSpreadsheetId(), body).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
