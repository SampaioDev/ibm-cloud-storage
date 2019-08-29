package br.com.teste.controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import br.com.teste.service.IUploadService;


@Controller
public class UploadController {
	
	@Autowired
	private IUploadService service;

	@RequestMapping("/")
	public String index() {
	    return "upload.html";
	}
		
	@RequestMapping("/downloadibm")
	public void downloadFile(@RequestParam("nomeArquivo") String nomeArquivo) throws Exception {
		service.download(nomeArquivo);
	}
	
	@RequestMapping("/createrepositoryibm")
	public void createRepository(@RequestParam("bucketName") String bucketName){
		service.createBucket(bucketName);
	}
	
	@RequestMapping(value="/uploadibm", method = RequestMethod.POST, consumes = "multipart/form-data")
    public void upload(@RequestParam("file") MultipartFile file) {
		service.upload(file);
    }
	
	@RequestMapping(value="/listbucketsibm", method = RequestMethod.GET)
    public ResponseEntity<List<String>> listBuckets() {
		List<String> buckets = service.listBuckets();
		return ResponseEntity.ok().body(buckets); 
		
    }
	
	@RequestMapping(value="/listobjectssibm", method = RequestMethod.POST)
    public ResponseEntity<List<String>> listObjects(@RequestParam("bucketName") String bucketName) {
		List<String> objects = service.listObjects(bucketName);
		return ResponseEntity.ok().body(objects); 
    }
}