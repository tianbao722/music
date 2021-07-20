package com.example.music.bean;

import java.io.File;

public class PDFImageBean {
    private String name;
    private File file;
    private String size;

    public PDFImageBean(String name, File file, String size) {
        this.name = name;
        this.file = file;
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
}
