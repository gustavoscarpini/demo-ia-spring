package com.example.demoiaspring.aiservice;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import jakarta.annotation.Resource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * This is an example of using an {@link AiService}, a high-level LangChain4j API.
 */
@RestController
class AssistantController {

    AssistantTools assistantTools;
    Assistant assistant;

    AssistantController(AssistantTools assistantTools) throws IOException {
        this.assistantTools = assistantTools;
        File resourceFile = new ClassPathResource("documents").getFile();


        String absolutePath = resourceFile.getAbsolutePath();

        List<Document> documents = FileSystemDocumentLoader.loadDocumentsRecursively(absolutePath);

        ChatLanguageModel chatModel = OllamaChatModel.builder()
                .modelName("vendedor001:latest")
                .baseUrl("http://localhost:11434")
                .build();

        InMemoryEmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
        EmbeddingStoreIngestor.ingest(documents, embeddingStore);

        this.assistant = AiServices.builder(Assistant.class)
                .chatLanguageModel(chatModel)
                .tools(assistantTools)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .contentRetriever(EmbeddingStoreContentRetriever.from(embeddingStore))
                .build();
    }

    @GetMapping("/assistant")
    public String assistant(@RequestParam(value = "message", defaultValue = "What is the time now?") String message) {
        return assistant
                .chat(message);
    }
}
