function adicionarArquivo(file){
	var form = new FormData();   

    form.append("file",file);
   		    	      	    
    $.ajax({ 
	    url: 'http://localhost:8080/uploadibm', 
	    type: 'post', 
	    data: form, 
	    mimetype: "multipart/form-data",
	    contentType: false, 
	    processData: false,
	    success: function(result) {
	    	
		},
		error: function(result) {
			alert("Upload feito com sucesso!");
		}, 
	 });
}

function baixarArquivo(nomeArquivo){
	var form = new FormData();   

    form.append("nomeArquivo",nomeArquivo);
   		    	      	    
    $.ajax({ 
	    url: 'http://localhost:8080/downloadibm', 
	    type: 'post', 
	    data: form, 
	    mimetype: "multipart/form-data",
	    contentType: false, 
	    processData: false,
	    success: function(result) {
	    	
		},
		error: function(result) {
			alert("Download concluído!");
		}, 
	 });
}

function createRepository(bucketName){
	var form = new FormData();   

    form.append("bucketName",bucketName);
   		    	      	    
    $.ajax({ 
	    url: 'http://localhost:8080/createrepositoryibm', 
	    type: 'post', 
	    data: form, 
	    mimetype: "multipart/form-data",
	    contentType: false, 
	    processData: false,
	    success: function(result) {
	    	alert(result);
		},
		error: function(result) {
			alert("Repositório criado!");
		}, 
	 });
}

function listObjects(bucketName){
	var form = new FormData();   

    form.append("bucketName",bucketName);
   		    	      	    
    $.ajax({ 
	    url: 'http://localhost:8080/listobjectssibm', 
	    type: 'post', 
	    data: form, 
	    mimetype: "multipart/form-data",
	    contentType: false, 
	    processData: false,
	    success: function(result) {
	    	alert(result);
		},
		error: function(result) {
		}, 
	 });
}

function atualizarCampo(campo){
	
	var label = document.querySelector('label[for="inputGroupFile01"]');
	label.textContent = campo;
	
}