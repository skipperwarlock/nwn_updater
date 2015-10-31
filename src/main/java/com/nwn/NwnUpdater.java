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
	    deleteDirWithMessage(tmpFolder);
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

    private void printExitStatus(int status){
	    String exitStatus;
	    switch(status){
		    case 0: exitStatus  = "Update Process Complete"; break;
		    case 1: exitStatus  = "Update canceled by user"; break;
		    case 2: exitStatus  = "Update failed"; break;
		    case 3: exitStatus  = "All Files up to date"; break;
		    default: exitStatus = "Update failed for unknonw reason"; break;
	    }
//	    System.out.println(exitStatus);
	    currentGui.appendOutputText("\n"+exitStatus);
    }
    
    private void cleanup(){
	currentGui.setTaskProgressBarValue(50);
        deleteDirWithMessage(new File(nwnRootPath + File.separator + FolderByExt.COMPRESSED.toString()));
	currentGui.setTaskProgressBarValue(100);
	currentGui.setOverallProgressBarValue(100);
	currentGui.setUpdateBtnText("Update");
    }

    /**
     * Notification wrapper for deleteDir
     * @param file directory or file to delete
     */
    private void deleteDirWithMessage(File file){
//        System.out.print("Cleaning up temporary files...");
	currentGui.appendOutputText("\nCleaning up temporary files...");
        NwnFileHandler.deleteDir(file);
//        System.out.println("done");
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
     * @param uncompressedFolder
     */
    private void processFilesInDirectory(Path uncompressedFolder){
        ArrayList<String> fileNames = NwnFileHandler.getFilesNamesInDirectory(uncompressedFolder);
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
                    if (folderName.equals(FolderByExt.COMPRESSED.toString())) {
                        uncompressFile(fileName, folderName);
                    }
                }else if(!desiredFolder.toFile().exists()){
//                    System.out.println("ERROR: Folder " + folderName + " does not exist!");
		    currentGui.appendOutputText("\nERROR: Folder "+folderName+" does not exist!");
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
//        System.out.print("Extracting " + fileName + "...");
	currentGui.appendOutputText("\nExtracting "+fileName+"...");
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
//            System.out.print("done\n");
	    currentGui.appendOutputText("done");
            processFilesInDirectory(Paths.get(baseName));
        }else{
//            System.out.print("ERROR: compression not supported\n");
	    currentGui.appendOutputText("\nERROR: compression not supported");
        }
    }

    /**
     *
     * @param filesToDownload
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
//                System.out.println("Error downloading file: " + serverFile.getName());
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
     *
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
//        System.out.print("Checking local files");
	currentGui.appendOutputText("\nChecking local files");
        ArrayList<ServerFile> filesToDownload = new ArrayList<ServerFile>();
        for(String folder:affectedFolders){
            Path folderPath = Paths.get(nwnRootPath.toString() + File.separator + folder);
            ArrayList<String> localFiles = NwnFileHandler.getFilesNamesInDirectory(folderPath);
            for(ServerFile serverFile:serverFileList){
//                System.out.print(".");
		currentGui.appendOutputText(".");
                if(serverFile.getFolder().equals(folder) && !localFiles.contains(serverFile.getName())){
                    filesToDownload.add(serverFile);
                }
            }
	    currentProgress = currentProgress + progressIncrement;
	    currentGui.setTaskProgressBarValue(currentProgress);
        }
//        System.out.println();
	currentGui.appendOutputText("done");
        return filesToDownload;
    }

    /**
     *
     */
    private boolean parseServerFileJson(){
	currentGui.setTaskProgressBarValue(0);
	int currentProgress = 0;
	int statusIncrement;
//        System.out.print("Reading file list");
	currentGui.appendOutputText("\n\nReading file list");
        try{
            FileReader reader = new FileReader(serverFileJson.toString());
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);
            Set<String> folders = jsonObject.keySet();
	    if(folders.size() > 0){
		statusIncrement = 100/folders.size();
	    }else{
		statusIncrement = 100;
	    }
	    for(String folderName:folders){
                if(!folderName.contains("..") && !folderName.contains(":")) {
                    affectedFolders.add(folderName);
                    JSONArray filesByFolder = (JSONArray) jsonObject.get(folderName);
                    Iterator fileItr = filesByFolder.iterator();
                    while (fileItr.hasNext()) {
//                        System.out.print(".");
			currentGui.appendOutputText(".");
                        JSONObject fileJson = (JSONObject) fileItr.next();
                        URL fileUrl = new URL(fileJson.get("url").toString());
                        serverFileList.add(new ServerFile(fileJson.get("name").toString(), fileUrl, folderName));
                    }
                }else{
//                    System.out.println("An unusual folder path was detected: " + folderName +
//                            "\nServer owner may be attempting to place files outside of NWN." +
//                            "\nThis folder has been excluded from the update."
//                    );
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
	    currentGui.appendOutputText("\nERROR: Cannot read server file list.");
	    return false;
        }catch (ParseException ex){
//            ex.printStackTrace();
	    currentGui.appendOutputText("\nERROR: Cannot parse server file list.");
	    return false;
        }
	return true;
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

    public void setNwnRootPath(Path newNwnRootPath){
        nwnRootPath = newNwnRootPath;
    }

    public void setServerFileJson(Path newServerFileJson){
        serverFileJson = newServerFileJson;
    }
}
