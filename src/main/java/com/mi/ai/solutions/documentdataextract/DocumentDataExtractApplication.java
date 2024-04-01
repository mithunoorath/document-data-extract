package com.mi.ai.solutions.documentdataextract;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.mi.ai.solutions.documentdataextract.scannedtosearchable.DataExtractionService;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import jakarta.annotation.PostConstruct;

import static dev.langchain4j.data.document.loader.FileSystemDocumentLoader.loadDocument;
import static dev.langchain4j.data.document.loader.FileSystemDocumentLoader.loadDocuments;

@SpringBootApplication
public class DocumentDataExtractApplication {

	@Autowired
	private DataExtractionService dataExtractionService;
    private final EmbeddingStoreIngestor embeddingStoreIngestor;

    public DocumentDataExtractApplication(EmbeddingStoreIngestor embeddingStoreIngestor) {
        this.embeddingStoreIngestor = embeddingStoreIngestor;
    }

    @PostConstruct
    public void init() throws IOException {
//		dataExtractionService.process("C:\\workspace\\Innovation\\document-data-extract\\src\\main\\resources\\pdfs\\Real1.pdf", "C:\\workspace\\Innovation\\document-data-extract\\src\\main\\resources\\pdfs\\searchablePdfs\\Real1-mod.pdf");
//		dataExtractionService.process("C:\\workspace\\Innovation\\document-data-extract\\src\\main\\resources\\pdfs\\labcorp-form3.pdf", "C:\\workspace\\Innovation\\document-data-extract\\src\\main\\resources\\pdfs\\searchablePdfs\\labcorp-form3-mod.pdf");
//		dataExtractionService.process("C:\\workspace\\Innovation\\document-data-extract\\src\\main\\resources\\pdfs\\labcorp-form4.pdf", "C:\\workspace\\Innovation\\document-data-extract\\src\\main\\resources\\pdfs\\searchablePdfs\\labcorp-form4-mod.pdf");
//
//

        List<Document> documents = loadDocuments("C:\\workspace\\Innovation\\document-data-extract\\src\\main\\resources\\pdfs\\searchablePdfs", new ApachePdfBoxDocumentParser());
        embeddingStoreIngestor.ingest(documents);
    }
	public static void main(String[] args) {
		SpringApplication.run(DocumentDataExtractApplication.class, args);
	}

}
