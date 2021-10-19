package com.snaplogic.snaps;

import com.google.common.collect.ImmutableMap;
import com.snaplogic.api.ConfigurationException;
import com.snaplogic.api.InputSchemaProvider;
import com.snaplogic.common.SnapType;
import com.snaplogic.common.properties.builders.PropertyBuilder;
import com.snaplogic.snap.api.Document;
import com.snaplogic.snap.api.PropertyValues;
import com.snaplogic.snap.api.SimpleSnap;
import com.snaplogic.snap.api.SnapCategory;
import com.snaplogic.snap.api.capabilities.Category;
import com.snaplogic.snap.api.capabilities.General;
import com.snaplogic.snap.api.capabilities.Inputs;
import com.snaplogic.snap.api.capabilities.Outputs;
import com.snaplogic.snap.api.capabilities.Version;
import com.snaplogic.snap.api.capabilities.ViewType;
import com.snaplogic.snap.schema.api.SchemaBuilder;
import com.snaplogic.snap.schema.api.SchemaProvider;

import java.util.Collection;
import java.util.Map;

import static com.snaplogic.snaps.Messages.INPUT_SCHEMA_AUTHOR;
import static com.snaplogic.snaps.Messages.INPUT_SCHEMA_DOC;
import static com.snaplogic.snaps.Messages.INPUT_SCHEMA_PURPOSE;
import static com.snaplogic.snaps.Messages.INPUT_SCHEMA_TITLE;
import static com.snaplogic.snaps.Messages.SCHEMA_DESC;
import static com.snaplogic.snaps.Messages.SCHEMA_LABEL;
import static com.snaplogic.snaps.Messages.SUCCESS;


@General(title = INPUT_SCHEMA_TITLE, purpose = INPUT_SCHEMA_PURPOSE,
        author = INPUT_SCHEMA_AUTHOR, docLink = INPUT_SCHEMA_DOC)
@Inputs(min = 0, max = 1, accepts = {ViewType.DOCUMENT})
@Outputs(min = 1, max = 1, offers = {ViewType.DOCUMENT})
@Version(snap = 1)
@Category(snap = SnapCategory.READ)
public class InputSchemaSnap extends SimpleSnap implements InputSchemaProvider {


    private static final String SCHEMA_KEY = "schema";
    private static final String INTEGER = "int";
    private static final String STRING = "string";
    private static final String BOOLEAN = "boolean";
    private static final Map<String, SnapType> TYPES = ImmutableMap.of(
            STRING, SnapType.STRING,
            INTEGER, SnapType.INTEGER,
            BOOLEAN, SnapType.BOOLEAN
    );
    private String schema;
    private Throwable error;

    @Override
    public void defineProperties(final PropertyBuilder propertyBuilder) {
        super.defineProperties(propertyBuilder);
            propertyBuilder.describe(SCHEMA_KEY, SCHEMA_LABEL, SCHEMA_DESC)
                    .required()
                    .uiRowCount(10)
                    .add();

    }

    @Override
    public void configure(final PropertyValues propertyValues) throws ConfigurationException {
        super.configure(propertyValues);
        schema = propertyValues.get(SCHEMA_KEY);
    }

    @Override
    protected void process(final Document document, final String s) {
        outputViews.write(documentUtility.newDocument(error == null ? SUCCESS : error.getMessage()));
    }

    @Override
    public void defineInputSchema(final SchemaProvider schemaProvider) {
        try {
            Collection<String> inputViewNames = schemaProvider.getRegisteredInputViewNames();
            for (final String inputViewName : inputViewNames) {
                SchemaBuilder schemaBuilder = schemaProvider.getSchemaBuilder(inputViewName);
                String[] keyValues = schema.split(System.lineSeparator());
                for (final String keyValue : keyValues) {
                    String[] split = keyValue.split("\\s+");
                    schemaBuilder.withChildSchema(schemaProvider.createSchema(TYPES.get(split[1]),
                            split[0]));
                }
                schemaBuilder.build();
            }
        } catch (Throwable t){
            error = t;
        }
    }
}
