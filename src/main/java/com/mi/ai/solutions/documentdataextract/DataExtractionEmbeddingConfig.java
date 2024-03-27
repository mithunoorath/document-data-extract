package com.mi.ai.solutions.documentdataextract;

import dev.langchain4j.chain.ConversationalRetrievalChain;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.model.bedrock.BedrockAI21LabsChatModel;
import dev.langchain4j.model.embedding.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.retriever.EmbeddingStoreRetriever;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.cassandra.AstraDbEmbeddingConfiguration;
import dev.langchain4j.store.embedding.cassandra.AstraDbEmbeddingStore;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.regions.Region;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;


@Configuration
public class DataExtractionEmbeddingConfig {
    @Bean
    public EmbeddingModel embeddingModel() {
        return new AllMiniLmL6V2EmbeddingModel();
    }

    @Bean
    public AstraDbEmbeddingStore astraDbEmbeddingStore() {
        String astraToken = "xxxxx";
        String databaseId = "xxxxx";

        return new AstraDbEmbeddingStore(AstraDbEmbeddingConfiguration
                .builder()
                .token(astraToken)
                .databaseId(databaseId)
                .databaseRegion("us-east1")
                .keyspace("default_keyspace")
                .table("pdf_db_exp")
                .dimension(384)
                .build());
    }

    @Bean
    public EmbeddingStoreIngestor embeddingStoreIngestor() {
        return EmbeddingStoreIngestor.builder()
                .documentSplitter(DocumentSplitters.recursive(300, 0))
                .embeddingModel(embeddingModel())
                .embeddingStore(astraDbEmbeddingStore())
                .build();
    }

    @Bean
    @Primary
    public ConversationalRetrievalChain conversationalRetrievalChain() {
        return ConversationalRetrievalChain.builder()
                .chatLanguageModel(OpenAiChatModel.withApiKey("xxxxxxxx"))
                .promptTemplate(new PromptTemplate("{{question}}\n\nBase your answer on the following information:\n{{information}} Given the following extracted parts of a long document and a question, create a final answer with references links (\"SOURCES\"). \n" +
                "If the question does not refer to the data provided with the extracts then don't provide the sources and just reply with \"I don't know the answer to this question\". Don't try to make up an answer.\n" +
                "ALWAYS return a \"SOURCES\" part in your answer. Provide the response in html format like <p><p/> with proper paragraph, line breaks, headings etc., in maximum 50 words."
                ))
                .retriever(EmbeddingStoreRetriever.from(astraDbEmbeddingStore(), embeddingModel()))
                .build();
    }

    @Bean
    public ConversationalRetrievalChain conversationalRetrievalChainAws() {
        return ConversationalRetrievalChain.builder()
                .chatLanguageModel(BedrockAI21LabsChatModel.builder()
                .credentialsProvider(()-> AwsBasicCredentials.create("xxxxx", "xx+x"))
                .region(Region.US_EAST_1)
                .humanPrompt("Key in your question")
                .model(BedrockAI21LabsChatModel.Types.J2MidV2).build())
                .promptTemplate(new PromptTemplate("{{question}}\n\nBase your answer on the following information:\n{{information}} Given the following extracted parts of a long document and a question, create a final answer with references links (\"SOURCES\"). \n" +
                "If the question does not refer to the data provided with the extracts then don't provide the sources and just reply with \"I don't know the answer to this question\". Don't try to make up an answer.\n" +
                "ALWAYS return a \"SOURCES\" part in your answer. Provide the response in html format like <p><p/> with proper paragraph, line breaks, headings etc., in maximum 50 words."))
                .retriever(EmbeddingStoreRetriever.from(astraDbEmbeddingStore(), embeddingModel()))
                .build();
    }
}
