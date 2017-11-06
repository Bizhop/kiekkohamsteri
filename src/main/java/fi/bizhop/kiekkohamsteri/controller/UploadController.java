package fi.bizhop.kiekkohamsteri.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import fi.bizhop.kiekkohamsteri.dto.UploadDto;
import fi.bizhop.kiekkohamsteri.service.UploadService;

@RestController
public class UploadController extends BaseController {
	@Autowired
	UploadService uploadService;

	@RequestMapping(value = "/upload", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public void upload(@RequestBody UploadDto dto, HttpServletResponse response) throws IOException {
		uploadService.upload(dto);
	}
}
