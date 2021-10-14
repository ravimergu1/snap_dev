package com.snaplogic.snaps;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableSet;
import com.snaplogic.api.ConfigurationException;
import com.snaplogic.api.ExecutionException;
import com.snaplogic.common.SnapType;
import com.snaplogic.common.properties.SnapProperty;
import com.snaplogic.common.properties.builders.PropertyBuilder;
import com.snaplogic.snap.api.*;
import com.snaplogic.snap.api.capabilities.*;
import com.snaplogic.snap.view.InputView;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;


@General(title = "CSV Reader Snap", purpose = "reader csv and check student record",
        author = "Your Company Name", docLink = "http://yourdocslinkhere.com")
@Inputs(min = 0, max = 1, accepts = {ViewType.DOCUMENT})
@Outputs(min = 1, max = 1, offers = {ViewType.DOCUMENT})
@Version(snap = 1)
@Category(snap = SnapCategory.READ)
public class LookupSnap extends SimpleSnap {

    private String fileName = "/home/gaian/Desktop/test/student.csv";
    private int studentId;
    private String name;
    private String address;
    private String mClass;
    private int grade;
    private Set<String> classSet = ImmutableSet.of("1", "2", "3", "4", "5", "6", "7", "8", "9", "10");
    private FileWriter csvWriter;
    private BufferedReader csvReader;
    private ExpressionProperty addressExp;

    @Override
    public void defineProperties(PropertyBuilder propertyBuilder) {
        super.defineProperties(propertyBuilder);
        propertyBuilder.describe("id", "ID", "give studentId")
                .type(SnapType.INTEGER)
                .required()
                .expression(SnapProperty.DecoratorType.ACCEPTS_SCHEMA)
                .add();
        SnapProperty nameProperty = propertyBuilder.describe("name", "Name", "give name")
                .required()
                .build();
        SnapProperty addressProperty = propertyBuilder.describe("address", "Address", "give address")
                .required()
                .expression(SnapProperty.DecoratorType.ACCEPTS_SCHEMA)
                .uiRowCount(10)
                .build();
        propertyBuilder.describe("moreInfo", "More Info", " more info")
                .type(SnapType.COMPOSITE)
                .withEntry(nameProperty)
                .withEntry(addressProperty)
                .add();
        SnapProperty classProperty = propertyBuilder.describe("class", "Class", "give class")
                .required()
                .withAllowedValues(classSet)
                .defaultValue("5")
                .build();
        SnapProperty gradeProperty = propertyBuilder.describe("grade", "Grade", "give grade")
                .required()
                .type(SnapType.INTEGER)
                .build();
        propertyBuilder.describe("perfInfo", "Performance Info", "give performance Info")
                .type(SnapType.COMPOSITE)
                .withEntry(classProperty)
                .withEntry(gradeProperty)
                .add();
    }

    @Override
    public void configure(PropertyValues propertyValues) throws ConfigurationException {
        super.configure(propertyValues);
        studentId = new BigInteger((String) propertyValues.get("id")).intValue();
        name = propertyValues.get("moreInfo.value.name");
        addressExp = propertyValues.getAsExpression("moreInfo.value.address");
        mClass = propertyValues.get("perfInfo.value.class");
        grade = ((BigInteger) propertyValues.get("perfInfo.value.grade")).intValue();
    }


    @Override
    protected void process(Document document, String s) {

        address = addressExp.eval(document);

        StringBuffer sb = new StringBuffer()
                .append(studentId).append(",")
                .append(name).append(",")
                .append(address).append(",")
                .append(mClass).append(",")
                .append(grade);

        try {
            boolean isExists = checkRecordExists(sb.toString());
            HashMap<String, Object> output = new LinkedHashMap<>();
            if (isExists) {
                output.put("message", "record already exists");
            } else {
                addRecord(sb.toString());
                output.put("message", "record appended");
            }
            outputViews.write(documentUtility.newDocument(output));
        } catch (IOException e) {
            e.printStackTrace();
            errorViews.write(new SnapDataException(e.getMessage()));
        }
    }

    private void addRecord(String record) throws IOException {
        csvWriter = new FileWriter(fileName, true);
        csvWriter.append(record);
        csvWriter.append("\n");
        csvWriter.flush();
        csvWriter.close();
    }

    private boolean checkRecordExists(String record) throws IOException {
        csvReader = new BufferedReader(new FileReader(fileName));
        String row = null;
        boolean isFirstRecord = true;
        while ((row = csvReader.readLine()) != null) {
            if (isFirstRecord) {
                isFirstRecord = false;
                continue;
            }
            if (row.replaceAll("\n", "").equals(record.replaceAll("\n", ""))) {
                csvReader.close();
                return true;
            }
        }
        csvReader.close();
        return false;
    }

    @Override
    public void cleanup() throws ExecutionException {
        super.cleanup();
        try {
            if (csvReader != null) {
                csvReader.close();
            }
            if (csvWriter != null) {
                csvWriter.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
