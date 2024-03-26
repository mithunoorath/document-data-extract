package com.mi.ai.solutions.documentdataextract.ingest;

import java.io.FileInputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.core.io.ClassPathResource;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;

import static dev.langchain4j.data.document.loader.FileSystemDocumentLoader.loadDocument;

public class EmbedVectorDataServiceImpl implements EmbedVectorDataService{

    @Override
    public void ingest(String fileName) {
        try{
            Document document = loadDocument(toPath(fileName), new ApachePdfBoxDocumentParser());

        }catch(Exception e){

        }
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'loadDocument'");
    }

    @Override
    public void loadData(FileInputStream fileInputStream) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'loadData'");
    }

    private static Path toPath(String fileName) {
        try {
            URL fileUrl = ClassPathResource.class.getResource(fileName);
            return Paths.get(fileUrl.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

}
