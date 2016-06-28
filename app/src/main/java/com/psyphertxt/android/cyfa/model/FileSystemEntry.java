package com.psyphertxt.android.cyfa.model;

import java.io.File;
import java.io.Serializable;
import java.util.List;

/**
 * Created by pie on 9/23/15.
 */
public class FileSystemEntry implements Serializable {
    private String parenthPath;
    private String path;
    private String name;
    private String description;
    private boolean isDirectory;
    private List<FileSystemEntry> children;

    private final String[] IMAGE_FILE_EXTENSIONS = new String[]{".jpg", ".jpeg", ".png"};
    private final String[] VIDEO_FILE_EXTENSIONS = new String[]{".mp4", ".mpeg", ".oog"};

    public FileSystemEntry() {
    }

    public String getParenthPath() {
        return parenthPath;
    }

    public void setParenthPath(String parenthPath) {
        this.parenthPath = parenthPath;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public void setIsDirectory(boolean isDirectory) {
        this.isDirectory = isDirectory;
    }

    public List<FileSystemEntry> getChildren() {
        return children;
    }

    public void setChildren(List<FileSystemEntry> children) {
        this.children = children;
    }

    public static FileSystemEntry fromFile(File file) {
        FileSystemEntry fileSystemEntry = new FileSystemEntry();
        fileSystemEntry.setPath(file.getAbsolutePath());
        fileSystemEntry.setName(file.getName());
        fileSystemEntry.setParenthPath(file.getParent());
        fileSystemEntry.setIsDirectory(file.isDirectory());

        if (fileSystemEntry.isDirectory()) {
            fileSystemEntry.setDescription(String.format("%s files", file.list().length));
        } else {
            fileSystemEntry.setDescription("");
        }

        return fileSystemEntry;
    }

    public String getExtension() {
        String extension = "";
        if (this.isDirectory()) {
        } else {
            extension = this.getPath().substring(this.getPath().lastIndexOf("."), this.getPath().length());
        }
        return extension;
    }

    public boolean isImageFile() {
        String extension = getExtension();
        for (int i = 0; i < IMAGE_FILE_EXTENSIONS.length; i++) {
            if (extension.equalsIgnoreCase(IMAGE_FILE_EXTENSIONS[i])) {
                return true;
            }
        }
        return false;
    }

    public boolean isVideoFile() {
        String extension = getExtension();
        for (int i = 0; i < VIDEO_FILE_EXTENSIONS.length; i++) {
            if (extension.equalsIgnoreCase(VIDEO_FILE_EXTENSIONS[i])) {
                return true;
            }
        }
        return false;
    }

    public boolean exists() {
        return new File(this.getPath()).exists();
    }
}
