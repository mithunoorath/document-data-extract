package com.mi.ai.solutions.documentdataextract.ingest;

import java.io.FileInputStream;

public interface EmbedVectorDataService {

    void ingest(String fileName);
    void loadData( FileInputStream fileInputStream);

}
