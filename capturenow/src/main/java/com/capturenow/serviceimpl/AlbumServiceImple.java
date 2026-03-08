package com.capturenow.serviceimpl;

import com.capturenow.config.ImageUtils;
import com.capturenow.module.Albums;
import com.capturenow.module.Photographer;
import com.capturenow.repository.AlbumRepo;
import com.capturenow.repository.PhotographerRepo;
import com.capturenow.service.AlbumService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Log4j2
@Service
public class AlbumServiceImple implements AlbumService {

    @Autowired
    private AlbumRepo albumRepo;

    @Autowired
    private PhotographerRepo repo;

    @Autowired
    private StorageService storageService;


    @Override
    public List<Albums> saveAlbum(MultipartFile[] file, String category, String photographerName) throws Exception {

        List<Albums> album = new ArrayList<Albums>(file.length);
        Photographer photographer = repo.findByEmail(photographerName);
        if (photographer != null) {
            for (MultipartFile photo : file) {
                Albums a = storageService.uploadeImage(photo, category, photographer);
                album.add(a);
                albumRepo.save(a);
            }
//            photographer.setAlbums(album);
//            repo.save(photographer);
            return album;
        }
        return null;
    }

    @Override
    public Page<Albums> downloadEquipments(String id, int offset, int pageSize) {
        List<Albums> ab = new ArrayList<>();
        Optional<Photographer> p = repo.findById(id);
        if (p.isEmpty()) {
            return new PageImpl<>(ab, PageRequest.of(offset / pageSize, pageSize), 0);
        }
        List<Albums> album = p.get().getAlbums();
        for (Albums a : album) {
            if (a.getCategory().equals("equipment")) {
                a.setPhoto(ImageUtils.decompressImage(a.getPhoto()));
                ab.add(a);
            }
        }
        Pageable pageable = PageRequest.of(offset / pageSize, pageSize);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), ab.size());
        List<Albums> pagedList = ab.subList(start, end);
        return new PageImpl<>(pagedList, pageable, ab.size());
    }

    @Override
    public Page<Albums> downloadAlbum(String id, int offset, int pageSize) {
        List<Albums> ab = new ArrayList<>();
        Optional<Photographer> p = repo.findById(id);
        if (p.isEmpty()) {
            return new PageImpl<>(ab, PageRequest.of(offset / pageSize, pageSize), 0);
        }
        List<Albums> album = p.get().getAlbums();
        for (Albums a : album) {
            if (!a.getCategory().equals("equipment")) {
                a.setPhoto(ImageUtils.decompressImage(a.getPhoto()));
                ab.add(a);
            }
        }
        Pageable pageable = PageRequest.of(offset / pageSize, pageSize);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), ab.size());
        List<Albums> pagedList = ab.subList(start, end);
        return new PageImpl<>(pagedList, pageable, ab.size());
    }

    @Override
    @Transactional
    public String deleteAlbumById(String  id) {
        try {
            // Find the album with the given ID
            Optional<Albums> album = albumRepo.findById(id);

            // Check if the album exists
            if (album.isPresent()) {
                // Get the associated photographer
                Photographer photographer = album.get().getPhotographer();

                // Delete the album
                albumRepo.deleteById(album.get().getId());

                // **If necessary, update the photographer object here and save it.**
                // Make sure to document the specific changes and their purpose.

                return "Album with ID " + id + " deleted successfully.";
            } else {
                return "Album with ID " + id + " does not exist.";
            }
        } catch (EntityNotFoundException e) {
            // Handle specific exception for missing album
            return "Album with ID " + id + " not found.";
        } catch (Exception e) {
            // Handle other potential exceptions
            log.error("Error deleting album:", e);
            return "Error deleting album: " + e.getMessage();
        }
    }
}
