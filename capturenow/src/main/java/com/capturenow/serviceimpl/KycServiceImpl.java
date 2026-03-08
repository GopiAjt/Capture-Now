package com.capturenow.serviceimpl;

import com.capturenow.config.ImageUtils;
import com.capturenow.module.Photographer;
import com.capturenow.module.PhotographerKycDetails;
import com.capturenow.repository.PhotographerKycRepo;
import com.capturenow.repository.PhotographerRepo;
import com.capturenow.service.KycService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class KycServiceImpl implements KycService {

    @Autowired
    private PhotographerRepo photographerRepo;

    @Autowired
    private PhotographerKycRepo photographerKycRepo;

    @Override
    public String addKycDetails(Long bankAccountNumber, String ifscCode, MultipartFile idProofImage, MultipartFile studioLicence, String emailId) throws Exception {
        Photographer photographer = photographerRepo.findByEmail(emailId);
        if (photographer != null){
            PhotographerKycDetails photographerKycDetails = new PhotographerKycDetails();
            photographerKycDetails.setKycStatus(false);
            photographerKycDetails.setIfscCode(ifscCode);
            photographerKycDetails.setBankAccountNumber(bankAccountNumber);
            photographerKycDetails.setIdProofImage(ImageUtils.compressImage(idProofImage.getBytes()));
            if (studioLicence != null && !studioLicence.isEmpty()) {
                photographerKycDetails.setStudioLicence(ImageUtils.compressImage(studioLicence.getBytes()));
            }
            photographerKycDetails.setPhotographer(photographer);
            photographerKycRepo.save(photographerKycDetails);
            return "details added successfully";
        }
        return null;
    }
}
