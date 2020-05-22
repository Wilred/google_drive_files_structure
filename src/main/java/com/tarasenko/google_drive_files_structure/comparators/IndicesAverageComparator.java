package com.tarasenko.google_drive_files_structure.comparators;

import com.tarasenko.google_drive_files_structure.data.Indices;

import java.util.Comparator;

public class IndicesAverageComparator implements Comparator<Indices> {
    @Override
    public int compare(Indices i1, Indices i2) {
        return Integer.compare(i2.getAverage(), i1.getAverage());
    }
}
