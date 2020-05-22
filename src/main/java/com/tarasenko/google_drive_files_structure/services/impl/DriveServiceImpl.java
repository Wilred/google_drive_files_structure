package com.tarasenko.google_drive_files_structure.services.impl;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.drive.model.File;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.tarasenko.google_drive_files_structure.adapters.FilesDataAdapter;
import com.tarasenko.google_drive_files_structure.comparators.IndicesAverageComparator;
import com.tarasenko.google_drive_files_structure.data.Dimension;
import com.tarasenko.google_drive_files_structure.data.FilesData;
import com.tarasenko.google_drive_files_structure.data.Indices;
import com.tarasenko.google_drive_files_structure.data.ValueInputOption;
import com.tarasenko.google_drive_files_structure.services.DriveService;
import com.tarasenko.google_drive_files_structure.services.FilesService;
import com.tarasenko.google_drive_files_structure.services.SheetsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class DriveServiceImpl implements DriveService {

    private FilesService filesService;
    private SheetsService sheetsService;

    @Autowired
    DriveServiceImpl(@Qualifier("FilesServiceImpl1") FilesService filesService, SheetsService sheetsService) {
        this.filesService = filesService;
        this.sheetsService = sheetsService;
    }

    public void sheetUpdateWithFilesStructure(Credential credential, String spreadsheetId) throws IOException {
        writeFilesStructure(credential, spreadsheetId);
    }

    @Override
    public void sheetCreateWithFilesStructure(Credential credential) throws IOException {
        Spreadsheet spreadsheet = sheetsService.createSheet(credential,"Files structure");
        writeFilesStructure(credential,spreadsheet.getSpreadsheetId());
    }

    private void writeFilesStructure(Credential credential, String spreadsheetId) throws IOException {
        List<File> files = filesService.searchFiles(credential,null, null, false, true);
        List<File> sortedFiles = filesService.sort(files);
        List<FilesData> filesData = filesService.group(credential, sortedFiles);
        FilesDataAdapter filesDataAdapter = new FilesDataAdapter(filesData).setDimension(Dimension.COLUMNS).setValueInputOption(ValueInputOption.USER_ENTERED);
        sheetsService.valuesUpdate(credential, spreadsheetId, null, filesDataAdapter);
        List<Indices> indices = getIndices(filesData);
        indices.sort(new IndicesAverageComparator());
        sheetsService.spreadsheetsUpdate(credential, spreadsheetId, Dimension.ROWS, indices);
    }

    private List<Indices> getIndices(List<FilesData> filesData) {
        return getIndices(new ArrayList<>(), filesData);
    }

    private List<Indices> getIndices(List<Indices> result, List<FilesData> filesData) {
        if (!CollectionUtils.isEmpty(filesData)) {
            filesData.forEach(f -> {
                Indices indices = new Indices();
                AtomicInteger sIndex = new AtomicInteger(0);
                AtomicInteger eIndex = new AtomicInteger(0);
                AtomicInteger average = new AtomicInteger(0);
                if (!CollectionUtils.isEmpty(f.getContainFile())) {
                    f.getContainFile().stream().mapToInt(FilesData::getPosition).min().ifPresent(sIndex::set);
                    f.getContainFile().stream().mapToInt(FilesData::getPosition).max().ifPresent(i -> eIndex.set(i + 1));
                    average.set(eIndex.get() - sIndex.get());
                    indices.setStartIndex(sIndex.get());
                    indices.setEndIndex(eIndex.get());
                    indices.setAverage(average.get());
                    getIndices(result, f.getContainFile());
                    result.add(indices);
                }
            });
        }
        return result;
    }
}
