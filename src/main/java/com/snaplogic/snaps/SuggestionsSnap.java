package com.snaplogic.snaps;

import com.snaplogic.api.ConfigurationException;
import com.snaplogic.common.SnapType;
import com.snaplogic.common.properties.builders.PropertyBuilder;
import com.snaplogic.snap.api.Document;
import com.snaplogic.snap.api.PropertyValues;
import com.snaplogic.snap.api.SimpleSnap;
import com.snaplogic.snap.api.SnapCategory;
import com.snaplogic.snap.api.SnapDataException;
import com.snaplogic.snap.api.capabilities.Category;
import com.snaplogic.snap.api.capabilities.General;
import com.snaplogic.snap.api.capabilities.Inputs;
import com.snaplogic.snap.api.capabilities.Outputs;
import com.snaplogic.snap.api.capabilities.Version;
import com.snaplogic.snap.api.capabilities.ViewType;

import java.math.BigInteger;
import java.time.Year;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import static com.snaplogic.snaps.Messages.*;

@General(title = SUGGESTION_TITLE, purpose = SUGGESTION_PURPOSE,
        author = SUGGESTION_AUTHOR, docLink = SUGGESTION_DOC)
@Inputs(min = 0, max = 1, accepts = {ViewType.DOCUMENT})
@Outputs(min = 1, max = 1, offers = {ViewType.DOCUMENT})
@Version(snap = 1)
@Category(snap = SnapCategory.READ)
public class SuggestionsSnap extends SimpleSnap {


    private static final String YEAR_PROP = "year";
    private static final String SPECIALIZATION_PROP = "specialization";
    private static final String GOAL_PROP = "goal";

    private String goal;
    private String specialization;
    private int year;

    @Override
    public void defineProperties(final PropertyBuilder propertyBuilder) {
        super.defineProperties(propertyBuilder);

        propertyBuilder.describe(YEAR_PROP, YEAR_LABEL, YEAR_DESC)
                .type(SnapType.INTEGER)
                .withAllowedValues(YEARS)
                .defaultValue(1)
                .required()
                .add();
        propertyBuilder.describe(SPECIALIZATION_PROP, SPECIALIZATION_LABEL, SPECIALIZATION_DESC)
                .required()
                .withSuggestions((suggestionBuilder, propertyValues) -> {
                    suggestionBuilder.node(SPECIALIZATION_PROP)
                            .suggestions(SPECIALIZATIONS);
                })
                .add();
        propertyBuilder.describe(GOAL_PROP, GOAL_LABEL, GOAL_DESC)
                .required()
                .withSuggestions((suggestionBuilder, propertyValues) -> {
                    String specialization = propertyValues.get(SPECIALIZATION_PROP);
                    suggestionBuilder.node(GOAL_PROP)
                            .suggestions(getGoals(specialization));
                })
                .add();
    }

    private String[] getGoals(final String specialization) {
        String[] goals = null;
        switch (specialization) {
            case ECE_SPE:
                goals = GOALS_ECE;
                break;
            case EEE_SPE:
                goals = GOALS_EEE;
                break;
            case CSC_SPE:
                goals = GOALS_CSC;
                break;
            case CIVIL_SPE:
                goals = GOALS_CIVIL;
                break;
            default:
                goals = new String[]{};
                break;
        }
        return goals;
    }

    @Override
    public void configure(final PropertyValues propertyValues) throws ConfigurationException {
        super.configure(propertyValues);
            year = ((BigInteger)propertyValues.get(YEAR_PROP)).intValue();
            specialization = propertyValues.get(SPECIALIZATION_PROP);
            goal = propertyValues.get(GOAL_PROP);
    }

    @Override
    protected void process(final Document document, final String s) {
        List<String> goals = Arrays.asList(getGoals(specialization));
        if(goal != null && !goals.contains(goal)){
            errorViews.write(new SnapDataException("Select correct goal based on specialization"));
            return;
        }
        HashMap<String, Object> output = new LinkedHashMap<>();
        output.put(YEAR_LABEL, year);
        output.put(SPECIALIZATION_LABEL, specialization);
        output.put(GOAL_LABEL, goal);
        outputViews.write(documentUtility.newDocument(output));
    }

}
