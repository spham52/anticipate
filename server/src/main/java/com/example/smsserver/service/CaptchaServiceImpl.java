package com.example.smsserver.service;

// class used to verify Google's ReCaptcha
import com.example.smsserver.dto.captcha.RecaptchaResponse;
import com.example.smsserver.exception.GoogleRecaptchaFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class CaptchaServiceImpl implements CaptchaService {
    private final String GOOGLE_RECAPTCHA_URL = "https://www.google.com/recaptcha/api/siteverify";
    private final WebClient webClient;

    @Value("${recaptcha.secret-key}")
    private String GOOGLE_RECAPTCHA_SECRET;

    @Autowired
    public CaptchaServiceImpl(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    // used to verify captcha code received on frontend is valid
    // posts to google's recaptcha endpoint
    public void checkCaptcha(String captcha) {
        RecaptchaResponse response;
        try {
            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.add("secret", GOOGLE_RECAPTCHA_SECRET);
            formData.add("response", captcha);

            response = webClient.post()
                    .uri(GOOGLE_RECAPTCHA_URL)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .bodyValue(formData)
                    .retrieve()
                    .bodyToMono(RecaptchaResponse.class)
                    .block();
        } catch (Exception e) {
            throw new GoogleRecaptchaFailedException();
        }

        if (response == null || !response.isSuccess()) {
            throw new GoogleRecaptchaFailedException();
        }
    }
}
