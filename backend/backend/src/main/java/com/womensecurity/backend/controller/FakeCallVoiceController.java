package com.womensecurity.backend.controller;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/fake-call")
@CrossOrigin(origins = "*")
public class FakeCallVoiceController {

    @Value("${elevenlabs.api.key}")
    private String apiKey;

    private final HttpClient httpClient =
            HttpClient.newHttpClient();

    @PostMapping(
            value = "/voice",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = "audio/mpeg"
    )
    public ResponseEntity<byte[]> generateVoice(
            @RequestBody VoiceRequest request) {

        try {

            String voiceId;

            if ("male".equalsIgnoreCase(request.gender())) {

                // Male voice
                voiceId = "JBFqnCBsd6RMkjVDRZzb";

            } else {

                // Female voice
                voiceId = "EXAVITQu4vr4xnSDxMaL";
            }

            String json = """
                    {
                        "text": "%s",
                        "model_id": "eleven_multilingual_v2",
                        "voice_settings": {
                            "stability": 0.45,
                            "similarity_boost": 0.80,
                            "style": 0.35,
                            "use_speaker_boost": true
                        }
                    }
                    """.formatted(
                            escapeJson(request.text())
                    );

            String url =
                    "https://api.elevenlabs.io/v1/text-to-speech/"
                    + voiceId;

            HttpRequest httpRequest =
                    HttpRequest.newBuilder()
                            .uri(URI.create(url))
                            .header(
                                    "xi-api-key",
                                    apiKey
                            )
                            .header(
                                    "Content-Type",
                                    "application/json"
                            )
                            .header(
                                    "Accept",
                                    "audio/mpeg"
                            )
                            .POST(
                                    HttpRequest.BodyPublishers
                                            .ofString(json)
                            )
                            .build();

            HttpResponse<byte[]> response =
                    httpClient.send(
                            httpRequest,
                            HttpResponse.BodyHandlers.ofByteArray()
                    );
                    byte[] audioBytes = response.body();
                    System.out.println(
    "Audio signature: " +
    java.util.HexFormat.of().formatHex(
        audioBytes,
        0,
        Math.min(audioBytes.length, 8)
    )
);

System.out.println("ElevenLabs HTTP status: " + response.statusCode());
System.out.println("Audio bytes received: " +
        (audioBytes == null ? 0 : audioBytes.length));

if (response.statusCode() == 200 &&
        (audioBytes == null || audioBytes.length < 4)) {
    return ResponseEntity.internalServerError().build();
}

          if (response.statusCode() != 200) {

    String errorBody =
            new String(response.body());

    System.out.println(
            "===================================="
    );

    System.out.println(
            "ELEVENLABS ERROR"
    );

    System.out.println(
            "HTTP STATUS: "
            + response.statusCode()
    );

    System.out.println(
            "ERROR BODY: "
            + errorBody
    );

    System.out.println(
            "===================================="
    );

    return ResponseEntity
            .status(response.statusCode())
            .body(response.body());
}

            return ResponseEntity.ok()
        .contentType(MediaType.parseMediaType("audio/mpeg"))
        .header("Cache-Control", "no-store")
        .body(audioBytes);

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .internalServerError()
                    .build();
        }
    }

    private String escapeJson(String text) {

        if (text == null) {
            return "";
        }

        return text
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }

    public record VoiceRequest(
            String text,
            String gender
    ) {
    }
}