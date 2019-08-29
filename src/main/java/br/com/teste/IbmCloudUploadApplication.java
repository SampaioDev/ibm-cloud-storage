package br.com.teste;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.ibm.cloud.objectstorage.SDKGlobalConfiguration;

@SpringBootApplication
public class IbmCloudUploadApplication {

	public static void main(String[] args) {
		SDKGlobalConfiguration.IAM_ENDPOINT = "https://iam.cloud.ibm.com/identity/token";
		SpringApplication.run(IbmCloudUploadApplication.class, args);
	}

}