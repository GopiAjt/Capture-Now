package com.capturenow.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@AllArgsConstructor
public class PhotographerCardDto {

    private String id;
    private String name;
    private String email;
    private String serviceLocation;
    private int experience;
    private String services;
    private String languages;
    private byte[] profilePhoto;
    private Double startsWith;
    private Double avgRating;
    // Constructor and other methods

    public PhotographerCardDto() {
    }

    // Builder Pattern
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private String name;
        private String mailId;
        private String serviceLocation;
        private int experience;
        private String services;
        private String languages;
        private byte[] profilePhoto;
        private Double startsWith;
        private Double avgRating;

        // Setter methods for the builder

        public Builder id(String id){
            this.id = id;
            return this;
        }
        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder avgRating(Double avgRating) {
            this.avgRating = avgRating;
            return this;
        }

        public Builder mailId(String mailId) {
            this.mailId = mailId;
            return this;
        }

        public Builder serviceLocation(String serviceLocation) {
            this.serviceLocation = serviceLocation;
            return this;
        }

        public Builder experience(int experience) {
            this.experience = experience;
            return this;
        }

        public Builder services(String services) {
            this.services = services;
            return this;
        }

        public Builder languages(String languages) {
            this.languages = languages;
            return this;
        }

        public Builder profilePhoto(byte[] profilePhoto) {
            this.profilePhoto = profilePhoto;
            return this;
        }

        public Builder startsWith(Double startsWith) {
            this.startsWith = startsWith;
            return this;
        }

        // Build method to create the PhotographerCardDto instance

        public PhotographerCardDto build() {
            PhotographerCardDto cardDto = new PhotographerCardDto();
            cardDto.setId(this.id);
            cardDto.setName(this.name);
            cardDto.setEmail(this.mailId);
            cardDto.setServiceLocation(this.serviceLocation);
            cardDto.setExperience(this.experience);
            cardDto.setServices(this.services);
            cardDto.setLanguages(this.languages);
            cardDto.setProfilePhoto(this.profilePhoto);
            cardDto.setStartsWith(this.startsWith);
            cardDto.setAvgRating(this.avgRating);
            return cardDto;
        }
    }
}
