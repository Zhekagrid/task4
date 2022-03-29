package com.hrydziushka.task4.reader;


import com.hrydziushka.task4.exception.CustomException;

import java.util.List;

public interface ReaderFromFile {
    List<String > readLinesFromFile(String filePath) throws CustomException;
}
