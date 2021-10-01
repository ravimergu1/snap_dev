package com.snaplogic.snaps;

import com.snaplogic.api.ConfigurationException;
import com.snaplogic.api.ExecutionException;
import com.snaplogic.api.Snap;
import com.snaplogic.common.SnapType;
import com.snaplogic.common.properties.builders.PropertyBuilder;
import com.snaplogic.snap.api.*;
import com.snaplogic.snap.api.capabilities.*;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.UUID;

@General(title = "Aadhar Generator", purpose = "Generates aadhar for an indian",
        author = "Your Company Name", docLink = "http://yourdocslinkhere.com")
@Inputs(min = 0, max = 1, accepts = {ViewType.DOCUMENT})
@Outputs(min = 1, max = 1, offers = {ViewType.DOCUMENT})
@Errors(min = 1, max = 1, offers = {ViewType.DOCUMENT})
@Version(snap = 1)
@Category(snap = SnapCategory.READ)
public class AadharGenerator implements Snap {

    private int id;
    private String name;
    private boolean isNRI;

    @Inject
    private OutputViews outputViews;

    @Inject
    private DocumentUtility documentUtility;

    @Override
    public void defineProperties(PropertyBuilder pb) {
        Snap.super.defineProperties(pb);

        pb.describe("id", "Id", "Id for indian nationality")
                .type(SnapType.INTEGER)
                .add();
        pb.describe("name", "Name", "Name for indian nationality")
                .add();
        pb.describe("nri", "NRI", "is NRI for india")
                .type(SnapType.BOOLEAN)
                .add();

    }

    @Override
    public void configure(PropertyValues propertyValues) throws ConfigurationException {
        Snap.super.configure(propertyValues);

        BigInteger bigInteger = propertyValues.get("id");
        if (bigInteger != null) id = bigInteger.intValue();

        name = propertyValues.get("name");

        isNRI = Boolean.TRUE.equals(propertyValues.get("nri"));
    }

    @Override
    public void execute() throws ExecutionException {

        HashMap<String, Object> output = new LinkedHashMap<>();
        if (!isNRI) {
            output.put("message", name + " with id " + id + " and Aadhar number: " + UUID.randomUUID());
        } else {
            output.put("message", name + " with id " + id + " is not an indian");
        }

        Document document = documentUtility.newDocument(output);
        outputViews.write(document);

    }

    @Override
    public void cleanup() throws ExecutionException {

    }
}
