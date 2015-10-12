package com.nwn;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.text.html.HTMLDocument;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by Sam on 10/10/2015.
 */
public class NwnUpdater implements Runnable{
    private Path                  serverFileJson;
    private ArrayList<ServerFile> serverFileList;
    private Path                  nwnRootPath;

    public NwnUpdater() {
        serverFileList = new ArrayList<ServerFile>();
    }

    @Override
    public void run() {
        parseServerFileJson();
        System.out.println(serverFileList.toString());
//        ArrayList<URL> filesToDownload = determineFilesToDownload();

        //by this point, serverFileList should already be populated
        //we need to:
        //parse the necessary directories to determine if all files are present
        //download files not present
        //move files to correct directories
        //delete any tmp data
    }

    private ArrayList<URL> determineFilesToDownload(){
        return null;
    }

    private void parseServerFileJson(){
        try{
            FileReader reader = new FileReader("test.json");
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);
            Set<String> folders = jsonObject.keySet();

            for(String folderName:folders){
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
