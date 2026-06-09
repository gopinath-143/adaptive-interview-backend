package com.interview.service;

import java.io.InputStream;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service

public class ResumeParserServiceImpl
        implements ResumeParserService {

	private static final Logger log =
	        LoggerFactory.getLogger(ResumeParserServiceImpl.class);
    @Override
    public String extractText(MultipartFile file) {

        try {

            String fileName =
                    file.getOriginalFilename();

            if (fileName == null) {
                return "";
            }

            if (fileName.endsWith(".pdf")) {

                return parsePdf(file);
            }

            if (fileName.endsWith(".docx")) {

                return parseDocx(file);
            }

            return "";

        } catch (Exception e) {

            log.error("Resume Parsing Failed", e);

            return "";
        }
    }

    private String parsePdf(
            MultipartFile file) throws Exception {

        PDDocument document =
                Loader.loadPDF(
                        file.getBytes());

        PDFTextStripper stripper =
                new PDFTextStripper();

        return stripper.getText(document);
    }

    private String parseDocx(
            MultipartFile file) throws Exception {

        InputStream inputStream =
                file.getInputStream();

        XWPFDocument document =
                new XWPFDocument(inputStream);

        XWPFWordExtractor extractor =
                new XWPFWordExtractor(document);

        return extractor.getText();
    }
}