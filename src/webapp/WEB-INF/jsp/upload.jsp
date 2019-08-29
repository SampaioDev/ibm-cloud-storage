<%@ taglib tagdir="/WEB-INF/tags/html" prefix="html"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html:templateNavbar title="Fazer upload de aúdio">
	<jsp:attribute name="extraJS">
		<script type="text/javascript" charset="utf-8"
			src="${pageContext.request.contextPath}/js/upload.js"></script>
	</jsp:attribute>
<jsp:body>
	<div class="modal-body">
		<form id="formArquivo" enctype="multipart/form-data">
			<div class="row">
				<div class="col-md-12">
					<span class="control-fileupload">
					  <label for="fileInput">Selecione o Arquivo :</label>
					  <input type="file" onchange="adicionarArquivo()"
						id="arquivoGlosa">
					</span>
				</div>							
			</div>
		</form>
		<div class="row">
			<div class="table-responsive tabela-modal-arquivos">
				<table id="tabela-modal-arquivos-temporario" class="table table-striped table-hover">
					<thead>
						<tr>
							<th></th>
							<th></th>
						</tr>
					</thead>
					<tbody>
					</tbody>
				</table>
			</div>
		</div>
	</div>
<jsp:body>
</html:templateNavbar>