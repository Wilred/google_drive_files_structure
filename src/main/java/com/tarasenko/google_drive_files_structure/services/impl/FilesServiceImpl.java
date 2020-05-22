package com.tarasenko.google_drive_files_structure.services.impl;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.tarasenko.google_drive_files_structure.comparators.FileMimeTypeComparator;
import com.tarasenko.google_drive_files_structure.comparators.FileNameComparator;
import com.tarasenko.google_drive_files_structure.data.FilesData;
import com.tarasenko.google_drive_files_structure.data.MimeTypes;
import com.tarasenko.google_drive_files_structure.services.FilesService;

import com.tarasenko.google_drive_files_structure.utils.ServiceUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service("FilesServiceImpl1")
public class FilesServiceImpl implements FilesService {

    public List<File> searchFiles(Credential credential,
                                  String googleFolderIdParent,
                                  MimeTypes mimeType,
                                  Boolean includeTrashed,
                                  Boolean sharedWithMe) {
        Drive drive = ServiceUtils.buildDrive(credential);
//        List<File> files = searchFiles(drive, new ArrayList<>(), googleFolderIdParent, mimeType, includeTrashed, null);
        List<File> files = searchFiles(drive, new HashSet<>(), googleFolderIdParent, mimeType, includeTrashed, sharedWithMe);
        return sort(files);
    }

    private List<File> searchFiles(Drive drive,
                                   Set<File> list,
                                   String googleFolderIdParent,
                                   MimeTypes mimeType,
                                   Boolean includeTrashed,
                                   Boolean sharedWithMe) {

        if (sharedWithMe != null && !sharedWithMe && googleFolderIdParent == null) googleFolderIdParent = "root";
        if (list == null) list = new HashSet<>();
        String pageToken = null;

        String query = queryBuild(googleFolderIdParent, mimeType, includeTrashed, sharedWithMe);

        do {
            FileList result = new FileList();
            try {
                result = drive.files().list().setQ(query).setSpaces("drive") //
                        .setFields("nextPageToken, files(id, name, createdTime, owners, mimeType, parents, webViewLink)")//
                        .setPageToken(pageToken).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }

//            if (sharedWithMe != null && !sharedWithMe) {
            for (File file : result.getFiles()) {
                if (file != null) {
                    list.add(file);
                    if (file.getMimeType().equals(MimeTypes.folder.getValue())) {
                        Objects.requireNonNull(searchFiles(drive, list, file.getId(), null, includeTrashed, null));
                    }
                }
            }
//            } else {
//                list.addAll(result.getFiles());
//            }
            pageToken = result.getNextPageToken();
        } while (pageToken != null);
        //
        return new ArrayList<>(list);
    }

    public String findMainDriveId(Credential credential) {
        Drive drive = ServiceUtils.buildDrive(credential);
        return findMainDriveId(drive);
    }

    private String findMainDriveId(Drive drive) {
        FileList result = new FileList();
        FileList result2 = new FileList();
        try {
            result = drive.files().list()
                    .setQ("'root' in parents")
                    .setSpaces("drive")
                    .setFields("files(id, name, createdTime, owners, mimeType, parents, webViewLink)")
                    .execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        result2 = service.files().list()
//                    .setQ("name = ")

        return result.getFiles().get(0).getParents().get(0);
    }

    private String queryBuild(String googleFolderIdParent, MimeTypes mimeType, Boolean includeTrashed, Boolean sharedWithMe) {
        StringBuilder query = new StringBuilder();

        if (sharedWithMe != null && sharedWithMe)
            query.append("sharedWithMe ").append("= ").append(true).append(" and ");

        if (googleFolderIdParent != null)
            query.append("'").append(googleFolderIdParent).append("' in ").append("parents ").append("and ");

        if (mimeType != null)
            query.append("mimeType ").append("= '").append(mimeType.getValue()).append("' ").append("and ");

        if (includeTrashed != null)
            query.append("trashed ").append("= ").append(includeTrashed.toString());
        return query.toString();
    }

    public List<File> sort(List<File> fileList) {
        return fileList.stream().sorted(new FileMimeTypeComparator().thenComparing(new FileNameComparator())).collect(Collectors.toList());
    }

    private String getSpace(int number) {
        return "    ".repeat(Math.max(0, number));
    }

    public List<FilesData> group(Credential credential, List<File> files) {
        Drive drive = ServiceUtils.buildDrive(credential);
        List<FilesData> result;
        File root = new File().setId(findMainDriveId(drive)).setName("Drive").setParents(Collections.singletonList("drive"));

        List<FilesData> allFiles = files.stream().map(FilesData::new).collect(Collectors.toList());
        Set<String> allowedParents = allFiles.stream()
                .filter(fd -> fd.getFile().getMimeType().equals(MimeTypes.folder.getValue()))
                .filter(fd -> !CollectionUtils.isEmpty(fd.getFile().getParents()))
                .map(fd -> fd.getFile().getParents())
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
        List<FilesData> firstParent = new ArrayList<>(Collections.singletonList(new FilesData(root)));

        result = group(firstParent, allFiles, allowedParents, 0);
        result = result.get(0).getContainFile();
        setPositionAndLevel(result, new AtomicInteger(0), new AtomicInteger(0));
        return result;
    }

    private List<FilesData> group(List<FilesData> groupedFiles, List<FilesData> remainingFiles, Set<String> allowedParents, int lvl) {
        List<FilesData> filesToClean = new ArrayList<>();
        if (!CollectionUtils.isEmpty(remainingFiles)) {
            remainingFiles.forEach(rf -> {
                if (!CollectionUtils.isEmpty(rf.getFile().getParents())) {
//                    if (allowedParents.stream().anyMatch(ap -> rf.getFile().getParents().contains(ap))) {
                        groupedFiles.forEach(gf -> {
                            if (rf.getFile().getParents().contains(gf.getFile().getId())) {
                                gf.addContainFile(rf);
                                gf.setLevel(lvl);
                            }
                        });
//                    } else {
//                        filesToClean.add(rf);
//                    }
                } else {
                    groupedFiles.get(0).addContainFile(rf);
                }
            });
            List<FilesData> newParents = groupedFiles.stream().flatMap(gf -> gf.getContainFile().stream()).collect(Collectors.toList());
            remainingFiles.removeAll(newParents);
            remainingFiles.removeAll(filesToClean);
            group(newParents, remainingFiles, allowedParents, lvl + 1);
        }
        return groupedFiles;
    }

//    private List<FilesData> group(List<FilesData> groupedFiles, List<FilesData> remainingFiles, int lvl) {
//        if (!CollectionUtils.isEmpty(remainingFiles)) {
//            remainingFiles.forEach(rf -> {
//                groupedFiles.forEach(gf -> {
//                    if (rf.getFile().getParents().get(0).equals(gf.getFile().getId())) {
//                        gf.addContainFile(rf);
//                        gf.setLevel(lvl);
//                    }
//                });
//            });
//            List<FilesData> newParents = groupedFiles.stream().flatMap(gf -> gf.getContainFile().stream()).collect(Collectors.toList());
//            remainingFiles.removeAll(newParents);
//            group(newParents, remainingFiles, lvl + 1);
//        }
//        return groupedFiles;
//    }

    private void setPositionAndLevel(List<FilesData> gFiles, AtomicInteger position, AtomicInteger level) {
        if (!CollectionUtils.isEmpty(gFiles)) {
            gFiles.forEach(f -> {
                f.setLevel(level.get());
                f.setPosition(position.getAndIncrement());
                setPositionAndLevel(f.getContainFile(), position, new AtomicInteger(level.get() + 1));
            });
        }
    }

    public void outFiles(List<FilesData> gFiles) {
        if (!CollectionUtils.isEmpty(gFiles)) {
            gFiles.forEach(f -> {
                System.out.printf("%s %s %s\n", getSpace(f.getLevel()), f.getPosition(), f.getFile().getName());
                outFiles(f.getContainFile());
            });
        }
    }
}