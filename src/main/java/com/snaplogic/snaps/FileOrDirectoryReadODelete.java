package com.snaplogic.snaps;

import com.google.common.collect.ImmutableSet;
import com.snaplogic.api.ConfigurationException;
import com.snaplogic.common.SnapType;
import com.snaplogic.common.properties.builders.PropertyBuilder;
import com.snaplogic.snap.api.*;
import com.snaplogic.snap.api.capabilities.*;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

@General(title = "File Directory Read Delete", purpose = "Used to read files from dir and remove file from direcory",
        author = "Your Company Name", docLink = "http://yourdocslinkhere.com")
@Inputs(min = 0, max = 1, accepts = {ViewType.DOCUMENT})
@Outputs(min = 1, max = 1, offers = {ViewType.DOCUMENT})
@Version(snap = 1)
@Category(snap = SnapCategory.READ)
public class FileOrDirectoryReadODelete extends SimpleSnap {

    private static final String FILE = "File";
    private static final String DIRECTORY = "Directory";
    private static final String DELETE = "Delete";
    private static final String READ = "Read";
    private static final Set<String> FILE_TYPE = ImmutableSet.of(FILE, DIRECTORY);
    private static final Set<String> OPERATION = ImmutableSet.of(DELETE, READ);
    private String fileType;
    private String path;
    private String operation;
    private boolean isShowAttributes;

    @Override
    public void defineProperties(PropertyBuilder propertyBuilder) {
        super.defineProperties(propertyBuilder);

        propertyBuilder.describe("fileType", " File Type", "select file or directory")
                .withAllowedValues(FILE_TYPE)
                .defaultValue(DIRECTORY)
                .add();
        propertyBuilder.describe("path", "path", " path for the file/directory")
                .required()
                .add();
        propertyBuilder.describe("operation", "Operation", " select read or write operation")
                .withAllowedValues(OPERATION)
                .defaultValue(READ)
                .add();
        propertyBuilder.describe("display_attribute", "Display Attributes ", " show all the attributes for the given file or directory")
                .type(SnapType.BOOLEAN)
                .add();
    }

    @Override
    public void configure(PropertyValues propertyValues) throws ConfigurationException {
        super.configure(propertyValues);
        fileType = propertyValues.get("fileType");
        path = propertyValues.get("path");
        operation = propertyValues.get("operation");
        isShowAttributes = Boolean.TRUE.equals(propertyValues.get("display_attribute"));
    }

    @Override
    protected void process(Document document, String s) {
        File file = new File(path);
        if (!file.exists()) {
            errorViews.write(new SnapDataException("file/directory not exists"));
        } else if (operation.equals(READ) && !file.canRead()) {
            errorViews.write(new SnapDataException("file/directory not readable"));
        } else if (operation.equals(DELETE) && !file.canWrite()) {
            errorViews.write(new SnapDataException("file/directory not deletable"));
        } else if (file.isFile() && operation.equals(READ)) {
            readFile(file);
        } else if (file.isFile() && operation.equals(DELETE)) {
            deleteFile(file);
        } else if (file.isDirectory() && operation.equals(READ)) {
            readDirectory(file);
        } else if (file.isDirectory() && operation.equals(DELETE)) {
            deleteDirectory(file);
        }
    }

    private void readFile(File file) {
        try {
            String data = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
            HashMap<String, Object> output = new LinkedHashMap<>();
            output.put("data", data);
            if(isShowAttributes){
                addAttributes(output, file);
            }
            outputViews.write(documentUtility.newDocument(output));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addAttributes(HashMap<String, Object> output, File file) {
        output.put("file name", file.getName());
        output.put("file size",file.length());

   }

    private void deleteFile(File file) {
        HashMap<String, Object> output = new LinkedHashMap<>();
        if(isShowAttributes){
            addAttributes(output, file);
        }
        boolean delete = file.delete();
        output.put("isDeleted", delete);
        outputViews.write(documentUtility.newDocument(output));
    }

    private void readDirectory(File file) {
        File[] listFiles = file.listFiles();
        StringBuffer sb = new StringBuffer();
        for(File f: listFiles){
            if(f.isFile()){
                try {
                    sb.append(FileUtils.readFileToString(f,StandardCharsets.UTF_8));
                    sb.append("\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        HashMap<String, Object> output = new LinkedHashMap<>();
        output.put("data", sb.toString());
        if(isShowAttributes){
            addAttributes(output, file);
        }
        outputViews.write(documentUtility.newDocument(output));
    }

    private void deleteDirectory(File file) {
        HashMap<String, Object> output = new LinkedHashMap<>();
        if(isShowAttributes){
            addAttributes(output, file);
        }
        boolean delete = file.delete();
        output.put("isDeleted",delete);
        outputViews.write(documentUtility.newDocument(output));
    }
}
