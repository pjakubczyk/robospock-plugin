package org.robospock.sample.json;

/**
 * Created by Przemek Jakubczyk on 1/23/14.
 */
public class Person {

    private String name;
    private String lastName;

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return Integer.toHexString((name + lastName).hashCode());
    }

    public String getFullName() {
        return name + lastName;
    }


    public static final String SAMPLE = "[\n" +
            "{\n" +
            "\"name\":\"John\",\n" +
            "\"lastName\":\"White\"\n" +
            "},\n" +
            "{\n" +
            "\"name\":\"Mark\",\n" +
            "\"lastName\":\"Yellow\"\n" +
            "},\n" +
            "{\n" +
            "\"name\": \"Jane\",\n" +
            "\"lastName\":\"Black\"\n" +
            "}\n" +
            "]\n";
}
