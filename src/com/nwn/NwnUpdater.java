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
    private Path                  serverFileJson;
    private ArrayList<ServerFile> serverFileList;
    private ArrayList<String>     affectedFolders;
    private Path                  nwnRootPath;

    public NwnUpdater() {
        serverFileList = new ArrayList<ServerFile>();
        affectedFolders = new ArrayList<String>();
    }

    @Override
    public void run() {
        parseServerFileJson();
        ArrayList<ServerFile> filesToDownload = determineFilesToDownload();
        downloadFilesFromList(filesToDownload);
        //by this point, serverFileList should already be populated
        //we need to:
        //parse the necessary directories to determine if all files are present
        //download files not present
        //move files to correct directories
        //delete any tmp data
    }

    private void downloadFilesFromList(ArrayList<ServerFile> filesToDownload){
        for(ServerFile serverFile:filesToDownload){
            NwnFileHandler.downloadFile(serverFile.getUrl().toString(), nwnRootPath + File.separator + serverFile.getFolder()
                    + File.separator + serverFile.getName());
        }
    }

    private ArrayList<ServerFile> determineFilesToDownload(){
        ArrayList<ServerFile> filesToDownload = new ArrayList<ServerFile>();
        for(String folder:affectedFolders){
            Path folderPath = Paths.get(nwnRootPath.toString() + File.separator + folder);
            ArrayList<String> localFiles = NwnFileHandler.getFilesNamesInDirectory(folderPath);
            for(ServerFile serverFile:serverFileList){
                if(serverFile.getFolder().equals(folder) && !localFiles.contains(serverFile.getName())){
                    filesToDownload.add(serverFile);
                }
            }
        }

        return filesToDownload;
    }

    private void parseServerFileJson(){
        try{
            FileReader reader = new FileReader("test.json");
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);
            Set<String> folders = jsonObject.keySet();

            for(String folderName:folders){
                affectedFolders.add(folderName);
                JSONArray filesByFolder = (JSONArray)jsonObject.get(folderName);
                Iterator fileItr = filesByFolder.iterator();
                while (fileItr.hasNext()){
                    JSONObject fileJson = (JSONObject) fileItr.next();
                    URL fileUrl = new URL(fileJson.get("url").toString());
                    serverFileList.add(new ServerFile(fileJson.get("name").toString(), fileUrl, folderName));
                }
            }

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
