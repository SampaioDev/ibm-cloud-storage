package br.com.teste.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ibm.cloud.objectstorage.ClientConfiguration;
import com.ibm.cloud.objectstorage.SdkClientException;
import com.ibm.cloud.objectstorage.auth.AWSCredentials;
import com.ibm.cloud.objectstorage.auth.AWSStaticCredentialsProvider;
import com.ibm.cloud.objectstorage.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.ibm.cloud.objectstorage.oauth.BasicIBMOAuthCredentials;
import com.ibm.cloud.objectstorage.services.s3.AmazonS3;
import com.ibm.cloud.objectstorage.services.s3.AmazonS3ClientBuilder;
import com.ibm.cloud.objectstorage.services.s3.model.AbortMultipartUploadRequest;
import com.ibm.cloud.objectstorage.services.s3.model.Bucket;
import com.ibm.cloud.objectstorage.services.s3.model.CompleteMultipartUploadRequest;
import com.ibm.cloud.objectstorage.services.s3.model.InitiateMultipartUploadRequest;
import com.ibm.cloud.objectstorage.services.s3.model.InitiateMultipartUploadResult;
import com.ibm.cloud.objectstorage.services.s3.model.ListObjectsRequest;
import com.ibm.cloud.objectstorage.services.s3.model.ObjectListing;
import com.ibm.cloud.objectstorage.services.s3.model.PartETag;
import com.ibm.cloud.objectstorage.services.s3.model.PutObjectResult;
import com.ibm.cloud.objectstorage.services.s3.model.S3Object;
import com.ibm.cloud.objectstorage.services.s3.model.S3ObjectSummary;
import com.ibm.cloud.objectstorage.services.s3.model.UploadPartRequest;
import com.ibm.cloud.objectstorage.services.s3.model.UploadPartResult;

@Service
public class UploadServiceImpl implements IUploadService{
	
	private long partSize = 1024 * 1024 * 5;
	//ibm-cloud-teste
    private final String bucketName = "ibm-cloud-teste";
	private final String api_key = "apikey";
	private final String service_instance_id = "resource_instance_id"; 
    private final String endpoint_url = "https://s3.us-south.cloud-object-storage.appdomain.cloud";
    //String final storageClass = "us-south-standard";
    private final String location = "us-standard"; 
    
    private AmazonS3 cosClient = createClient(api_key, service_instance_id, endpoint_url, location);
    
    private AmazonS3 createClient(String api_key, String service_instance_id, String endpoint_url, String location)
    {
        AWSCredentials credentials;
        credentials = new BasicIBMOAuthCredentials(api_key, service_instance_id);

        ClientConfiguration clientConfig = new ClientConfiguration().withRequestTimeout(90000);
        clientConfig.setUseTcpKeepAlive(true);

        AmazonS3 cosClient = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withEndpointConfiguration (new EndpointConfiguration(endpoint_url, location)) .withPathStyleAccessEnabled (true)
                .withClientConfiguration(clientConfig).build();
        return cosClient;
    }

	@Override
	public void upload(String bucketName, String itemName, File file) {
        System.out.printf("Creating new item: %s\n", itemName);
    	cosClient.putObject(bucketName, itemName, file);
        System.out.printf("Item: %s created!\n", itemName);	
	}
	
	@Override
	public void upload(MultipartFile file) {
		try {
			listBuckets();
			listObjects(bucketName);
			long tamanhoArquivo = file.getSize();
			File arquivo = multipartToFile(file, file.getOriginalFilename());
			if(tamanhoArquivo > partSize) {
				multiPartUpload(bucketName, file.getOriginalFilename(), arquivo);
			}else {
				upload(bucketName, file.getOriginalFilename(), arquivo);
			}
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<String> listObjects(String bucketName) {
		System.out.println("Listing objects in bucket " + bucketName);
		List<String> objects = new ArrayList<String>();
        ObjectListing objectListing = cosClient.listObjects(new ListObjectsRequest().withBucketName(bucketName));
        for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
        	objects.add(objectSummary.getKey());
            System.out.println(" - " + objectSummary.getKey() + "  " + "(size = " + objectSummary.getSize() + ")");
        }
        System.out.println();
        return objects;
	}	

	@Override
	public void createBucket(String bucketName) {
        cosClient.createBucket(bucketName);
	}

	@Override
	public List<String> listBuckets() {
		System.out.println("Listing buckets");
        final List<Bucket> bucketList = cosClient.listBuckets();
        
        List<String> bucketNames = new ArrayList<String>();
        for (final Bucket bucket : bucketList) {
            bucketNames.add(bucket.getName());
        }
        System.out.println();
        return bucketNames;
	}

	@Override
	public void deleteItem(String bucketName, String itemName) {
		cosClient.deleteObject(bucketName, itemName);
	}

	@Override
	public void multiPartUpload(String bucketName, String itemName, File file) {
		System.out.printf("Starting multi-part upload for %s to bucket: %s\n", itemName, bucketName);
		
		InitiateMultipartUploadResult mpResult = cosClient.initiateMultipartUpload(new InitiateMultipartUploadRequest(bucketName, itemName));
		String uploadID = mpResult.getUploadId();
		
		long fileSize = file.length();
		long partCount = ((long)Math.ceil(fileSize / partSize)) + 1;
		List<PartETag> dataPacks = new ArrayList<PartETag>();
		
		try {
		    long position = 0;
		    for (int partNum = 1; position < fileSize; partNum++) {
		        partSize = Math.min(partSize, (fileSize - position));
		
		        System.out.printf("Uploading to %s (part %s of %s)\n", bucketName, partNum, partCount);  
		
		    UploadPartRequest upRequest = new UploadPartRequest()
		            .withBucketName(bucketName)
		            .withKey(itemName)
		            .withUploadId(uploadID)
		            .withPartNumber(partNum)
		            .withFileOffset(position)
		            .withFile(file)
		            .withPartSize(partSize);
		    
		    UploadPartResult upResult = cosClient.uploadPart(upRequest);
		    dataPacks.add(upResult.getPartETag());
		
		    position += partSize;
		} 
		
		//complete upload
		cosClient.completeMultipartUpload(new CompleteMultipartUploadRequest(bucketName, itemName, uploadID, dataPacks));
		System.out.printf("Upload for %s Complete!\n", itemName);
		} catch (SdkClientException sdke) {
		    System.out.printf("Multi-part upload aborted for %s\n", itemName);
		System.out.printf("Upload Error: %s\n", sdke.getMessage());
			cosClient.abortMultipartUpload(new AbortMultipartUploadRequest(bucketName, itemName, uploadID));
		}
	}
	
	private File multipartToFile(MultipartFile multipart, String fileName) throws IllegalStateException, IOException {
	    File convFile = new File(System.getProperty("java.io.tmpdir")+"/"+fileName);
	    multipart.transferTo(convFile);
	    return convFile;
	}
	
	@Override
	public void download(String fileName) throws Exception {
		S3Object s3Object = cosClient.getObject(bucketName, fileName);	    	
	    if (s3Object == null) {
	        throw new Exception("Object not found");
	    }
	    System.out.println("Iniciando download do arquivo " + fileName);
	    File file = new File("C:\\ibm-downloads\\"+fileName);
	    Files.copy(s3Object.getObjectContent(), file.toPath());
	    System.out.println("Download concluído!");
	}
  
}
