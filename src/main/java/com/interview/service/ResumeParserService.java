package com.interview.service;

import org.springframework.web.multipart.MultipartFile;

public interface ResumeParserService {

    String extractText(MultipartFile file);

}