package com.nwn;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;

/**
` * Created by Sam on 10/10/2015.
 */
public class FileHandler {

    /**
     * Get files in given directory
     * @param dir Path to directory for parsing
     * @return Array of files in directory
     */
    public static File[] getFilesInDirectory(Path dir){
        return null;
    }

    /**
     * Get md5 value of given file
     * @param file file to get md5 of
     * @return String representation of MD5 value
     */
    public static String getMd5(File file){
        return null;
    }

    /**
     * Move file
     * @param file File to move
     * @param dest Desired path to move file to
     * @return True if move success, False if move failed
     */
    public static boolean moveFile(File file, String dest){
       return false;
    }

    /**
     * Extracts contents of given file to provided directory
     * @param file File to extract
     * @param dest Location to extract to
     * @return True if extract success, False if extract failed
     */
    public static boolean extractFile(File file, String dest){
        return false;
    }

    /**
     * Downloads file from given url
     * @param fileUrl URL of file to download
     * @param dest Location on system where file should be downloaded
     * @return True if download success, False if download failed
     */
    public static boolean downloadFile(URL fileUrl, String dest){
        return false;
    }
}
