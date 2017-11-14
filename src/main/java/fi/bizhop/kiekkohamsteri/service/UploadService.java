package fi.bizhop.kiekkohamsteri.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.cloudinary.Cloudinary;
import com.cloudinary.Singleton;

import fi.bizhop.kiekkohamsteri.dto.UploadDto;

@Service
public class UploadService {
	public void upload(UploadDto dto, String name) throws IOException {
		Cloudinary cloudinary = Singleton.getCloudinary();
		Map<String, String> options = new HashMap<>();
		options.put("public_id", name);
		cloudinary.uploader().upload(dto.getData(), options);
	}
}
