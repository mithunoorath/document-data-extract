package com.mi.ai.solutions.documentdataextract.scannedtosearchable;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.util.CredentialUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;
import org.springframework.stereotype.Service;

import com.amazonaws.services.textract.AmazonTextract;
import com.amazonaws.services.textract.AmazonTextractClientBuilder;
import com.amazonaws.services.textract.model.Block;
import com.amazonaws.services.textract.model.BoundingBox;
import com.amazonaws.services.textract.model.DetectDocumentTextRequest;
import com.amazonaws.services.textract.model.DetectDocumentTextResult;
import com.amazonaws.services.textract.model.Document;
import com.mi.ai.solutions.model.ImageType;
import com.mi.ai.solutions.model.TextLine;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;

@Service
@Slf4j
public class DataExtractionServiceImpl implements DataExtractionService {

    @Override
    public List<TextLine> extractDataFromBuffer(ByteBuffer byteBuffer) {

        AmazonTextract client = AmazonTextractClientBuilder
//                .defaultClient();
                .standard()
                .withRegion(Regions.US_EAST_1)
//                .withClientConfiguration(new ProfileCredentialsProvider().getCredentials())
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials("xxx", "xxxxx")))

//                .withRegion(Region.US_EAST_1.toString())
                .build();
        DetectDocumentTextRequest request = new DetectDocumentTextRequest()
                .withDocument(new Document()
                        .withBytes(byteBuffer));

        DetectDocumentTextResult result = client.detectDocumentText(request);

        List<TextLine> lines = new ArrayList<TextLine>();
        List<Block> blocks = result.getBlocks();
        BoundingBox boundingBox = null;
        for (Block block : blocks) {
            if ((block.getBlockType()).equals("LINE")) {
                boundingBox = block.getGeometry().getBoundingBox();
                lines.add(new TextLine(boundingBox.getLeft(),
                        boundingBox.getTop(),
                        boundingBox.getWidth(),
                        boundingBox.getHeight(),
                        block.getText()));
            }
        }

        return lines;
    }

    @Override
    public boolean process(String fileNameToProcess, String outputFileName) {
        try{
            PDFDocument pdfDocument = new PDFDocument();

            List<TextLine> lines = null;
            BufferedImage image = null;
            ByteArrayOutputStream byteArrayOutputStream = null;
            ByteBuffer imageBytes = null;
    
            // Load pdf document and process each page as image
            PDDocument inputDocument = PDDocument.load(new File(fileNameToProcess));
            PDFRenderer pdfRenderer = new PDFRenderer(inputDocument);
            for (int page = 0; page < inputDocument.getNumberOfPages(); ++page) {
    
                // Render image
                image = pdfRenderer.renderImageWithDPI(page, 300, org.apache.pdfbox.rendering.ImageType.RGB);
    
                // Get image bytes
                byteArrayOutputStream = new ByteArrayOutputStream();
                ImageIOUtil.writeImage(image, "jpeg", byteArrayOutputStream);
                byteArrayOutputStream.flush();
                imageBytes = ByteBuffer.wrap(byteArrayOutputStream.toByteArray());
    
                // Extract text
                lines = extractDataFromBuffer(imageBytes);
    
                // Add extracted text to pdf page
                pdfDocument.addPage(image, ImageType.JPEG, lines);
    
    
                System.out.println("Processed page index: " + page);
            }
    
            inputDocument.close();
        
            // Save PDF to local disk
            try (OutputStream outputStream = new FileOutputStream(outputFileName)) {
                pdfDocument.save(outputStream);
                pdfDocument.close();
            }
    
            System.out.println("Generated searchable pdf: " + outputFileName);    
            return true;
        }
        catch(Exception e){
            log.error("Error incurred {}", e.getLocalizedMessage());
            return false;
        }
        
    }

}
