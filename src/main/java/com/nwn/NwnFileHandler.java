package com.nwn;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.*;
import java.util.ArrayList;
import org.apache.commons.codec.digest.DigestUtils;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.core.ZipFile;

/**
` * Created by Sam on 10/10/2015.
  * TODO: add test cases, add logging
 */
public class NwnFileHandler {

    /**
     * Get files in given directory
     * @param dir Path to directory for parsing
     * @return ArrayList of files in directory
     */
    public static ArrayList<Path> getFilesInDirectory(Path dir){
        ArrayList<Path> filesInDir = new ArrayList<Path>();
        try {
            DirectoryStream<Path> dirStream = Files.newDirectoryStream(dir);
            for (Path file : dirStream) {
                filesInDir.add(file);
            }
        }catch (IOException ex){
            ex.printStackTrace();
        }
        return filesInDir;
    }

    /**
     * Get names of files in given directory
     * @param dir Path to directory for parsing
     * @return String of file names in directory
     */
    public static ArrayList<String> getFilesNamesInDirectory(Path dir){
        ArrayList<String> fileNamesInDir = new ArrayList<String>();
        try {
            DirectoryStream<Path> dirStream = Files.newDirectoryStream(dir);
            for (Path file : dirStream) {
                fileNamesInDir.add(file.getFileName().toString());
            }
            dirStream.close();
        }catch (IOException ex){
            ex.printStackTrace();
        }
        return fileNamesInDir;
    }

    /**
     * Get md5 value of given file
     * @param file file to get md5 of
     * @return String representation of MD5 value
     */
    public static String getMd5(Path file){
        try {
            FileInputStream fis = new FileInputStream(file.toFile());
            String md5 = DigestUtils.md5Hex(fis);
            fis.close();

            return md5;
        }catch (IOException ex){
            ex.printStackTrace();
        }
        return "";
    }

    /**
     * Move file
     * @param src File to move
     * @param dest Desired path to move file to
     * @return True if move success, False if move failed
     */
    public static boolean moveFile(Path src, Path dest){
        try {
            Files.move(src, dest);
        }catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * Extracts contents of given file to provided directory
     * @param file File to extract
     * @param dest Location to extract to
     * @return True if extract success, False if extract failed
     */
    public static boolean extractFile(Path file, Path dest){
        try{
            ZipFile zipFile = new ZipFile(file.toString());
            if(zipFile.isEncrypted()){
                System.out.println("Cannot extract from " + file.getFileName().toString() +": Password required.");
                return false;
            }else{
                zipFile.extractAll(dest.toString());
            }
        }catch (ZipException ex){
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Downloads file from given url
     * @param fileUrl String of url to download
     * @param dest Location on system where file should be downloaded
     * @return True if download success, False if download failed
     */
    public static boolean downloadFile(String fileUrl, String dest){
        try{
            URL url = new URL(fileUrl);
            BufferedInputStream bis = new BufferedInputStream(url.openStream());
            FileOutputStream fis = new FileOutputStream(dest);
            String fileSizeString = url.openConnection().getHeaderField("Content-Length");
            double fileSize = Double.parseDouble(fileSizeString);
            byte[] buffer = new byte[1024];
            int count;
            double bytesDownloaded = 0.0;
            while((count = bis.read(buffer,0,1024)) != -1)
            {
                bytesDownloaded += count;
                fis.write(buffer, 0, count);

                int downloadStatus = (int)((bytesDownloaded/fileSize)*100);

                System.out.println("Downloading " + fileUrl + " to " + dest + " " + downloadStatus + "%");
            }
            fis.close();
            bis.close();

        }catch (MalformedURLException ex){
            ex.printStackTrace();
            return false;
        }catch (FileNotFoundException ex){
            ex.printStackTrace();
            return false;
        }catch (IOException ex){
            ex.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * Get file extension as string
     * @param fileName String name of file
     * @return extension as string without '.'
     */
    public static String getFileExtension(String fileName){
        String extension = "";
        int i = fileName.lastIndexOf('.');
        int p = Math.max(fileName.lastIndexOf('/'), fileName.lastIndexOf('\\'));

        if (i > p) {
            extension = fileName.substring(i+1);
        }

        return extension;
    }

    /**
     * Get suggested folder for file based off extension
     * @param fileName String name of respective file
     * @return String of folder name file should be placed in
     */
    public static String getFolderByExtension(String fileName){
        String ext = getFileExtension(fileName);
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