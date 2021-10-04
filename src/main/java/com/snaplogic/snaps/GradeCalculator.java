package com.snaplogic.snaps;

import com.snaplogic.api.ConfigurationException;
import com.snaplogic.common.SnapType;
import com.snaplogic.common.properties.Suggestions;
import com.snaplogic.common.properties.builders.PropertyBuilder;
import com.snaplogic.snap.api.*;
import com.snaplogic.snap.api.capabilities.*;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.LinkedHashMap;

@General(title = "Grade Calculator", purpose = "Generates percentage/Grade based on student marks",
        author = "Your Company Name", docLink = "http://yourdocslinkhere.com")
@Inputs(min = 0, max = 1, accepts = {ViewType.DOCUMENT})
@Outputs(min = 1, max = 1, offers = {ViewType.DOCUMENT})
@Version(snap = 1)
@Category(snap = SnapCategory.READ)
public class GradeCalculator extends SimpleSnap {

    private int id;
    private String name;
    private boolean senior;
    private int m1;
    private int m2;
    private int m3;

    @Override
    public void defineProperties(PropertyBuilder pb) {
        pb.describe("id", "Student Id", "give Student id")
                .required()
                .type(SnapType.INTEGER)
                .add();
        pb.describe("name", "Student Name", "Name of the student")
                .required()
                .add();
        pb.describe("senior", "Senior", "is senior")
                .type(SnapType.BOOLEAN)
                .add();

        pb.describe("m1", "C Language", "give marks for C language from 0-100")
                .required()
                .type(SnapType.INTEGER)
                .add();
        pb.describe("m2", "Java", "give marks for Java from 0-100")
                .required()
                .type(SnapType.INTEGER)
                .add();
        pb.describe("m3", ".Net", "give marks for .Net from 0-100")
                .required()
                .type(SnapType.INTEGER)
                .add();

    }

    @Override
    public void configure(PropertyValues propertyValues) throws ConfigurationException {
        BigInteger biId = propertyValues.get("id");
        if (biId != null) id = biId.intValue();

        name = propertyValues.get("name");

        senior = Boolean.TRUE.equals(propertyValues.get("senior"));

        BigInteger biM1 = propertyValues.get("m1");
        if (biM1 != null) m1 = biM1.intValue();

        BigInteger biM2 = propertyValues.get("m2");
        if (biM2 != null) m2 = biM2.intValue();

        BigInteger biM3 = propertyValues.get("m3");
        if (biM3 != null) m3 = biM3.intValue();

        if(m1 < 0 || m1 > 100){
            errorViews.write(new SnapDataException("C language marks between 1-100"));
        }
        if(m2 < 0 || m2 > 100){
            errorViews.write(new SnapDataException("Java marks between 1-100"));
        }
        if(m3 < 0 || m3 > 100){
            errorViews.write(new SnapDataException(".Net marks between 1-100"));
        }
    }

    @Override
    protected void process(Document document, String s) {
        HashMap<String, Object> output = new LinkedHashMap<>();

        output.put("Student",name+"-"+id);

        int percentage = getPercentage();

        output.put("Percentage",percentage);

        if(senior) {
            output.put("Grade", getGrade(percentage));
        }

        outputViews.write(documentUtility.newDocument(output));

    }

    public int getPercentage(){
        int total = m1+m2+m3;
        return total/3;
    }

    public String getGrade(int percentage){
        String grade;
        if(percentage >= 90) grade = "A";
        else if(percentage >= 75) grade = "B";
        else if(percentage >= 60) grade = "C";
        else grade = "D";
        return grade;
    }
}
