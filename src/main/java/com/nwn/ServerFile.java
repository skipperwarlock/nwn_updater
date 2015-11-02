/*
This file is part of NWN Server Updater.

    NWN Server Updater is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    NWN Server Updater is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with NWN Server Updater.  If not, see <http://www.gnu.org/licenses/>.
*/
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
	
	/**
	 * Variable used for archives to track files inside archive
	 * @return list of files reported to be inside archive
	 */
    public ArrayList<String> getFileList(){
	    return fileList;
    }
   
	/**
	 * md5 currently not stored
	 * @return 
	 */
    public String getMd5(){
        return md5;
    }

	/**
	 * @return URL of file
	 */
    public URL getUrl(){
        return url;
    }

	/**
	 * @return filename 
	 */
    public String getName(){
        return name;
    }

	/**
	 * @return file destination folder 
	 */
    public String getFolder(){
        return folder;
    }

	/**
	 * Used for adding file names to archived file list
	 * @param fileName filename to add to list
	 */
    public void addFileToList(String fileName){
        fileList.add(fileName);
    }

	/**
	 * Used for removing file names from archived file list
	 * @param fileName filename to remove from list
	 */
    public void removeFileFromList(String fileName){
        fileList.remove(fileName);
    }

	/**
	 * set fileList to null
	 */
	public void deleteFileList(){
		for(String file:fileList){
			fileList.remove(file);
		}
		fileList = null;
	}

	/**
	 * currently not used
	 * @param newMd5 
	 */
    public void setMd5(String newMd5){
        md5 = newMd5;
    }

	/**
	 * @param newName new filename
	 */
    public void setName(String newName){
        name = newName;
    }

	/**
	 * @param newFolder new destination folder name
	 */
    public void setFolder(String newFolder){
        folder = newFolder;
    }

	/**
	 * @param newUrl new file url
	 */
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
