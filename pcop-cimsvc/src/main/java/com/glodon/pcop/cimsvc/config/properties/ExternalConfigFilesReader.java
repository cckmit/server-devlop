package com.glodon.pcop.cimsvc.config.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

@Component
public class ExternalConfigFilesReader {

    @Value("my.default-base.property-name-path")
    private String defaultBasePropertyPath;

    public static Set<String> defaultBaseDataSetNames = new HashSet<>();


    static {
        defaultBaseDataSetNames.add("ID");
        defaultBaseDataSetNames.add("NAME");
        defaultBaseDataSetNames.add("objecttypeID");
        defaultBaseDataSetNames.add("creator");
        defaultBaseDataSetNames.add("createtime");
        defaultBaseDataSetNames.add("updatetime");
        defaultBaseDataSetNames.add("x");
        defaultBaseDataSetNames.add("y");
        defaultBaseDataSetNames.add("z");
        defaultBaseDataSetNames.add("angle");
        defaultBaseDataSetNames.add("scaling");
        defaultBaseDataSetNames.add("version");
        defaultBaseDataSetNames.add("comment");
    }

    public Set<String> defaultBaseProperties() throws FileNotFoundException {
        Set<String> propertyNames = new HashSet<>();
        InputStream stream;
        File file = new File(defaultBasePropertyPath);
        if (file.exists()) {
            stream = new FileInputStream(file);
        } else {
            stream = getClass().getClassLoader().getResourceAsStream(defaultBasePropertyPath);
        }

        StringBuffer stringBuffer = new StringBuffer();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(stream, "UTF-8"))) {
            // br = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            String line;
            while ((line = br.readLine()) != null) {
                stringBuffer.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return propertyNames;
    }

}
