package com.nwn;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    public static void updaterThreadExample(){
        Path serverFileJson = Paths.get("test.json");
        Path nwnDir = Paths.get("C:\\NeverwinterNights\\NWN");
        NwnUpdater nwnUpdater = new NwnUpdater(nwnDir, serverFileJson);
        Thread updateThread = new Thread(nwnUpdater, "Update Thread");
        updateThread.start();
    }

    public static void main(String[] args) {
        NwnUpdaterMainView mainView = new NwnUpdaterMainView();
    }
}
