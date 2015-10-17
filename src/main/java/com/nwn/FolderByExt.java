package com.nwn;

/**
 * Keep track of which folders are associated with which file extensions
 * note: getFolderByExtension checks statically for extension names. If you add here, add there also
 */
public enum FolderByExt {
    BMU("music"),
    WAV("ambient"),
    HAK("hak"),
    TLK("tlk"),
    COMPRESSED("compressed_tmp");

    private String value;

    FolderByExt(String value){
        this.value = value;
    }

    public String toString(){
        return this.value;
    }
}
