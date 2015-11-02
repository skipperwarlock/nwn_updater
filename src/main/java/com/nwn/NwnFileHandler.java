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

import com.github.junrar.Archive;
import com.github.junrar.exception.RarException;
import com.github.junrar.impl.FileVolumeManager;
import com.github.junrar.rarfile.FileHeader;
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
  * TODO: add test cases
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
    public static ArrayList<String> getFileNamesInDirectory(Path dir){
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
		if(getFileExtension(file.toString()).equals("zip")){
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
		}else if(getFileExtension(file.toString()).equals("rar")){//todo: finish
			Archive a = null;
			try {
				a = new Archive(new FileVolumeManager(file.toFile()));
			} catch (RarException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (a != null) {
				a.getMainHeader().print();
				FileHeader fh = a.nextFileHeader();
				while (fh != null) {
					try {
						File out = new File(dest.toString()
								+ File.separator
								+ fh.getFileNameString().trim());
						System.out.println(out.getAbsolutePath());
						FileOutputStream os = new FileOutputStream(out);
						a.extractFile(fh, os);
						os.close();
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (RarException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					fh = a.nextFileHeader();
				}
			}
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
     * Deletes every file in given directory
     * If a directory is found, it will be recursively deleted
     * @param file directory or file to delete
     */
    public static void deleteDir(File file){
        File[] contents = file.listFiles();
        if (contents != null) {
            for (File f : contents) {
                deleteDir(f);
            }
        }
        file.delete();
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

	public static boolean isValidNwnDirectory(String nwnDir, String checkfileName){
		Path nwnPath = Paths.get(nwnDir);
		if(nwnPath.toFile().isDirectory()){
			ArrayList<String> nwnPathFiles = getFileNamesInDirectory(nwnPath);
			for(String fileName:nwnPathFiles){
				if(fileName.equalsIgnoreCase(checkfileName)){
					return true;
				}
			}
		}
		return false;
	}
}
