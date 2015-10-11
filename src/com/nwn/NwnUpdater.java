package com.nwn;

import java.nio.file.Path;
import java.util.ArrayList;

/**
 * Created by Sam on 10/10/2015.
 */
public class NwnUpdater {
    private ArrayList<String> serverFileList;
    private static NwnUpdater instance = new NwnUpdater();

    public synchronized static NwnUpdater getInstance() {
        return instance;
    }

    private NwnUpdater() {

    }
}
