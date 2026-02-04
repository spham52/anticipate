package com.example.smsserver.service;

// class used to verify Google's ReCaptcha
public interface CaptchaService {

    public void checkCaptcha(String captcha);
}
