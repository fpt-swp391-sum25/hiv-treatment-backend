package backend.authentication.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.Value;

import backend.authentication.dto.GoogleTokenResponse;

@Service
public class GoogleAuthService {

    @Value("${google.client-id}")
    private String clientId;

    @Value("${google.client-secret}")
    private String clientSecret;

    @Value("${google.redirect-uri}")
    private String redirectUri;

    private static final GsonFactory gsonFactory = GsonFactory.getDefaultInstance();

    public GoogleTokenResponse exchangeCodeForToken(String code) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", "352858603517-ntvardeqch50ati93mpfjgl2aqqaf8qp.apps.googleusercontent.com");
        params.add("client_secret", "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");
        params.add("redirect_uri", "http://localhost:3000");
        params.add("grant_type", "authorization_code");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<GoogleTokenResponse> response = restTemplate.exchange(
                "https://oauth2.googleapis.com/token",
                HttpMethod.POST,
                request,
                GoogleTokenResponse.class);

        return response.getBody();
    }

    public GoogleIdToken.Payload parseIdToken(String idTokenString) {
        try {
            GoogleIdToken idToken = GoogleIdToken.parse(gsonFactory, idTokenString);
            return idToken.getPayload();
        } catch (Exception e) {
            throw new RuntimeException("Không thể phân tích id_token từ Google", e);
        }
    }
}
