# AI Document Q&A System

A full-stack RAG (Retrieval Augmented Generation) application that lets users upload PDF documents and ask natural language questions about them. Built with Spring Boot, React, Supabase (pgvector), Voyage AI, and the Claude API.

---


## Architecture Overview

```
React Frontend
    │
    ├── POST /api/document/upload  ──►  DocumentController
    │                                       │
    │                                       ▼
    │                                  DocumentService
    │                                       │
    │                              ┌────────┼────────┐
    │                              ▼        ▼        ▼
    │                           PDFBox   Chunker  EmbeddingService
    │                                             (Voyage AI)
    │                                             │
    │                                             ▼
    │                                       Supabase (pgvector)
    │
    └── POST /api/document/query  ──►  QueryController
                                           │
                                           ▼
                                      QueryService
                                           │
                                  ┌────────┼────────┐
                                  ▼        ▼        ▼
                           EmbeddingService  DocumentChunk  ClaudeService
                           (embed question)  Repository    (Anthropic API)
                                             (vector search)
```

---

## Tech Stack

| Layer | Technology |
|---|---|
| Frontend | React + TypeScript + Vite |
| Backend | Spring Boot 4 (Java 26) |
| Database | Supabase (PostgreSQL + pgvector) |
| Embeddings | Voyage AI (`voyage-4-lite`) |
| AI | Anthropic Claude (`claude-sonnet-4-6`) |
| PDF Parsing | Apache PDFBox 3.x |
| ORM | Spring Data JPA + Hibernate |
| HTTP Client | Java `HttpClient` |
| JSON | Jackson `ObjectMapper` |

---

## Project Structure

```
backend/
└── src/main/java/com/document_QA/demo/
    ├── DemoApplication.java
    ├── controller/
    │   └── QA_Controller.java          # REST endpoints for upload and query
    ├── service/
    │   ├── DocumentService.java        # Upload pipeline orchestration
    │   ├── EmbeddingService.java       # Voyage AI embedding API calls
    │   ├── QueryService.java           # Query pipeline orchestration
    │   └── ClaudeService.java          # Anthropic Claude API calls
    ├── repository/
    │   └── DocumentChunkRepository.java  # JPA repository + pgvector query
    ├── model/
    │   ├── DocumentChunk.java          # JPA entity for document_chunks table
    │   ├── QueryRequest.java           # Request DTO for query endpoint
    │   ├── VoyageAiResponse.java       # Voyage AI response model
    │   └── ClaudeAiResponse.java       # Claude API response model
    └── config/
        └── CorsConfig.java             # CORS configuration for React dev server

frontend/
└── src/
    ├── App.tsx                         # React Router setup
    ├── components/
    │   ├── Upload.tsx                  # PDF upload with drag and drop
    │   └── Query.tsx                   # Chat interface for Q&A
```

---

## Upload Pipeline

When a user uploads a PDF the following happens:

1. **React** sends a `multipart/form-data` POST to `/api/document/upload`
2. **DocumentController** receives the `MultipartFile` and calls `DocumentService.processDocument()`
3. **PDFBox** extracts raw text from the PDF using `Loader.loadPDF()` and `PDFTextStripper`
4. **Chunker** splits the text into overlapping chunks (500 tokens, 50 token overlap) using `String.substring()`
5. **EmbeddingService** sends the full list of chunks to Voyage AI's `/v1/embeddings` endpoint in a single batched request
6. **DocumentService** converts the returned `List<Float>` vectors to `float[]` arrays and builds `DocumentChunk` entities
7. **DocumentChunkRepository** saves all chunks to Supabase via `saveAll()`
8. React automatically navigates to the query page

---

## Query Pipeline

When a user asks a question:

1. **React** sends a POST to `/api/document/query` with `{ "question": "..." }`
2. **QueryController** calls `QueryService.buildQuery()`
3. **EmbeddingService** embeds the question into a single vector using the same Voyage AI model
4. **DocumentChunkRepository** runs a pgvector cosine similarity search (`<=>`) against all stored embeddings, returning the 5 most relevant chunks
5. **QueryService** concatenates those chunks into a context string and appends the question
6. **ClaudeService** sends the assembled prompt to the Claude API with a system prompt instructing it to answer only from the provided context
7. The answer is returned to React and displayed in the chat interface

---

## Database Schema

```sql
CREATE EXTENSION IF NOT EXISTS vector;

CREATE TABLE document_chunks (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    document_name TEXT,
    chunk_text    TEXT,
    embedding     vector(1024)
);
```

The `embedding` column uses pgvector's `vector` type with 1024 dimensions matching Voyage AI's `voyage-4-lite` output.

---

## Environment Variables

Create `src/main/resources/application.properties` (never commit this file):

```properties
spring.datasource.url=your_supabase_jdbc_url
spring.datasource.username=your_supabase_username
spring.datasource.password=your_supabase_password
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.hibernate.ddl-auto=validate
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

voyageai.api.key=your_voyage_api_key
claude.api.key=your_anthropic_api_key
```

---

## Running Locally

**Backend**
```bash
cd demo
# Run via VS Code Spring Boot Dashboard or:
./mvnw spring-boot:run
# Starts on http://localhost:8080
```

**Frontend**
```bash
cd QA-React-Frontend
npm install
npm run dev
# Starts on http://localhost:5173
```

---
