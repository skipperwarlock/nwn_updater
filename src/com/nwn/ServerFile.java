package com.nwn;

import java.net.URL;

/**
 * Created by Sam on 10/11/2015.
 */
public class ServerFile {
    private String name;
    private String md5;
    private String folder;
    private URL url;

    public ServerFile(String fileName, URL fileUrl){
        name = fileName;
        url = fileUrl;
        folder = NwnFileHandler.getFolderByExtension(fileName);
    }

    public ServerFile(String fileName, URL fileUrl, String fileFolder){
        name = fileName;
        url = fileUrl;
        folder = fileFolder;
    }

    public ServerFile(String fileName, URL fileUrl, String fileFolder, String fileMd5){
        name = fileName;
        url = fileUrl;
        folder = fileFolder;
        md5 = fileMd5;
    }

    public String toString(){
        return "Name: " + getName() + "\nFolder: " + getFolder() + "\nMd5: " + getMd5() + "\nUrl: " + getUrl();
    }

    public String getMd5(){
        return md5;
    }

    public URL getUrl(){
        return url;
    }

    public String getName(){
        return name;
    }

    public String getFolder(){
        return folder;
    }

    public void setMd5(String newMd5){
        md5 = newMd5;
    }

    public void setName(String newName){
        name = newName;
    }

    public void setFolder(String newFolder){
        folder = newFolder;
    }

    public void setUrl(URL newUrl){
        url = newUrl;
    }
}
