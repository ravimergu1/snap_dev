package com.snaplogic.snaps;


import com.snaplogic.api.ConfigurationException;
import com.snaplogic.api.ExecutionException;
import com.snaplogic.api.Snap;
import com.snaplogic.common.properties.builders.PropertyBuilder;
import com.snaplogic.snap.api.*;
import com.snaplogic.snap.api.capabilities.*;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

@General(title = "String processor", purpose = "process the given string",
        author = "Your Company Name", docLink = "http://yourdocslinkhere.com")
@Inputs(min = 0, max = 1, accepts = {ViewType.DOCUMENT})
@Outputs(min = 1, max = 1, offers = {ViewType.DOCUMENT})
@Errors(min = 1, max = 1, offers = {ViewType.DOCUMENT})
@Version(snap = 1)
@Category(snap = SnapCategory.READ)
public class StringProcessor implements Snap {

    private String inputString;


    @Inject
    private OutputViews outputViews;

    @Inject
    private DocumentUtility documentUtility;


    @Override
    public void defineProperties(PropertyBuilder propertyBuilder) {
        Snap.super.defineProperties(propertyBuilder);

        propertyBuilder.describe("inputString", "Input String", "input string for processing")
                .required()
                .withMinLength(4)
                .add();
    }

    @Override
    public void configure(PropertyValues propertyValues) throws ConfigurationException {
        Snap.super.configure(propertyValues);
        inputString = propertyValues.get("inputString");
    }

    @Override
    public void execute() throws ExecutionException {

            HashMap<String, Object> res = new LinkedHashMap<>();
            res.put("Upper Case", inputString.toUpperCase());
            res.put("Lower Case", inputString.toLowerCase());
            res.put("Reverse", new StringBuffer(inputString).reverse().toString());
            res.put("Length", inputString.length());

            outputViews.write(documentUtility.newDocument(res));

    }

    @Override
    public void cleanup() throws ExecutionException {

    }
}
