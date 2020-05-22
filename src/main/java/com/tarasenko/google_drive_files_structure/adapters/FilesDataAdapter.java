package com.tarasenko.google_drive_files_structure.adapters;

import com.tarasenko.google_drive_files_structure.data.Dimension;
import com.tarasenko.google_drive_files_structure.data.FilesData;
import com.tarasenko.google_drive_files_structure.data.SheetData;
import com.tarasenko.google_drive_files_structure.data.ValueInputOption;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

public class FilesDataAdapter extends SheetData {
    private List<FilesData> filesData;

    public FilesDataAdapter(List<FilesData> filesData) {
        this.filesData = filesData;
        super.setValue(mapValue());
    }

    public List<List<Object>> mapValue() {
        List<FilesData> filesData  = new ArrayList<>(this.filesData);
        List<List<Object>> row = new ArrayList<>();
        return mapValue(filesData, row);
    }

    private List<List<Object>> mapValue(List<FilesData> filesData, List<List<Object>> row) {
        if (!CollectionUtils.isEmpty(filesData)) {
            int lvl = filesData.get(0).getLevel();
            if (row.size() - 1 < lvl) {
                row.add(lvl, new ArrayList<>());
            }
            List<Object> column = row.get(lvl);
            filesData.forEach(fd -> {
                while (column.size() - 1 < fd.getPosition()) column.add("");
                String value = "=HYPERLINK(\"" + fd.getFile().getWebViewLink() + "\"; \"" + fd.getFile().getName() + "\")";
                column.add(fd.getPosition(), value);
                mapValue(new ArrayList<>(fd.getContainFile()), row);
            });
        }
        return row;
    }

    @Override
    public FilesDataAdapter setValue(List<List<Object>> value) {
        super.setValue(value);
        return this;
    }

    @Override
    public FilesDataAdapter setDimension(Dimension dimension) {
        super.setDimension(dimension);
        return this;
    }

    @Override
    public FilesDataAdapter setValueInputOption(ValueInputOption valueInputOption) {
        super.setValueInputOption(valueInputOption);
        return this;
    }
}
