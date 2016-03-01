<%@ page import="daweb3.User" %>

<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<title>Premis</title>
	</head>
	<body>
		<a href="#list-object" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><a class="back" href="${createLink(uri: '/object/list')}">Back</a></li>
			</ul>
		</div>
		<div id="list-object" class="content scaffold-list" role="main">
			<h1>Premis zu ${params.objectIdentifier }</h1>
			Anzahl der Events ${size}
			<!--  ${params.pkg}
			<br/> <br/>
			${events}
			<br/> <br/>
			${events.each{ 
				it.eventIdentifier.eventIdentifierType 
				} }
			<br/><br/>
			${events[0].eventIdentifier.eventIdentifierType }-->
			
			<table>
				<thead>
					<tr>
						<th>eventType</th>

						<th>eventDateTime</th>
						<th>eventDetail</th>
						<th>linkingAgentIdentifierType</th>
						<th>linkingAgentIdentifierValue</th>
						<th>linkingObjectIdentifierType</th>
						<th>linkingObjectIdentifierValue</th>
						<th>linkingObjectRole</th>
						<th>linkingObjectIdentifierType</th>
						<th>linkingObjectIdentifierValue</th>
						<th>linkingObjectRole</th>
					</tr>
				</thead>
				
				<!-- 
				
					<th>eventIdentifierType</th>
					<th>eventIdentifierValue</th>
				
					print "<td>"+it.eventIdentifier.eventIdentifierType+"</td>"
					print "<td>"+it.eventIdentifier.eventIdentifierValue+"</td>"
				
					
				 -->
				
				<tbody>
				<% events.each{ 
					print "<tr>"
					print "<td>"+it.eventType+"</td>"

					print "<td>"+it.eventDateTime+"</td>"
					print "<td>"+it.eventDetail+"</td>"
					print "<td>"+it.linkingAgentIdentifier.linkingAgentIdentifierType+"</td>"
					print "<td>"+it.linkingAgentIdentifier.linkingAgentIdentifierValue+"</td>"
					print "<td>"+it.linkingObjectIdentifier[0].linkingObjectIdentifierType+"</td>"
					print "<td>"+it.linkingObjectIdentifier[0].linkingObjectIdentifierValue+"</td>"
					print "<td>"+it.linkingObjectIdentifier[0].linkingObjectRole+"</td>"
					print "<td>"+it.linkingObjectIdentifier[1]?.linkingObjectIdentifierType+"</td>"
					print "<td>"+it.linkingObjectIdentifier[1]?.linkingObjectIdentifierValue+"</td>"
					print "<td>"+it.linkingObjectIdentifier[1]?.linkingObjectRole+"</td>"
					print "</tr>"
				} %>
				</tbody>
			</table>
			
			${params}
			
			<table>
				<thead>
					<tr>
						<th>eventType</th>
						
						<th>eventDateTime</th>
						<th>eventDetail</th>
						<th>linkingAgentIdentifierType</th>
						<th>linkingAgentIdentifierValue</th>
						
						<th>linkingObjectIdentifierValue / Source</th>

						<th>linkingObjectIdentifierValue / Target</th>

					</tr>
				</thead>
				<tbody>
				<!-- 
					
					<th>eventIdentifierType</th>
					<th>eventIdentifierValue</th>
					
					<th>linkingObjectIdentifierType</th>
					
					<th>linkingObjectRole</th>
					<th>linkingObjectIdentifierType</th>
				
					<th>linkingObjectRole</th>
				
				
					print "<td>"+it.eventIdentifier.eventIdentifierType+"</td>"
					print "<td>"+it.eventIdentifier.eventIdentifierValue+"</td>"
					
					print "<td>"+it.linkingObjectIdentifier[0]?.linkingObjectIdentifierValue+"</td>"
					print "<td>"+it.linkingObjectIdentifier[0]?.linkingObjectRole+"</td>"
					
					print "<td>"+it.linkingObjectIdentifier[1]?.linkingObjectIdentifierValue+"</td>"
					print "<td>"+it.linkingObjectIdentifier[1]?.linkingObjectRole+"</td>"
				 -->
				<% eventList.each{ 
					print "<tr>"
					print "<td>"+it.type+"</td>"
				
					print "<td>"+it.date+"</td>"
					print "<td>"+it.detail+"</td>"
					print "<td>"+it.agentType+"</td>"
					print "<td>"+it.agentName+"</td>"
					print "<td>"+it.sourceFile?.rep_name+"</td>"

					print "<td>"+it.targetFile?.rep_name+"</td>"

					print "</tr>"
				} %>
				</tbody>
			</table>
			
		</div>
		<br/> Test Premis <br/>

	</body>
</html>