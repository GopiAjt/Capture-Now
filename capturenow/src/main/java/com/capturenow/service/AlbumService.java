package com.capturenow.service;

import com.capturenow.module.Albums;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AlbumService {
    String deleteAlbumById(String id);

    List<Albums> saveAlbum(MultipartFile[] file, String category, String photographerName) throws Exception;

    Page<Albums> downloadAlbum(String email, int offset, int pageSize);

    Page<Albums> downloadEquipments(String email, int offset, int pageSize);
}
