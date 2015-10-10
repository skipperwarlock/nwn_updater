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
public class FileHandler {

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

        }
        return filesInDir;
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
                //todo: log error
                return false;
            }else{
                zipFile.extractAll(dest.toString());
            }
        }catch (ZipException ex){
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
            int fileSize = Integer.parseInt(fileSizeString);
            byte[] buffer = new byte[1024];
            int count;
            while((count = bis.read(buffer,0,1024)) != -1)
            {
                fis.write(buffer, 0, count);
            }
            fis.close();
            bis.close();

        }catch (MalformedURLException mue){
            return false;
        }catch (FileNotFoundException fnfe){
            return false;
        }catch (IOException ioex){
            return false;
        }

        return true;
    }
}
