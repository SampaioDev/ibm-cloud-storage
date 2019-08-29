function removerAcentos( newStringComAcento ) {
    var string = newStringComAcento;
  	var mapaAcentosHex 	= {
  		a : /[\xE0-\xE6]/g,
  		e : /[\xE8-\xEB]/g,
  		i : /[\xEC-\xEF]/g,
  		o : /[\xF2-\xF6]/g,
  		u : /[\xF9-\xFC]/g,
  		c : /\xE7/g,
  		n : /\xF1/g
  	};

  	for ( var letra in mapaAcentosHex ) {
  		var expressaoRegular = mapaAcentosHex[letra];
  		string = string.replace( expressaoRegular, letra );
  	}

  	return string;
  }

function getBase64(file) {
	return new Promise((resolve, reject) => {
	    const reader = new FileReader();
	    reader.readAsDataURL(file);
	    reader.onload = () => resolve(reader.result);
	    reader.onerror = error => reject(error);
	  });
}

function adicionarArquivo(){
	var form = new FormData(); 
    var arquivo = $('#arquivoAudio');  
    console.log(arquivo);
     	
    getBase64(arquivo).then(
    		arquivoBase64 => {
    			  if (arquivo == null) {
    			    	showNotification("Selecione um Arquivo para Enviar", "warning");
    			    	return false;
    			    } else {
    			        var tamanho = parseInt(arquivo.size);
    			        var nomeArquivo = arquivo.name;
    			        nomeArquivo = removerAcentos(nomeArquivo);  
    			    }
			    
		    	   var itemArquivo = {};
		    	   itemArquivo.nomeArquivo = nomeArquivo;
		    	   
		    	   form.append('file', arquivoBase64);
				   form.append('nomeArquivo',nomeArquivo);
				   
				   showLoading();
		    	      	    
	    	    	$.ajax({ 
	    	            url: 'localhost:8080/uploadibm', 
	    	            type: 'post', 
	    	            data: form, 
	    	            mimetype: "multipart/form-data",
	    	            contentType: false, 
	    	            processData: false,
	    	            success: function(result) {
	    					showNotification("Arquivo anexado com sucesso!", "success");
	    					itemArquivo = {};
	    					hideLoading();
	    				},
	    				error: function(result) {
	    					showNotification(result.responseJSON, "danger");
	    					hideLoading();
	    				}, 
	    	        }); 
    		  }
    		);
}