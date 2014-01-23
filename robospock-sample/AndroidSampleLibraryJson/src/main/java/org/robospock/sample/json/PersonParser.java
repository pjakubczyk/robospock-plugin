package org.robospock.sample.json;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Created by Przemek Jakubczyk on 1/23/14.
 */
public class PersonParser {


    public static Person[] parseSample() {
        return parse(Person.SAMPLE);
    }

    public static Person[] parse(String source) {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonFactory jsonFactory = objectMapper.getFactory();

        try {
            JsonParser jp = jsonFactory.createParser(source);

            return objectMapper.readValue(jp, Person[].class);
        } catch (IOException e) {
            return null;
        }
    }
}
