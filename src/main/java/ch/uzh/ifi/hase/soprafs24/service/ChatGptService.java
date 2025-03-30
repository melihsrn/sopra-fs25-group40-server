package ch.uzh.ifi.hase.soprafs24.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
public class ChatGptService {

    // TODO ' export OPENAI_API_KEY=your_actual_api_key_here ' add this to bashrc file,
    //  never NEVER push your api key to GitHub !!!
    @Value("${openai.api.key}")
    private String openaiApiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    // Updated endpoint as per your curl example
    private final String openaiEndpoint = "https://api.openai.com/v1/responses";

    /**
     * Calls the ChatGPT API with a given prompt and returns the raw JSON response.
     * The payload includes a constant system message and a dynamic user message.
     */
    public String generateFlashcards(String prompt, int numberOfCards) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openaiApiKey);

        // Constant system message content
        String systemText = "Create JSON-formatted flashcards with fields for the description, correct answer, and up to three wrong answers.\n\n" +
                "Flashcards typically consist of a question or description and options, including one correct and up to three incorrect answers. \n\n" +
                "# Steps\n\n" +
                "1. **Description**: Create a description or question for the flashcard.\n" +
                "2. **Correct Answer**: Determine and include the correct answer.\n" +
                "3. **Wrong Answers**: Generate one to three incorrect answers to provide alternatives.\n\n" +
                "# Output Format\n\n" +
                "Output the flashcards in JSON format with the following structure:\n\n" +
                "```json\n" +
                "{\n" +
                "  \"flashcards\": [\n" +
                "    {\n" +
                "      \"description\": \"Your question or description here\",\n" +
                "      \"answer\": \"Correct answer here\",\n" +
                "      \"wrong_answers\": [\"Wrong answer 1\", \"Wrong answer 2\", \"Wrong answer 3\"]\n" +
                "    },\n" +
                "    ...\n" +
                "  ]\n" +
                "}\n" +
                "```\n\n" +
                "# Examples\n\n" +
                "**Example 1**\n\n" +
                "_Input:_\n\n" +
                "- Description: \"What is the capital of France?\"\n" +
                "- Answer: \"Paris\"\n" +
                "- Wrong Answers: \"Berlin\", \"Madrid\", \"Rome\"\n\n" +
                "_Output:_\n\n" +
                "```json\n" +
                "{\n" +
                "  \"flashcards\": [\n" +
                "    {\n" +
                "      \"description\": \"What is the capital of France?\",\n" +
                "      \"answer\": \"Paris\",\n" +
                "      \"wrong_answers\": [\"Berlin\", \"Madrid\", \"Rome\"]\n" +
                "    }\n" +
                "  ]\n" +
                "}\n" +
                "```\n\n" +
                "**Example 2**\n\n" +
                "_Input:_\n\n" +
                "- Description: \"What is 2 + 2?\"\n" +
                "- Answer: \"4\"\n" +
                "- Wrong Answers: \"3\", \"5\", \"6\"\n\n" +
                "_Output:_\n\n" +
                "```json\n" +
                "{\n" +
                "  \"flashcards\": [\n" +
                "    {\n" +
                "      \"description\": \"What is 2 + 2?\",\n" +
                "      \"answer\": \"4\",\n" +
                "      \"wrong_answers\": [\"3\", \"5\", \"6\"]\n" +
                "    }\n" +
                "  ]\n" +
                "}\n" +
                "```\n\n" +
                "# Notes\n\n" +
                "- Ensure that the correct answer is always accurate.\n" +
                "- Variability in wrong answers can help in learning effectiveness.\n" +
                "- Consider the difficulty level and adapt the descriptions and wrong answers accordingly.";

        // Build system message input.
        Map<String, Object> systemInput = new HashMap<>();
        systemInput.put("role", "system");
        List<Map<String, Object>> systemContentList = new ArrayList<>();
        Map<String, Object> systemContentItem = new HashMap<>();
        systemContentItem.put("type", "input_text");
        systemContentItem.put("text", systemText);
        systemContentList.add(systemContentItem);
        systemInput.put("content", systemContentList);

        // Build user message input.
        Map<String, Object> userInput = new HashMap<>();
        userInput.put("role", "user");
        List<Map<String, Object>> userContentList = new ArrayList<>();
        Map<String, Object> userContentItem = new HashMap<>();
        // Prepend the number of flashcards to the user's prompt.
        String userText = numberOfCards + " flashcards on " + prompt;
        userContentItem.put("type", "input_text");
        userContentItem.put("text", userText);
        userContentList.add(userContentItem);
        userInput.put("content", userContentList);

        // Combine both system and user messages in the input array.
        List<Map<String, Object>> inputList = new ArrayList<>();
        inputList.add(systemInput);
        inputList.add(userInput);

        // Build the overall request payload.
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-4o-mini");
        requestBody.put("input", inputList);

        // Build the "text" section with the JSON schema (same as before).
        Map<String, Object> descriptionSchema = new HashMap<>();
        descriptionSchema.put("type", "string");
        descriptionSchema.put("description", "The question or description for the flashcard.");

        Map<String, Object> answerSchema = new HashMap<>();
        answerSchema.put("type", "string");
        answerSchema.put("description", "The correct answer for the flashcard.");

        Map<String, Object> wrongAnswersSchema = new HashMap<>();
        wrongAnswersSchema.put("type", "array");
        wrongAnswersSchema.put("items", Map.of("type", "string"));
        wrongAnswersSchema.put("description", "An array of incorrect answer options.");

        Map<String, Object> flashcardItemSchema = new HashMap<>();
        flashcardItemSchema.put("type", "object");
        flashcardItemSchema.put("required", List.of("description", "answer", "wrong_answers"));
        Map<String, Object> flashcardProperties = new HashMap<>();
        flashcardProperties.put("description", descriptionSchema);
        flashcardProperties.put("answer", answerSchema);
        flashcardProperties.put("wrong_answers", wrongAnswersSchema);
        flashcardItemSchema.put("properties", flashcardProperties);
        flashcardItemSchema.put("additionalProperties", false);

        Map<String, Object> flashcardsSchema = new HashMap<>();
        flashcardsSchema.put("type", "array");
        flashcardsSchema.put("items", flashcardItemSchema);
        flashcardsSchema.put("description", "An array of flashcard objects containing questions and answers.");

        Map<String, Object> schemaProperties = new HashMap<>();
        schemaProperties.put("flashcards", flashcardsSchema);

        Map<String, Object> schema = new HashMap<>();
        schema.put("type", "object");
        schema.put("required", List.of("flashcards"));
        schema.put("properties", schemaProperties);
        schema.put("additionalProperties", false);

        Map<String, Object> format = new HashMap<>();
        format.put("type", "json_schema");
        format.put("name", "flashcards");
        format.put("schema", schema);
        format.put("strict", true);

        Map<String, Object> textSection = new HashMap<>();
        textSection.put("format", format);
        requestBody.put("text", textSection);

        // Add additional parameters.
        requestBody.put("reasoning", new HashMap<>());
        requestBody.put("tools", new ArrayList<>());
        requestBody.put("temperature", 1);
        requestBody.put("max_output_tokens", 2048);
        requestBody.put("top_p", 1);
        requestBody.put("store", true);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(openaiEndpoint, entity, String.class);

        // Print the raw response for debugging.
//        System.out.println("ChatGPT API response: " + response.getBody());

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error calling ChatGPT API: " + response.getStatusCode());
        }
    }

    /**
     * Extracts the generated flashcards JSON from the API response.
     * Expects the output to be a JSON object containing the "flashcards" key.
     */
    public String extractGeneratedText(String jsonResponse) {
        // Print raw JSON for debugging.
//        System.out.println("Raw ChatGPT response: " + jsonResponse);
        try {
            JsonNode root = objectMapper.readTree(jsonResponse);
            JsonNode output = root.path("output");
            if (output.isArray() && output.size() > 0) {
                JsonNode firstMessage = output.get(0);
                JsonNode content = firstMessage.path("content");
                if (content.isArray() && content.size() > 0) {
                    JsonNode firstContent = content.get(0);
                    String text = firstContent.path("text").asText();
                    // Optionally print the extracted text.
//                    System.out.println("Extracted text: " + text);
                    // Validate that the text contains a "flashcards" key.
                    JsonNode flashcardsJson = objectMapper.readTree(text);
                    if (flashcardsJson.has("flashcards")) {
                        return text;
                    } else {
                        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                                "The extracted text does not contain a 'flashcards' key: " + text);
                    }
                }
            }
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Invalid response structure from ChatGPT: " + jsonResponse);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error parsing ChatGPT response: " + e.getMessage(), e);
        }
    }

}
