<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp"%>

<openmrs:require privilege="Process Yanks" otherwise="/login.htm" redirect="/module/yank/process.form" />

<openmrs:htmlInclude file="/moduleResources/yank/js/jquery.dataTables.min.js" />
<openmrs:htmlInclude file="/moduleResources/yank/css/jquery.dataTables.css" />

<openmrs:htmlInclude file="/scripts/jquery/dataTables/js/jquery.dataTables.filteringDelay.js" />

<%@ include file="template/localHeader.jsp"%>

<script>
	$j(document).ready(function(){
		$j("#selectAll").click(function(){
			$j("input[name=yankIds]").attr("checked", this.checked);
		});
	
		yankTable = $j("#yankTable").dataTable({
			bProcessing: true,
			bServerSide: true,
			sAjaxSource: "getYanks.json",
			aoColumns: [
					{ bSortable: false, sName: "select", bSortable: false, 
						fnRender: function(oObj){
							return '<input type="checkbox" name="yankIds" value="' + oObj.aData[0] + '"/>'
							+ ' <a href="#" class="showYank" yankId="' + oObj.aData[0] + '">[+]</a>';
					}},
					{ bSortable: false, sName: "datatype"},
					{ bSortable: false, sName: "summary", "sWidth": "50em"},
					{ bSortable: false, sName: "dateCreated"}
			]
		});
		yankTable.fnSetFilteringDelay(1000);

		$j(".showYank").live("click", function(){
			var id = $j(this).attr("yankId");
			$j.ajax({
				url: "getYankData.json",
				data: {yankId: id},
				dataType: "text",
				success: function(data) {
					alert(data);
				}
			});
			return false;
		});

	});
</script>

<style>
	a.showYank { text-decoration: none !important; }
</style>

<h2>Process Yanks</h2>

<p class="description">
	Select yanks from the table below for processing.  If selecting all, please scroll through
	entire table before selecting (until I fix it).
</p>

<br/>

<div style="border: 1px solid #5D8AA8; padding: 1em; position: relative; left: 10%; width: 80%;">
	<form method="post">
		<table id="yankTable">
			<thead>
				<tr>
					<th><input type="checkbox" id="selectAll"/> all</th>
					<th>Datatype</th>
					<th>Summary</th>
					<th>Date Yanked</th>
				</tr>
			</thead>
			<tbody>
			<c:forEach items="${yanks}" var="yank">
				<tr>
					<td><input type="checkbox" name="yankIds" value="${yank.yankId}"/></td>
					<td>${yank.datatype}</td>
					<td>${yank.summary}</td>
					<td>${yank.dateCreated}</td>
				</tr>
			</c:forEach>
			</tbody>
		</table>
		<br style="clear:both;"/>
		<br/>
		<input type="submit" name="submitSelected" value="Process Selected Yank(s)"/>
		<input type="submit" name="submitAll" value="Process All Pending Yanks"/>
	</form>
</div>

<%@ include file="/WEB-INF/template/footer.jsp"%>