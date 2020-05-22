package com.tarasenko.google_drive_files_structure.services.impl;

import com.google.api.client.auth.oauth2.Credential;
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

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service(value = "FilesServiceImpl2")
public class FilesServiceImpl2 implements FilesService {
    @Override
    public List<File> searchFiles(Credential credential,
                                  String googleFolderIdParent,
                                  MimeTypes mimeType,
                                  Boolean includeTrashed,
                                  Boolean sharedWithMe) {

        Drive drive = ServiceUtils.buildDrive(credential);
        List<File> files = searchFiles(drive, new HashSet<>(), googleFolderIdParent, mimeType, includeTrashed, sharedWithMe);
        return sort(files);
    }

    private List<File> searchFiles(Drive drive,
                                   Set<File> list,
                                   String googleFolderIdParent,
                                   MimeTypes mimeType,
                                   Boolean includeTrashed,
                                   Boolean sharedWithMe) {

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

            for (File file : result.getFiles()) {
                if (file != null) {
                    list.add(file);
                    if (file.getMimeType().equals(MimeTypes.folder.getValue())) {
                        Objects.requireNonNull(searchFiles(drive, list, file.getId(), null, includeTrashed, null));
                    }
                }
            }

            pageToken = result.getNextPageToken();
        } while (pageToken != null);
        //
        return new ArrayList<>(list);
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


    @Override
    public List<FilesData> group(Credential credential, List<File> files) {
        File root = new File().setId(findMainDriveId(credential)).setName("Drive");
        FilesData rootFolder = new FilesData(root);

        Set<FilesData> allFiles = files.stream().map(FilesData::new).collect(Collectors.toSet());
        Set<FilesData> filesWithoutParent = allFiles.stream()
                .filter(fd -> CollectionUtils.isEmpty(fd.getFile().getParents()))
                .collect(Collectors.toSet());
        Set<FilesData> filesWithParent = allFiles.stream()
                .filter(fd -> !CollectionUtils.isEmpty(fd.getFile().getParents()))
                .filter(fd -> !fd.getFile().getMimeType().equals(MimeTypes.folder.getValue()))
                .collect(Collectors.toSet());
        Set<FilesData> folders = allFiles.stream()
                .filter(fd -> fd.getFile().getMimeType().equals(MimeTypes.folder.getValue()))
                .collect(Collectors.toSet());

        folders.add(rootFolder);

        Set<FilesData> foldersWithFiles = group(folders, filesWithParent);
        Set<FilesData> firstLevelFolders = foldersWithFiles.stream()
                .filter(fd -> CollectionUtils.isEmpty(fd.getFile().getParents()))
                .collect(Collectors.toSet());
        Set<FilesData> foldersWithoutParent = foldersWithFiles.stream()
                .filter(fd -> !CollectionUtils.isEmpty(fd.getFile().getParents()))
                .collect(Collectors.toSet());

        Set<FilesData> groupedFolders = groupFolders(firstLevelFolders, foldersWithoutParent);
        List<FilesData> result = new ArrayList<>(groupedFolders);
//        result = result.get(0).getContainFile();
        setPositionAndLevel(result, new AtomicInteger(0), new AtomicInteger(0));
        return result;
    }

    private Set<FilesData> groupFolders(Set<FilesData> groupedFolders, Set<FilesData> allFolders) {
        if (!CollectionUtils.isEmpty(groupedFolders)) {
            groupedFolders.forEach(gf -> {
                allFolders.forEach(af -> {
//                    if (!CollectionUtils.isEmpty(af.getFile().getParents())) {
                        if (af.getFile().getParents().contains(gf.getFile().getId())) {
                            gf.addContainFile(af);
                        }
//                    } else {
//
//                    }
                });
                groupFolders(new HashSet<>(gf.getContainFile()), allFolders);
            });
        }
        return groupedFolders;
    }

    private Set<FilesData> group(Set<FilesData> groupedFiles, Set<FilesData> remainingFiles) {
        if (!CollectionUtils.isEmpty(remainingFiles)) {
            remainingFiles.forEach(rf ->
                    groupedFiles.forEach(gf -> {
                if (rf.getFile().getParents().contains(gf.getFile().getId())) {
                    gf.addContainFile(rf);
                }
            }));
            Set<FilesData> newParents = groupedFiles.stream()
                    .flatMap(gf -> gf.getContainFile().stream())
//                    .filter(fd -> fd.getFile().getMimeType().equals(MimeTypes.folder.getValue()))
                    .collect(Collectors.toSet());
            remainingFiles.removeAll(newParents);
            group(newParents, remainingFiles);
        }
        return groupedFiles;
    }

    private void setPositionAndLevel(List<FilesData> gFiles, AtomicInteger position, AtomicInteger level) {
        if (!CollectionUtils.isEmpty(gFiles)) {
            gFiles.forEach(f -> {
                f.setLevel(level.get());
                f.setPosition(position.getAndIncrement());
                setPositionAndLevel(f.getContainFile(), position, new AtomicInteger(level.get() + 1));
            });
        }
    }

    @Override
    public List<File> sort(List<File> fileList) {
        return fileList.stream().sorted(new FileNameComparator().thenComparing(new FileMimeTypeComparator())).collect(Collectors.toList());
    }

    @Override
    public String findMainDriveId(Credential credential) {
        Drive drive = ServiceUtils.buildDrive(credential);
        FileList result = new FileList();

        try {
            result = drive.files().list()
                    .setQ("'root' in parents")
                    .setSpaces("drive")
                    .setFields("files(id, name, createdTime, owners, mimeType, parents, webViewLink)")
                    .execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result.getFiles().get(0).getParents().get(0);
    }

    @Override
    public void outFiles(List<FilesData> gFiles) {
        if (!CollectionUtils.isEmpty(gFiles)) {
            gFiles.forEach(f -> {
                System.out.printf("%s %s %s\n", getSpace(f.getLevel()), f.getPosition(), f.getFile().getName());
                outFiles(f.getContainFile());
            });
        }
    }

    private String getSpace(int number) {
        return "    ".repeat(Math.max(0, number));
    }
}
