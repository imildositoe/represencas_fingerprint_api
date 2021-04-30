package com.imildo.represencas_api_fingerprint_.controller;

import java.io.File;

public class CheckDirectories {

    public CheckDirectories() {
        File file1 = new File("C:\\Represencas\\db_fingerprint_templates");
        File file2 = new File("C:\\Represencas\\pressed_fingerprint_templates");

        if (!file1.exists()) {
            file1.mkdirs();
        }
        if (!file2.exists()) {
            file2.mkdirs();
        }
    }
}
