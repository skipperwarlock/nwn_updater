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

import java.io.BufferedInputStream;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
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
    private NwnUpdaterHomeView    currentGui;

    /**
     * Create NwnUpdater object
     * Also creates compressed folder if it does not exist
     * @param newNwnRootPath
     * @param newServerFileJson
     */
    public NwnUpdater(Path newNwnRootPath, Path newServerFileJson, NwnUpdaterHomeView gui) {
        serverFileList  = new ArrayList<ServerFile>();
        affectedFolders = new ArrayList<String>();
        nwnRootPath     = newNwnRootPath;
        serverFileJson  = newServerFileJson;
		currentGui      = gui;

        File tmpFolder = new File(nwnRootPath.toString() + File.separator + FolderByExt.COMPRESSED.toString());
        if(!tmpFolder.exists()){
            tmpFolder.mkdir();
        }else{
		    deleteDirWithMessage(tmpFolder, "\nRemoving old files...");
		    tmpFolder.mkdir();
		}
    }

    /**
     * Start updater process
     */
    @Override
    public void run() {
		currentGui.setUpdateBtnText("Stop");    
	    
		if(Thread.currentThread().isInterrupted()){cleanup();printExitStatus(1);return;}
        if(!parseServerFileJson()){cleanup();printExitStatus(2);return;}
		currentGui.setOverallProgressBarValue(5);
	
		if(Thread.currentThread().isInterrupted()){cleanup();printExitStatus(1);return;}
        ArrayList<ServerFile> filesToDownload = determineFilesToDownload();
		currentGui.setOverallProgressBarValue(10);
	
		if(Thread.currentThread().isInterrupted()){cleanup();printExitStatus(1);return;}
		if(filesToDownload.size() > 0){
		    downloadFilesFromList(filesToDownload);
		}else{cleanup();printExitStatus(3);return;}
		currentGui.setOverallProgressBarValue(90);
	
		if(Thread.currentThread().isInterrupted()){cleanup();printExitStatus(1);return;}
		cleanup();
		printExitStatus(0);
    }
	/**
	 * Output why the update process ended
	 * Available statuses:
	 * 0 = Complete
	 * 1 = Canceled
	 * 2 = Failed
	 * 3 = No update required
	 * default = Unknown reason
	 * @param status exit status integer
	 * //todo: ENUMS
	 */
    private void printExitStatus(int status){
	    String exitStatus;
	    switch(status){
		    case 0: exitStatus  = "Update Process Complete"; break;
		    case 1: exitStatus  = "Update canceled by user"; break;
		    case 2: exitStatus  = "Update failed"; break;
		    case 3: exitStatus  = "All Files up to date"; break;
		    default: exitStatus = "Update failed for unknonw reason"; break;
	    }
	    currentGui.appendOutputText("\n"+exitStatus);
    }
   
	/**
	 * Make sure everything is cleaned up
	 * This currently only wipes the compressed file directory
	 */
    private void cleanup(){
		currentGui.setTaskProgressBarValue(50);
        deleteDirWithMessage(new File(nwnRootPath + File.separator + FolderByExt.COMPRESSED.toString()),"Cleaning up temporary files...");
		currentGui.setTaskProgressBarValue(100);
		currentGui.setOverallProgressBarValue(100);
		currentGui.setUpdateBtnText("Update");
    }

    /**
     * Notification wrapper for deleteDir
     * @param file directory or file to delete
	 * @param message message to give user about delete
     */
    private void deleteDirWithMessage(File file, String message){
		currentGui.appendOutputText("\n"+message);
        NwnFileHandler.deleteDir(file);
		currentGui.appendOutputText("done");
    }

    /**
     * For debugging file deletes
     * @param file
     * @return String of why file can't be deleted
     */
    private String getReasonForFileDeletionFailureInPlainEnglish(File file) {
        try {
            if (!file.exists())
                return file.getName() + " It doesn't exist in the first place.";
            else if (file.isDirectory() && file.list().length > 0)
                return file.getName() + " It's a directory and it's not empty.\n";
            else
                return file.getName() + " Somebody else has it open, we don't have write permissions, or somebody stole my disk.";
        } catch (SecurityException e) {
            return file.getName() + " We're sandboxed and don't have filesystem access.";
        }
    }

    /**
     * Downloads file from given url
	 * Gracefully handle thread interrupt
     * @param fileUrl String of url to download
     * @param dest Location on system where file should be downloaded
     * @return True if download success, False if download failed
     */
    public boolean interruptableDownloadFile(String fileUrl, String dest){
		currentGui.appendOutputText("\nDownloading "+fileUrl+" to "+dest+"...");
        try{
            URL url = new URL(fileUrl);
            BufferedInputStream bis = new BufferedInputStream(url.openStream());
            FileOutputStream fis = new FileOutputStream(dest);
            String fileSizeString = url.openConnection().getHeaderField("Content-Length");
            double fileSize = Double.parseDouble(fileSizeString);
            byte[] buffer = new byte[1024];
            int count;
            double bytesDownloaded = 0.0;
		    currentGui.setTaskProgressBarValue(0);
            while((count = bis.read(buffer,0,1024)) != -1 && !Thread.currentThread().isInterrupted())
            {
                bytesDownloaded += count;
                fis.write(buffer, 0, count);
				if(fileSize > 0){
					int downloadStatus = (int)((bytesDownloaded/fileSize)*100);
				    currentGui.setTaskProgressBarValue(downloadStatus);
//                    System.out.println("Downloading " + fileUrl + " to " + dest + " " + downloadStatus + "%");
				}
            }
            fis.close();
            bis.close();
			if(Thread.currentThread().isInterrupted()){//cleanup from interrupt
				File thisFile = new File(dest);
				thisFile.delete();
			}
        }catch (MalformedURLException ex){
		    currentGui.appendOutputText("\nERROR: URL Invalid");
//            ex.printStackTrace();
            return false;
        }catch (FileNotFoundException ex){
		    currentGui.appendOutputText("\nERROR: File not found");
//            ex.printStackTrace();
            return false;
        }catch (IOException ex){
	    currentGui.appendOutputText("\nERROR: Cannot save file");
//            ex.printStackTrace();
            return false;
        }
		if(Thread.currentThread().isInterrupted()){
			return false;
		}
		currentGui.appendOutputText("done");
        return true;
    }

    /**
     * Reads through every file in directory and determines correct action for each file based off the file extension
     * Files will either be moved to the correct directory or extracted and processed.
     * @param uncompressedFolder Path of directory to process
     */
    private void processFilesInDirectory(Path uncompressedFolder){
        ArrayList<String> fileNames = NwnFileHandler.getFileNamesInDirectory(uncompressedFolder);
        for(String fileName:fileNames){
            Path srcFile = Paths.get(uncompressedFolder.toString() + File.separator + fileName);
            if(srcFile.toFile().isDirectory()){
                processFilesInDirectory(srcFile);
            }else {
                String folderName = ServerFile.getFolderByExtension(fileName);
                Path desiredFolder = Paths.get(nwnRootPath.toString() + File.separator + folderName);
                Path desiredPath = Paths.get(nwnRootPath.toString() + File.separator + folderName + File.separator + fileName);
                if (!desiredPath.toFile().exists() && desiredFolder.toFile().exists()) {
                    NwnFileHandler.moveFile(srcFile, desiredPath);
		    currentGui.appendOutputText("\nMoving " + srcFile.getFileName().toString() + " to " + desiredFolder.toString());
                    if (folderName.equals(FolderByExt.COMPRESSED.toString())) {
                        uncompressFile(fileName, folderName);
                    }
                }else if(!desiredFolder.toFile().exists()){
		    currentGui.appendOutputText("\nERROR: Folder "+folderName+" does not exist!");
                }
            }
        }
    }

    /**
     * Extract contents of archive to current directory
	 * Supports zip and rar
     * @param fileName archive file name
     * @param parentFolder folder containing archive file 
     */
    private void uncompressFile(String fileName, String parentFolder){
		currentGui.appendOutputText("\nExtracting "+fileName+"...");
        String fileLoc = nwnRootPath + File.separator + parentFolder + File.separator + fileName;
        String baseName = fileLoc;
        String fileExt = NwnFileHandler.getFileExtension(fileLoc);
        if(fileExt.length() > 0){
            baseName = fileLoc.substring(0,fileLoc.lastIndexOf('.'));
        }
        if(fileExt.equals("zip") || fileExt.equals("rar")) {
            File extractFolder = new File(baseName);
            if (!extractFolder.exists()) {
                extractFolder.mkdir();
            }
            NwnFileHandler.extractFile(Paths.get(fileLoc), Paths.get(baseName));
		    currentGui.appendOutputText("done");
            processFilesInDirectory(Paths.get(baseName));
        }else{
		    currentGui.appendOutputText("\nERROR: compression not supported");
        }
    }

    /**
     * Download files from given list
	 * If they are archives, extract them
     * @param filesToDownload List of ServerFile objects to download
     */
    private void downloadFilesFromList(ArrayList<ServerFile> filesToDownload){
		//todo: don't hardcode progress numbers
		int overallProgress = 10;
		int overallInterval = 80;
		if(filesToDownload.size() > 0){
		    overallInterval = 80/filesToDownload.size();
		}
		boolean downloadSuccess;
        for(ServerFile serverFile:filesToDownload){
		    downloadSuccess = interruptableDownloadFile(serverFile.getUrl().toString(), 
		    nwnRootPath + File.separator + serverFile.getFolder()
                    + File.separator + serverFile.getName());
            if(!downloadSuccess){
			    currentGui.appendOutputText("\nError downloading file: "+serverFile.getName());
            }else{
			    if(serverFile.getFolder().equals(FolderByExt.COMPRESSED.toString())){
				uncompressFile(serverFile.getName(), serverFile.getFolder());
		    }
	    }
	    overallProgress = overallProgress + overallInterval;
	    currentGui.setOverallProgressBarValue(overallProgress);
        }
    }

    /**
     * Scan local folders which should contain the files needed for the selected server
	 * If any files are missing, add them to the list of files to download
     * @return
     */
    private ArrayList<ServerFile> determineFilesToDownload(){
		currentGui.setTaskProgressBarValue(0);
		int currentProgress = 0;
		int progressIncrement;
		if(affectedFolders.size() > 0){
			progressIncrement = 100/affectedFolders.size();
		}else{
			progressIncrement = 100;
		}
		currentGui.appendOutputText("\nChecking local files");
        ArrayList<ServerFile> filesToDownload = new ArrayList<ServerFile>();
        for(String folder:affectedFolders){
            Path folderPath = Paths.get(nwnRootPath.toString() + File.separator + folder);
            ArrayList<String> localFiles = NwnFileHandler.getFileNamesInDirectory(folderPath);
            for(ServerFile serverFile:serverFileList){
				currentGui.appendOutputText(".");
                if(serverFile.getFileList() == null && serverFile.getFolder().equals(folder) && !localFiles.contains(serverFile.getName())){
                    filesToDownload.add(serverFile);
                }else if(serverFile.getFileList() != null){
					for(String file:serverFile.getFileList()){
						if(!localFiles.contains(file) && NwnFileHandler.getFileExtension(file).equalsIgnoreCase(folder) 
							&& !filesToDownload.contains(serverFile)){
							
							filesToDownload.add(serverFile);
							break;
						}
					}
				}
            }
		    currentProgress = currentProgress + progressIncrement;
		    currentGui.setTaskProgressBarValue(currentProgress);
        }
		currentGui.appendOutputText("done");
        return filesToDownload;
    }

    /**
     * Parse json file containing files required for server
	 * Convert those files into ServerFile objects and store them 
     */
    private boolean parseServerFileJson(){
		String compressedFileName;
		currentGui.setTaskProgressBarValue(0);
		int currentProgress = 0;
		int statusIncrement;
		currentGui.appendOutputText("\n\nReading file list");
        try{
			Thread.sleep(500);
            FileReader  reader     = new FileReader(serverFileJson.toString());
            JSONParser  jsonParser = new JSONParser();
            JSONObject  jsonObject = (JSONObject) jsonParser.parse(reader);
            Set<String> folders    = jsonObject.keySet();
		    if(folders.size() > 0){
				statusIncrement = 100/folders.size();
		    }else{
				statusIncrement = 100;
		    }
		    for(String folderName:folders){
                if(!folderName.contains("..") && !folderName.contains(":")) {
                    affectedFolders.add(folderName);
                    JSONArray filesByFolder = (JSONArray) jsonObject.get(folderName);
                    Iterator  fileItr       = filesByFolder.iterator();
					while (fileItr.hasNext()) {
						currentGui.appendOutputText(".");
                        JSONObject fileJson = (JSONObject) fileItr.next();
                        URL fileUrl         = new URL(fileJson.get("url").toString());
						if(folderName.equalsIgnoreCase(FolderByExt.COMPRESSED.toString())){
							ArrayList<String> compressedFileList  = new ArrayList<>();
							JSONArray         compressedFileArray = (JSONArray) fileJson.get("files");
							Iterator          cfItr               = compressedFileArray.iterator();						
							while(cfItr.hasNext()){
								compressedFileName = cfItr.next().toString();
								affectedFolders.add(NwnFileHandler.getFileExtension(compressedFileName));
								compressedFileList.add(compressedFileName);
							}
							serverFileList.add(new ServerFile(fileJson.get("name").toString(), fileUrl, folderName,compressedFileList));
						}else{
							serverFileList.add(new ServerFile(fileJson.get("name").toString(), fileUrl, folderName));
						}
					}
                }else{
				    currentGui.appendOutputText("An unusual folder path was detected: " + folderName +
                            "\nServer owner may be attempting to place files outside of NWN." +
                            "\nThis folder has been excluded from the update.");
                }
				currentProgress = currentProgress + statusIncrement;
				currentGui.setTaskProgressBarValue(currentProgress);
            }
			currentGui.appendOutputText("done");
		    reader.close();
        }catch (IOException ex){
//            ex.printStackTrace();
		    currentGui.appendOutputText("...failed\nERROR: Cannot read server file list.");
		    return false;
        }catch (ParseException ex){
//            ex.printStackTrace();
		    currentGui.appendOutputText("...failed\nERROR: Cannot parse server file list.");
		    return false;
        }catch (InterruptedException ex){
		    currentGui.appendOutputText("...canceled");
			return false;
		}
		return true;
    }

	/**
	 * @return ArrayList of files required by the selected server 
	 */
    public ArrayList<ServerFile> getServerFileList(){
        return serverFileList;
    }

	/**
	 * @return User specified root path of nwn
	 */
    public Path getNwnRootPath(){
        return nwnRootPath;
    }

	/**
	 * @return path to json file containing files required by selected server 
	 */
    public Path getServerFileJson(){
        return serverFileJson;
    }

	/**
	 * Set path of directory containing nwmain.exe
	 * @param newNwnRootPath root path for nwn
	 */
    public void setNwnRootPath(Path newNwnRootPath){
        nwnRootPath = newNwnRootPath;
    }

	/**
	 * @param newServerFileJson path to json file containing required server files
	 */
    public void setServerFileJson(Path newServerFileJson){
        serverFileJson = newServerFileJson;
    }
}
