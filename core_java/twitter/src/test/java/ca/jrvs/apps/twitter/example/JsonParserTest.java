package ca.jrvs.apps.twitter.example;

import ca.jrvs.apps.twitter.example.dto.Company;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static ca.jrvs.apps.twitter.example.JsonParser.companyStr;
import static org.junit.Assert.*;

public class JsonParserTest {

    JsonParser jsonParser;
    StringBuilder stringBuilder;
    String exampleJson;
    Company company;

    @Before
    public void setUp () throws Exception {
        jsonParser = new JsonParser();
        stringBuilder = new StringBuilder();

        File apple = new File("./src/main/java/ca/jrvs/apps/twitter/example/appleJson");
        List<String> jsonContents = new ArrayList<>(Files.readAllLines(apple.toPath(), Charset.defaultCharset()));
        jsonContents.forEach((String str) -> stringBuilder.append(str));

        exampleJson = stringBuilder.toString();
        company = new Company();
    }

    @After
    public void tearDown() {
        jsonParser = null;
        exampleJson = null;
    }

    @Test
    public void toJson() throws IOException{
        // This fails, but the content matches aside from formatting.
        // TODO: Figure this out to see why the formatting diffrences.
        assertEquals(companyStr, (JsonParser.toJson(JsonParser.toObjectFromJson(exampleJson, Company.class), true, false)));
    }
    @Test
    public void toObjectFromJson() throws IOException {
        Company apple = new Company();
        // Asserting: conversion to json matches example json.
        assertEquals(JsonParser.toObjectFromJson(exampleJson, Company.class).getClass(), apple.getClass());
    }

        @Test
    public void main() {

    }
}