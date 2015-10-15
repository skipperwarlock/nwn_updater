package com.nwn;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by Sam on 10/10/2015.
 */
public class NwnUpdater implements Runnable{
    private Path                  nwnRootPath;
    private Path                  serverFileJson;
    private ArrayList<ServerFile> serverFileList;
    private ArrayList<String>     affectedFolders;

    /**
     * Create NwnUpdater object
     * Also creates "compressed_tmp" folder if it does not exist
     * @param newNwnRootPath
     * @param newServerFileJson
     */
    public NwnUpdater(Path newNwnRootPath, Path newServerFileJson) {
        serverFileList = new ArrayList<ServerFile>();
        affectedFolders = new ArrayList<String>();
        nwnRootPath = newNwnRootPath;
        serverFileJson = newServerFileJson;

        File tmpFolder = new File(nwnRootPath.toString() + File.separator + "compressed_tmp");
        if(!tmpFolder.exists()){
            tmpFolder.mkdir();
        }
    }

    /**
     * Start updater process
     */
    @Override
    public void run() {
        parseServerFileJson();
        ArrayList<ServerFile> filesToDownload = determineFilesToDownload();
        downloadFilesFromList(filesToDownload);
        //todo: ask user if they want temp files deleted
        deleteFilesInDir(nwnRootPath + File.separator + "compressed_tmp");
        System.out.println("Update Process Complete");
    }

    /**
     * Deletes every file in given directory
     * If a directory is found, it will be recursively deleted
     * @param folderPath absolute path to directory to delete from
     */
    private void deleteFilesInDir(String folderPath){
        ArrayList<Path> fileList = NwnFileHandler.getFilesInDirectory(Paths.get(folderPath));
        for (Path file : fileList) {
            if (file.toFile().isDirectory()) {
                deleteFilesInDir(file.toFile().getAbsolutePath());
            }
            file.toFile().delete();
        }
    }

    /**
     * Reads through every file in directory and determines correct action for each file based off the file extension
     * Files will either be moved to the correct directory or extracted and processed.
     * @param uncompressedFolder
     */
    private void processFilesInDirectory(Path uncompressedFolder){
        ArrayList<String> fileNames = NwnFileHandler.getFilesNamesInDirectory(uncompressedFolder);
        for(String fileName:fileNames){
            Path srcFile = Paths.get(uncompressedFolder.toString() + File.separator + fileName);
            if(srcFile.toFile().isDirectory()){
                processFilesInDirectory(srcFile);
            }else {
                String folderName = NwnFileHandler.getFolderByExtension(fileName);
                Path desiredFolder = Paths.get(nwnRootPath.toString() + File.separator + folderName);
                Path desiredPath = Paths.get(nwnRootPath.toString() + File.separator + folderName + File.separator + fileName);
                if (!desiredPath.toFile().exists() && desiredFolder.toFile().exists()) {
                    NwnFileHandler.moveFile(srcFile, desiredPath);
                    if (folderName.equals("compressed_tmp")) {
                        uncompressFile(fileName, folderName);
                    }
                }else if(!desiredFolder.toFile().exists()){
                    System.out.println("ERROR: Folder " + folderName + " does not exist!");
                }
            }
        }
    }

    /**
     * 
     * @param fileName
     * @param parentFolder
     */
    private void uncompressFile(String fileName, String parentFolder){
        System.out.print("Extracting " + fileName + "...");
        String fileLoc = nwnRootPath + File.separator + parentFolder + File.separator + fileName;
        String baseName = fileLoc;
        String fileExt = NwnFileHandler.getFileExtension(fileLoc);
        if(fileExt.length() > 0){
            baseName = fileLoc.substring(0,fileLoc.lastIndexOf('.'));
        }
        if(fileExt.equals("zip")) {
            File extractFolder = new File(baseName);
            if (!extractFolder.exists()) {
                extractFolder.mkdir();
            }
            NwnFileHandler.extractFile(Paths.get(fileLoc), Paths.get(baseName));
            System.out.print("done\n");
            processFilesInDirectory(Paths.get(extractFolder.getAbsolutePath()));
        }else{
            System.out.print("ERROR: compression not supported\n");
        }
    }

    /**
     *
     * @param filesToDownload
     */
    private void downloadFilesFromList(ArrayList<ServerFile> filesToDownload){
        for(ServerFile serverFile:filesToDownload){
            if(!NwnFileHandler.downloadFile(serverFile.getUrl().toString(), nwnRootPath + File.separator + serverFile.getFolder()
                    + File.separator + serverFile.getName())){
                System.out.println("Error downloading file: " + serverFile.getName());
            }
            //TODO: replace string with enum
            if(serverFile.getFolder().equals("compressed_tmp")){
                uncompressFile(serverFile.getName(), serverFile.getFolder());
            }
        }
    }

    /**
     *
     * @return
     */
    private ArrayList<ServerFile> determineFilesToDownload(){
        System.out.print("Checking local files");
        ArrayList<ServerFile> filesToDownload = new ArrayList<ServerFile>();
        for(String folder:affectedFolders){
            Path folderPath = Paths.get(nwnRootPath.toString() + File.separator + folder);
            ArrayList<String> localFiles = NwnFileHandler.getFilesNamesInDirectory(folderPath);
            for(ServerFile serverFile:serverFileList){
                System.out.print(".");
                if(serverFile.getFolder().equals(folder) && !localFiles.contains(serverFile.getName())){
                    filesToDownload.add(serverFile);
                }
            }
        }
        System.out.println();

        return filesToDownload;
    }

    /**
     *
     */
    private void parseServerFileJson(){
        System.out.print("Reading file list");
        try{
            FileReader reader = new FileReader("test.json");
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);
            Set<String> folders = jsonObject.keySet();

            for(String folderName:folders){
                if(!folderName.contains("..") && !folderName.contains(":")) {
                    affectedFolders.add(folderName);
                    JSONArray filesByFolder = (JSONArray) jsonObject.get(folderName);
                    Iterator fileItr = filesByFolder.iterator();
                    while (fileItr.hasNext()) {
                        System.out.print(".");
                        JSONObject fileJson = (JSONObject) fileItr.next();
                        URL fileUrl = new URL(fileJson.get("url").toString());
                        serverFileList.add(new ServerFile(fileJson.get("name").toString(), fileUrl, folderName));
                    }
                }else{
                    System.out.println("An unusual folder path was detected: " + folderName +
                            "\nServer owner may be attempting to place files outside of NWN." +
                            "\nThis folder has been excluded from the update."
                    );
                }
            }
            System.out.println();

        }catch (IOException ex){
            ex.printStackTrace();
        }catch (ParseException ex){
            ex.printStackTrace();
        }
    }

    public ArrayList<ServerFile> getServerFileList(){
        return serverFileList;
    }

    public Path getNwnRootPath(){
        return nwnRootPath;
    }

    public Path getServerFileJson(){
        return serverFileJson;
    }

//    public void setServerFileList(ArrayList<ServerFile> newServerFileList){
//        serverFileList = newServerFileList;
//    }

    public void setNwnRootPath(Path newNwnRootPath){
        nwnRootPath = newNwnRootPath;
    }

    public void setServerFileJson(Path newServerFileJson){
        serverFileJson = newServerFileJson;
    }
}
