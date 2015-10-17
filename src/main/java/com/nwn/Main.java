package com.nwn;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    public static void updaterThreadExample(){
        Path serverFileJson = Paths.get("test.json");
        NwnUpdater nwnUpdater = new NwnUpdater(Paths.get("C:\\NeverwinterNights\\NWN"), serverFileJson);
        Thread updateThread = new Thread(nwnUpdater, "Update Thread");
        updateThread.start();
    }

    public static void main(String[] args) {
        updaterThreadExample();
    }
}
