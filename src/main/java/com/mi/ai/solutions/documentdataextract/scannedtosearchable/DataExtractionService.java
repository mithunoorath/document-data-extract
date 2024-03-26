package com.mi.ai.solutions.documentdataextract.scannedtosearchable;

import java.nio.ByteBuffer;
import java.util.List;

import com.mi.ai.solutions.model.TextLine;


public interface DataExtractionService {
    List<TextLine> extractDataFromBuffer(ByteBuffer byteBuffer);
    boolean process(String fileNameToProcess, String outputFileName);

}
