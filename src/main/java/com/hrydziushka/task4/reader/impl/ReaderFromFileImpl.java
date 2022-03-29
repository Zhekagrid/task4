package com.hrydziushka.task4.reader.impl;


import com.hrydziushka.task4.exception.CustomException;
import com.hrydziushka.task4.reader.ReaderFromFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class ReaderFromFileImpl implements ReaderFromFile {
    private static final Logger logger = LogManager.getLogger();

    @Override
    public List<String> readLinesFromFile(String filePath) throws CustomException {
        List<String> lines=new ArrayList<>();
        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource(filePath);

        if (resource == null) {
            logger.error("file does not exist{}", filePath);
            throw new CustomException("file " + filePath + " does not exist");
        }
        try (Stream<String> stream = Files.lines(new File(resource.toURI()).toPath())) {
            lines = stream.toList();

        } catch (IOException e) {
            logger.error("can't read file{}", filePath, e);
            throw new CustomException("can't read " + filePath, e);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }


        return lines;
    }
}
