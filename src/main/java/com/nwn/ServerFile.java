package com.nwn;

import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Sam on 10/11/2015.
 */
public class ServerFile {
    private String	      name;
    private String	      md5;
    private String	      folder;
    private URL		      url;
    private ArrayList<String> fileList; //compressed files only

    public ServerFile(String fileName, URL fileUrl){
        name = fileName;
        url = fileUrl;
        folder = getFolderByExtension(fileName).toLowerCase();
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

    public ServerFile(String fileName, URL fileUrl, String fileFolder, ArrayList<String> files){
        name = fileName;
        url = fileUrl;
        folder = fileFolder;
		fileList = files;
    }

    @Override
    public String toString(){
		if(fileList == null){
			return "Name: " + getName() + "\nFolder: " + getFolder() + "\nMd5: " + getMd5() + "\nUrl: " + getUrl();
		}else{
			return "Name: " + getName() + "\nFolder: " + getFolder() + "\nUrl: " + getUrl() + fileList.toString();
		}
    }

    public ArrayList<String> getFileList(){
	    return fileList;
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

    public void addFileToList(String fileName){
        fileList.add(fileName);
    }

    public void removeFileFromList(String fileName){
        fileList.remove(fileName);
    }

	public void deleteFileList(){
		for(String file:fileList){
			fileList.remove(file);
		}
		fileList = null;
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
    
    /**
     * Get suggested folder for file based off extension
     * @param fileName String name of respective file
     * @return String of folder name file should be placed in
     */
    public static String getFolderByExtension(String fileName){
        String ext = NwnFileHandler.getFileExtension(fileName);
        String folder = ext;
        switch (ext){
            case "bmu":
                folder = FolderByExt.BMU.toString();
                break;
            case "wav":
                folder = FolderByExt.WAV.toString();
                break;
            case "hak":
                folder = FolderByExt.HAK.toString();
                break;
            case "tlk":
                folder = FolderByExt.TLK.toString();
                break;
            case "zip":
                folder = FolderByExt.COMPRESSED.toString();
                break;
        }

        return folder;
    }
}
