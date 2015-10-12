package com.nwn;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    public static void testing(){
        NwnFileHandler.downloadFile("http://supergsego.com/apache//commons/beanutils/binaries/commons-beanutils-1.9.2-bin.tar.gz", "C:\\Users\\samca\\Downloads\\commons-beanutils-1.9.2-bin.tar.gz");
        Path test = Paths.get("C:\\Users\\samca\\Downloads\\commons-beanutils-1.9.2-bin.tar.gz");
        System.out.println(NwnFileHandler.getFileExtension(test.getFileName().toString()));
    }

    public static void updaterThreadExample(){
        Path serverFileJson = Paths.get("test.json");
        NwnUpdater nwnUpdater = new NwnUpdater();
        nwnUpdater.setNwnRootPath(Paths.get("C:\\NeverwinterNights\\NWN"));
        nwnUpdater.setServerFileJson(serverFileJson);

        Thread updateThread = new Thread(nwnUpdater, "Update Thread");
        updateThread.start();
    }

    public static void main(String[] args) {
        updaterThreadExample();
    }
}
