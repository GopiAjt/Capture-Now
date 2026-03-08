package com.capturenow.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface KycService {

    String addKycDetails(Long bankAccountNumber, String ifscCode, MultipartFile idProofImage, MultipartFile studioLicence, String emailId) throws Exception;
}
