package com.snaplogic.snaps;

import com.google.common.collect.ImmutableSet;
import com.snaplogic.api.ConfigurationException;
import com.snaplogic.common.SnapType;
import com.snaplogic.common.properties.SnapProperty;
import com.snaplogic.common.properties.builders.PropertyBuilder;
import com.snaplogic.snap.api.*;
import com.snaplogic.snap.api.capabilities.*;
import com.snaplogic.snap.view.InputView;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;


@General(title = "Expression Snap", purpose = "sample snap to use expressions",
        author = "Your Company Name", docLink = "http://yourdocslinkhere.com")
@Inputs(min = 0, max = 1, accepts = {ViewType.DOCUMENT})
@Outputs(min = 1, max = 1, offers = {ViewType.DOCUMENT})
@Version(snap = 1)
@Category(snap = SnapCategory.READ)
public class ExpressionSnap extends SimpleSnap {

    private static final String QA = "QA";
    private static final String DEV = "DEV";
    private static final String DOC = "Doc";
    private static final String SUPPORT = "SUPPORT";
    private static final String PS = "PS";
    private static final Set<String> ORG_TYPE = ImmutableSet.of(DEV, QA, DOC, SUPPORT, PS);
    private String orgId;
    private String orgName;
    private String orgAddress;
    private int noOfEmps;
    private boolean isActive;
    private ExpressionProperty orgAddressProperty;


    @Override
    public void defineProperties(PropertyBuilder propertyBuilder) {
        super.defineProperties(propertyBuilder);

        propertyBuilder.describe("orgId", "Org id", "select orgid")
                .withAllowedValues(ORG_TYPE)
                .defaultValue(DEV)
                .add();
        propertyBuilder.describe("orgName", "Org Name", "organization name")
                .required()
                .expression()
                .add();
        propertyBuilder.describe("orgAddress", "Org Address", "organization address")
                .required()
                .expression(SnapProperty.DecoratorType.ACCEPTS_SCHEMA)
                .add();
        propertyBuilder.describe("isActive", "Active", "check is active")
                .type(SnapType.BOOLEAN)
                .defaultValue(true)
                .add();
        propertyBuilder.describe("noOfEmps", "Number of employees", "give number of employees")
                .type(SnapType.INTEGER)
                .expression()
                .required()
                .add();
    }

    @Override
    public void configure(PropertyValues propertyValues) throws ConfigurationException {
        super.configure(propertyValues);
        orgId = propertyValues.get("orgId");
        orgName = propertyValues.get("orgName");
        orgAddressProperty = propertyValues.getAsExpression("orgAddress");
        isActive = Boolean.TRUE.equals(propertyValues.get("isActive"));
        noOfEmps = ((BigInteger)propertyValues.getAsExpression("noOfEmps").eval(null)).intValue();
    }

    @Override
    protected void process(Document document, String s) {
        orgAddress = orgAddressProperty.eval(document);
        HashMap<String, Object> output = new LinkedHashMap<>();
        output.put("Id", orgId);
        output.put("Org Name", orgName);
        output.put("Org Address", orgAddress);
        output.put("Active", isActive);
        output.put("# of Emp", noOfEmps);
        if(isActive){
            String rating;
            if(noOfEmps > 100) rating = "A";
            else if (noOfEmps > 50) rating = "B";
            else rating = "C";
            output.put("Rating", rating);
        }
        outputViews.write(documentUtility.newDocument(output));
    }
}
