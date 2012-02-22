<spring:htmlEscape defaultHtmlEscape="true" />
<ul id="menu">
	<li class="first"><a
		href="${pageContext.request.contextPath}/admin"><spring:message
				code="admin.title.short" /></a></li>

	<li
		<c:if test='<%= request.getRequestURI().contains("/query") %>'>class="active"</c:if>>
		<a
		href="${pageContext.request.contextPath}/module/yank/query.form"><spring:message
				code="yank.query.link" /></a>
	</li>
	
	<li
		<c:if test='<%= request.getRequestURI().contains("/process") %>'>class="active"</c:if>>
		<a
		href="${pageContext.request.contextPath}/module/yank/process.form"><spring:message
				code="yank.process.link" /></a>
	</li>

	<!-- Add further links here -->
</ul>

