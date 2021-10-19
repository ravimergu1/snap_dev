package com.snaplogic.snaps;

import com.google.common.collect.ImmutableSet;

import java.util.Set;

public class Messages {
    static final String INPUT_SCHEMA_TITLE = "Input schema Snap";
    static final String INPUT_SCHEMA_PURPOSE = "show input schema to other apps";
    static final String INPUT_SCHEMA_AUTHOR = "Your Company Name";
    static final String INPUT_SCHEMA_DOC = "http://yourdocslinkhere.com";
    static final String SCHEMA_LABEL = "Schema";
    static final String SCHEMA_DESC = "Enter you schema";
    static final String YEAR_LABEL = "Year";
    static final String YEAR_DESC = "Select year";
    static final Set<Integer> YEARS = ImmutableSet.of(1, 2, 3, 4);
    static final String CSC_SPE = "CSC";
    static final String EEE_SPE = "EEE";
    static final String ECE_SPE = "ECE";
    static final String CIVIL_SPE = "CIVIL";
    static final String[] SPECIALIZATIONS = new String[]{ECE_SPE, CSC_SPE, EEE_SPE, CIVIL_SPE};

    static final String GOAL_CSC_DEV = "Dev";
    static final String GOAL_CSC_QA = "QA";
    static final String[] GOALS_CSC = new String[]{GOAL_CSC_DEV, GOAL_CSC_QA};

    static final String GOAL_ECE_CHIP = "Chip";
    static final String GOAL_ECE_PCB = "PCB";
    static final String[] GOALS_ECE = new String[]{GOAL_ECE_CHIP, GOAL_ECE_PCB};

    static final String GOAL_EEE_ELE = "Electronic";
    static final String GOAL_EEE_SD = "Smart Devices";
    static final String[] GOALS_EEE = new String[]{GOAL_EEE_ELE, GOAL_EEE_SD};

    static final String GOAL_CIVIL_DESIGN = "Design";
    static final String GOAL_CIVIL_BUILD = "Build";
    static final String[] GOALS_CIVIL = new String[]{GOAL_CIVIL_DESIGN, GOAL_CIVIL_BUILD};

    static final String SPECIALIZATION_LABEL = "Specialization";
    static final String SPECIALIZATION_DESC = "Select Specialization";
    static final String GOAL_LABEL = "GOAL";
    static final String GOAL_DESC = "Select Goal";
    static final String SUCCESS = "Success";
    static final String SUGGESTION_TITLE = "Suggestion";
    static final String SUGGESTION_AUTHOR = "Author";
    static final String SUGGESTION_DOC = "http://doc.com";
    static final String SUGGESTION_PURPOSE = "show list of suggestions";

}
