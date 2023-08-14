package com.capturenow.serviceimpl;


import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.capturenow.config.ImageUtils;
import com.capturenow.module.Albums;
import com.capturenow.module.Photographer;
import com.capturenow.repository.AlbumRepo;

@Service
public class StorageService {

	@Autowired
	private AlbumRepo repo;

	public Albums uplodeImage(MultipartFile file, String category, Photographer name) throws Exception
	{
		Albums album = repo.save(Albums.builder()
				.name(file.getOriginalFilename())
				.type(file.getContentType())
				.category(category).photographer(name)
				.photo(ImageUtils.compressImage(file.getBytes())).build());
		if(album!=null)
		{
			return album;
		}
		return null;
	}

	public byte[] downloadImage(String fileName)
	{
		Optional<Albums> imageData = repo.findByName(fileName);
		byte[] image = ImageUtils.decompressImage(imageData.get().getPhoto());
		return image;
	}
}
