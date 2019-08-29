package br.com.teste.service;

import java.io.File;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;



public interface IUploadService {
	
	List<String> listObjects(String bucketName);
	
	void createBucket(String bucketName);
	
	List<String> listBuckets( );
	
	void deleteItem(String bucketName, String itemName);
	
	void upload(String bucketName, String itemName, File file);

	void upload(MultipartFile file);

	void multiPartUpload(String bucketName, String itemName, File file);

	void download(String fileName) throws Exception;
	
}