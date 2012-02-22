<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp"%>

<openmrs:require privilege="Query Yanks" otherwise="/login.htm" redirect="/module/yank/query.form" />

<openmrs:htmlInclude file="/moduleResources/yank/js/jquery.formparams.min.js" />

<%@ include file="template/localHeader.jsp"%>

<script>
	$j(document).ready(function(){
		
		// set css on outer rows of table
		$j("table#queryParams tr:first td").each(function(){
			$j(this).css("border-top", "none");
			$j(this).css("padding", "0 0 1em");
		});
		$j("table#queryParams tr:last td").each(function(){
			$j(this).css("padding", "1em 0 0");
		});

		// set the action on yank submit
		$j("#yankButton").click(function(){
			$j("#queryForm").submit();
//			var formData = $j("#queryForm").formParams(false);
//			$j.ajax({
//				url: "queryForYanks.json",
//				data: formData,
//				dataType: "text",
//				success: function(response) {
//					alert(response);
//				}
//			});
//			return false;
		});
	});
</script>	
	
<style>
	form#queryForm { margin: 2em 0; }
	table#queryParams { width: 100%; }
	table#queryParams td { border-top: 1px dashed #5D8AA8; padding: 1em 0; vertical-align: middle; }
	table#queryParams label { font-weight: bold; }
	input.full, textarea.full { width: 100%; }
	div.centered { border: 1px solid #5D8AA8; padding: 1em; position: relative; left: 10%; width: 80%; }
	div.centered.header, div.centered.footer { background-color: #5D8AA8; color: #fff; font-weight: bold; }
</style>

<h2>Query for Yanks</h2>

<form id="queryForm" method="post" enctype="multipart/form-data">
	<div class="centered header">
		Fill in the form below with a list of UUIDs separated by commas, or upload
		a file of UUIDs separated by commas.  Choosing to upload a file will override
		anything entered in the text field.
	</div>
	<div class="centered">
		<table id="queryParams">
			<tr>
				<td>
					<label for="server">Server:</label>
				</td>
				<td>
					<input class="full" name="server" type="text"/>
				</td>
			</tr>
			<tr>
				<td>
					<label for="password">Username:</label>
				</td>
				<td>
					<input name="username" type="text"/>
				</td>
			</tr>
			<tr>
				<td>
					<label for="password">Password:</label>
				</td>
				<td>
					<input name="password" type="password"/>
				</td>
			</tr>
			<tr>
				<td>
					<label for="datatype">Data type: </label>
				</td>
				<td>
					<select name="datatype">
						<option value="patient">Patient</option>
					</select>
				</td>
			</tr>
			<tr>
				<td>
					<label for="uuids">Uuids:</label>
				</td>
				<td>
					<textarea class="full" name="uuids" cols="100" rows="10"></textarea>
					<blockquote>
						<i>or</i>
					</blockquote>
					<input type="file" name="file"/>
				</td>
			</tr>
		</table>
	</div>
	<div class="centered footer">
		<button id="yankButton">Yank</button>
	</div>
</form>
	
<%@ include file="/WEB-INF/template/footer.jsp"%>