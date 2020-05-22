package com.tarasenko.google_drive_files_structure.data;

import java.util.List;

public class SheetData {
    private List<List<Object>> value;
    private Dimension dimension = Dimension.COLUMNS;
    private ValueInputOption valueInputOption = ValueInputOption.RAW;

    public List<List<Object>> getValue() {
        return value;
    }

    public SheetData setValue(List<List<Object>> value) {
        this.value = value;
        return this;
    }

    public Dimension getDimension() {
        return dimension;
    }

    public SheetData setDimension(Dimension dimension) {
        this.dimension = dimension;
        return this;
    }

    public ValueInputOption getValueInputOption() {
        return valueInputOption;
    }

    public SheetData setValueInputOption(ValueInputOption valueInputOption) {
        this.valueInputOption = valueInputOption;
        return this;
    }
}
