package com.nwn;

import java.nio.file.Path;
import java.nio.file.Paths;

//.hak, .tlk .bmu, .wav
public class Main {

    public static void testing(){
        FileHandler.downloadFile("http://supergsego.com/apache//commons/beanutils/binaries/commons-beanutils-1.9.2-bin.tar.gz", "C:\\Users\\samca\\Downloads\\commons-beanutils-1.9.2-bin.tar.gz");
        Path test = Paths.get("C:\\Users\\samca\\Downloads\\commons-beanutils-1.9.2-bin.tar.gz");
        System.out.println(FileHandler.getFileExtension(test));
    }

    public static void main(String[] args) {

    }
}
