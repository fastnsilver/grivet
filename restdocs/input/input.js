var com = { qmino : { miredot : {}}};
com.qmino.miredot.restApiSource = {"validLicence":true,"buildSystem":"maven 3","allowUsageTracking":true,"issuesTabHidden":false,"singlePage":false,"licenceErrorMessage":null,"miredotRevision":"8e41c1c81bb8","jsonDocHidden":false,"licenceHash":"329803669817583205","miredotVersion":"1.6.1","baseUrl":"http:\/\/www.example.com","jsonDocEnabled":true,"dateOfGeneration":"2015-08-14 17:28:18","licenceType":"PRO","hideLogoOnTop":false,"projectName":"Grivet","projectVersion":"0.0.1-SNAPSHOT","projectTitle":"Grivet-0.0.1-SNAPSHOT"};
com.qmino.miredot.restApiSource.tos = {
	com_fns_grivet_query_NamedQuery_in: { "type": "complex", "name": "com_fns_grivet_query_NamedQuery_in", "content": [] },
	com_fns_grivet_query_NamedQuery_out: { "type": "complex", "name": "com_fns_grivet_query_NamedQuery_out", "content": [] },
	org_springframework_http_ResponseEntity_java_lang_Object__in: { "type": "complex", "name": "org_springframework_http_ResponseEntity_java_lang_Object__in", "content": [] },
	org_springframework_http_ResponseEntity_java_lang_Object__out: { "type": "complex", "name": "org_springframework_http_ResponseEntity_java_lang_Object__out", "content": [] }
};

com.qmino.miredot.restApiSource.enums = {
	org_springframework_http_HttpStatus: { "type": "enum", "name": "org_springframework_http_HttpStatus", "values": [{"name": "CONTINUE", "comment": null}, {"name": "SWITCHING_PROTOCOLS", "comment": null}, {"name": "PROCESSING", "comment": null}, {"name": "CHECKPOINT", "comment": null}, {"name": "OK", "comment": null}, {"name": "CREATED", "comment": null}, {"name": "ACCEPTED", "comment": null}, {"name": "NON_AUTHORITATIVE_INFORMATION", "comment": null}, {"name": "NO_CONTENT", "comment": null}, {"name": "RESET_CONTENT", "comment": null}, {"name": "PARTIAL_CONTENT", "comment": null}, {"name": "MULTI_STATUS", "comment": null}, {"name": "ALREADY_REPORTED", "comment": null}, {"name": "IM_USED", "comment": null}, {"name": "MULTIPLE_CHOICES", "comment": null}, {"name": "MOVED_PERMANENTLY", "comment": null}, {"name": "FOUND", "comment": null}, {"name": "MOVED_TEMPORARILY", "comment": null}, {"name": "SEE_OTHER", "comment": null}, {"name": "NOT_MODIFIED", "comment": null}, {"name": "USE_PROXY", "comment": null}, {"name": "TEMPORARY_REDIRECT", "comment": null}, {"name": "PERMANENT_REDIRECT", "comment": null}, {"name": "BAD_REQUEST", "comment": null}, {"name": "UNAUTHORIZED", "comment": null}, {"name": "PAYMENT_REQUIRED", "comment": null}, {"name": "FORBIDDEN", "comment": null}, {"name": "NOT_FOUND", "comment": null}, {"name": "METHOD_NOT_ALLOWED", "comment": null}, {"name": "NOT_ACCEPTABLE", "comment": null}, {"name": "PROXY_AUTHENTICATION_REQUIRED", "comment": null}, {"name": "REQUEST_TIMEOUT", "comment": null}, {"name": "CONFLICT", "comment": null}, {"name": "GONE", "comment": null}, {"name": "LENGTH_REQUIRED", "comment": null}, {"name": "PRECONDITION_FAILED", "comment": null}, {"name": "PAYLOAD_TOO_LARGE", "comment": null}, {"name": "REQUEST_ENTITY_TOO_LARGE", "comment": null}, {"name": "URI_TOO_LONG", "comment": null}, {"name": "REQUEST_URI_TOO_LONG", "comment": null}, {"name": "UNSUPPORTED_MEDIA_TYPE", "comment": null}, {"name": "REQUESTED_RANGE_NOT_SATISFIABLE", "comment": null}, {"name": "EXPECTATION_FAILED", "comment": null}, {"name": "I_AM_A_TEAPOT", "comment": null}, {"name": "INSUFFICIENT_SPACE_ON_RESOURCE", "comment": null}, {"name": "METHOD_FAILURE", "comment": null}, {"name": "DESTINATION_LOCKED", "comment": null}, {"name": "UNPROCESSABLE_ENTITY", "comment": null}, {"name": "LOCKED", "comment": null}, {"name": "FAILED_DEPENDENCY", "comment": null}, {"name": "UPGRADE_REQUIRED", "comment": null}, {"name": "PRECONDITION_REQUIRED", "comment": null}, {"name": "TOO_MANY_REQUESTS", "comment": null}, {"name": "REQUEST_HEADER_FIELDS_TOO_LARGE", "comment": null}, {"name": "INTERNAL_SERVER_ERROR", "comment": null}, {"name": "NOT_IMPLEMENTED", "comment": null}, {"name": "BAD_GATEWAY", "comment": null}, {"name": "SERVICE_UNAVAILABLE", "comment": null}, {"name": "GATEWAY_TIMEOUT", "comment": null}, {"name": "HTTP_VERSION_NOT_SUPPORTED", "comment": null}, {"name": "VARIANT_ALSO_NEGOTIATES", "comment": null}, {"name": "INSUFFICIENT_STORAGE", "comment": null}, {"name": "LOOP_DETECTED", "comment": null}, {"name": "BANDWIDTH_LIMIT_EXCEEDED", "comment": null}, {"name": "NOT_EXTENDED", "comment": null}, {"name": "NETWORK_AUTHENTICATION_REQUIRED", "comment": null}]},
	com_fns_grivet_query_QueryType: { "type": "enum", "name": "com_fns_grivet_query_QueryType", "values": [{"name": "SELECT", "comment": null}, {"name": "SPROC", "comment": null}]}
};
com.qmino.miredot.restApiSource.tos["com_fns_grivet_query_NamedQuery_in"].content = [ 
	{
		"name": "name",
		"comment": null,
		"fullComment": null,
		"typeValue": { "type": "simple", "typeValue": "string" },
		"deprecated": false,
		"required": false
	},
	{
		"name": "type",
		"comment": null,
		"fullComment": null,
		"typeValue": com.qmino.miredot.restApiSource.enums["com_fns_grivet_query_QueryType"],
		"deprecated": false,
		"required": false
	},
	{
		"name": "query",
		"comment": null,
		"fullComment": null,
		"typeValue": { "type": "simple", "typeValue": "string" },
		"deprecated": false,
		"required": false
	},
	{
		"name": "params",
		"comment": null,
		"fullComment": null,
		"typeValue": { "type": "map", "typeKey": { "type": "simple", "typeValue": "string" }, "typeValue": { "type": "simple", "typeValue": "string" } },
		"deprecated": false,
		"required": false
	}
];
com.qmino.miredot.restApiSource.tos["com_fns_grivet_query_NamedQuery_in"].ordered = true;
com.qmino.miredot.restApiSource.tos["com_fns_grivet_query_NamedQuery_in"].comment = null;
com.qmino.miredot.restApiSource.tos["com_fns_grivet_query_NamedQuery_out"].content = [ 
	{
		"name": "name",
		"comment": null,
		"fullComment": null,
		"typeValue": { "type": "simple", "typeValue": "string" },
		"deprecated": false,
		"required": false
	},
	{
		"name": "type",
		"comment": null,
		"fullComment": null,
		"typeValue": com.qmino.miredot.restApiSource.enums["com_fns_grivet_query_QueryType"],
		"deprecated": false,
		"required": false
	},
	{
		"name": "query",
		"comment": null,
		"fullComment": null,
		"typeValue": { "type": "simple", "typeValue": "string" },
		"deprecated": false,
		"required": false
	},
	{
		"name": "params",
		"comment": null,
		"fullComment": null,
		"typeValue": { "type": "map", "typeKey": { "type": "simple", "typeValue": "string" }, "typeValue": { "type": "simple", "typeValue": "string" } },
		"deprecated": false,
		"required": false
	},
	{
		"name": "createdTime",
		"comment": null,
		"fullComment": null,
		"typeValue": { "type": "simple", "typeValue": "java.time.LocalDateTime" },
		"deprecated": false,
		"required": false
	}
];
com.qmino.miredot.restApiSource.tos["com_fns_grivet_query_NamedQuery_out"].ordered = true;
com.qmino.miredot.restApiSource.tos["com_fns_grivet_query_NamedQuery_out"].comment = null;
com.qmino.miredot.restApiSource.tos["org_springframework_http_ResponseEntity_java_lang_Object__in"].content = [ 

];
com.qmino.miredot.restApiSource.tos["org_springframework_http_ResponseEntity_java_lang_Object__in"].ordered = false;
com.qmino.miredot.restApiSource.tos["org_springframework_http_ResponseEntity_java_lang_Object__in"].comment = null;
com.qmino.miredot.restApiSource.tos["org_springframework_http_ResponseEntity_java_lang_Object__out"].content = [ 
	{
		"name": "body",
		"comment": null,
		"fullComment": null,
		"typeValue": { "type": "simple", "typeValue": "object" },
		"deprecated": false,
		"required": false
	},
	{
		"name": "headers",
		"comment": null,
		"fullComment": null,
		"typeValue": { "type": "simple", "typeValue": "object" },
		"deprecated": false,
		"required": false
	},
	{
		"name": "statusCode",
		"comment": null,
		"fullComment": null,
		"typeValue": com.qmino.miredot.restApiSource.enums["org_springframework_http_HttpStatus"],
		"deprecated": false,
		"required": false
	}
];
com.qmino.miredot.restApiSource.tos["org_springframework_http_ResponseEntity_java_lang_Object__out"].ordered = false;
com.qmino.miredot.restApiSource.tos["org_springframework_http_ResponseEntity_java_lang_Object__out"].comment = null;
com.qmino.miredot.restApiSource.interfaces = [
	{
		"beschrijving": "",
		"url": "/register/",
		"http": "POST",
		"title": null,
		"tags": [],
		"authors": ["Chris Phillipson"],
		"compressed": false,
		"deprecated": false,
		"consumes": ["application/json"],
		"produces": ["application/json"],
		"roles": [],
		"rolesAllowed": null,
		"permitAll": false,
		"output": {"typeValue": com.qmino.miredot.restApiSource.tos["org_springframework_http_ResponseEntity_java_lang_Object__out"], "comment": null},
		"statusCodes": [],
		"hash": "-1670671189",
		"responseHttpHeaders": 
			[
			]
,
		"responseCustomHeaders": 
			[
			]
,
		"inputs": {
                "PATH": [],
                "QUERY": [],
                "BODY": [{"typeValue": { "type": "simple", "typeValue": "javax.servlet.http.HttpServletRequest" }, "comment": null, "jaxrs": "BODY"}],
                "HEADER": [],
                "COOKIE": [],
                "FORM": [],
                "MATRIX": []
            }
	},
	{
		"beschrijving": "",
		"url": "/store/{type}",
		"http": "POST",
		"title": null,
		"tags": [],
		"authors": ["Chris Phillipson"],
		"compressed": false,
		"deprecated": false,
		"consumes": ["application/json"],
		"produces": ["application/json"],
		"roles": [],
		"rolesAllowed": null,
		"permitAll": false,
		"output": {"typeValue": com.qmino.miredot.restApiSource.tos["org_springframework_http_ResponseEntity_java_lang_Object__out"], "comment": null},
		"statusCodes": [],
		"hash": "-457203635",
		"responseHttpHeaders": 
			[
			]
,
		"responseCustomHeaders": 
			[
			]
,
		"inputs": {
                "PATH": [{"name": "type", "typeValue": { "type": "simple", "typeValue": "string" }, "comment": null, "jaxrs": "PATH"}],
                "QUERY": [],
                "BODY": [{"typeValue": { "type": "simple", "typeValue": "javax.servlet.http.HttpServletRequest" }, "comment": null, "jaxrs": "BODY"}],
                "HEADER": [],
                "COOKIE": [],
                "FORM": [],
                "MATRIX": []
            }
	},
	{
		"beschrijving": "",
		"url": "/register/{type}",
		"http": "DELETE",
		"title": null,
		"tags": [],
		"authors": ["Chris Phillipson"],
		"compressed": false,
		"deprecated": false,
		"consumes": [],
		"produces": ["application/json"],
		"roles": [],
		"rolesAllowed": null,
		"permitAll": false,
		"output": {"typeValue": com.qmino.miredot.restApiSource.tos["org_springframework_http_ResponseEntity_java_lang_Object__out"], "comment": null},
		"statusCodes": [],
		"hash": "-1991031162",
		"responseHttpHeaders": 
			[
			]
,
		"responseCustomHeaders": 
			[
			]
,
		"inputs": {
                "PATH": [{"name": "type", "typeValue": { "type": "simple", "typeValue": "string" }, "comment": null, "jaxrs": "PATH"}],
                "QUERY": [],
                "BODY": [],
                "HEADER": [],
                "COOKIE": [],
                "FORM": [],
                "MATRIX": []
            }
	},
	{
		"beschrijving": "",
		"url": "/query/",
		"http": "POST",
		"title": null,
		"tags": [],
		"authors": ["Chris Phillipson"],
		"compressed": false,
		"deprecated": false,
		"consumes": ["application/json"],
		"produces": ["application/json"],
		"roles": [],
		"rolesAllowed": null,
		"permitAll": false,
		"output": {"typeValue": com.qmino.miredot.restApiSource.tos["org_springframework_http_ResponseEntity_java_lang_Object__out"], "comment": null},
		"statusCodes": [],
		"hash": "2052062378",
		"responseHttpHeaders": 
			[
			]
,
		"responseCustomHeaders": 
			[
			]
,
		"inputs": {
                "PATH": [],
                "QUERY": [],
                "BODY": [{"typeValue": com.qmino.miredot.restApiSource.tos["com_fns_grivet_query_NamedQuery_in"], "comment": null, "jaxrs": "BODY"}],
                "HEADER": [],
                "COOKIE": [],
                "FORM": [],
                "MATRIX": []
            }
	},
	{
		"beschrijving": "",
		"url": "/register/{type}",
		"http": "PUT",
		"title": null,
		"tags": [],
		"authors": ["Chris Phillipson"],
		"compressed": false,
		"deprecated": false,
		"consumes": [],
		"produces": ["application/json"],
		"roles": [],
		"rolesAllowed": null,
		"permitAll": false,
		"output": {"typeValue": com.qmino.miredot.restApiSource.tos["org_springframework_http_ResponseEntity_java_lang_Object__out"], "comment": null},
		"statusCodes": [],
		"hash": "526124417",
		"responseHttpHeaders": 
			[
			]
,
		"responseCustomHeaders": 
			[
			]
,
		"inputs": {
                "PATH": [{"name": "type", "typeValue": { "type": "simple", "typeValue": "string" }, "comment": null, "jaxrs": "PATH"}],
                "QUERY": [],
                "BODY": [{"typeValue": { "type": "simple", "typeValue": "javax.servlet.http.HttpServletRequest" }, "comment": null, "jaxrs": "BODY"}],
                "HEADER": [],
                "COOKIE": [],
                "FORM": [],
                "MATRIX": []
            }
	}];
com.qmino.miredot.projectWarnings = [
	{
		"category": "JAVADOC_MISSING_SUMMARY",
		"description": "Missing summary tag",
		"failedBuild": false,
		"interface": "-1670671189",
		"implementationClass": "com.fns.grivet.controller.ClassRegistryController",
		"implementationMethod": "register",
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_INTERFACEDOCUMENTATION",
		"description": "Missing interface documentation",
		"failedBuild": false,
		"interface": "-1670671189",
		"implementationClass": "com.fns.grivet.controller.ClassRegistryController",
		"implementationMethod": "register",
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_PARAMETER_DOCUMENTATION",
		"description": "Missing parameter documentation",
		"failedBuild": false,
		"interface": "-1670671189",
		"implementationClass": "com.fns.grivet.controller.ClassRegistryController",
		"implementationMethod": "register",
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_PARAMETER_DOCUMENTATION",
		"description": "Missing return type documentation",
		"failedBuild": false,
		"interface": "-1670671189",
		"implementationClass": "com.fns.grivet.controller.ClassRegistryController",
		"implementationMethod": "register",
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_EXCEPTION_DOCUMENTATION",
		"description": "Exception thrown by method has no comment",
		"failedBuild": false,
		"interface": "-1670671189",
		"implementationClass": "com.fns.grivet.controller.ClassRegistryController",
		"implementationMethod": "register",
		"entity": "java.io.IOException"
	},
	{
		"category": "REST_UNMAPPED_EXCEPTION",
		"description": "Exception is thrown by interface specification, but is not mapped in the MireDot configuration. As such, the return errorcode can not be documented properly.",
		"failedBuild": false,
		"interface": "-1670671189",
		"implementationClass": "com.fns.grivet.controller.ClassRegistryController",
		"implementationMethod": "register",
		"entity": "java.io.IOException"
	},
	{
		"category": "JAVADOC_MISSING_SUMMARY",
		"description": "Missing summary tag",
		"failedBuild": false,
		"interface": "-457203635",
		"implementationClass": "com.fns.grivet.controller.EntityController",
		"implementationMethod": "create",
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_INTERFACEDOCUMENTATION",
		"description": "Missing interface documentation",
		"failedBuild": false,
		"interface": "-457203635",
		"implementationClass": "com.fns.grivet.controller.EntityController",
		"implementationMethod": "create",
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_PARAMETER_DOCUMENTATION",
		"description": "Missing parameter documentation",
		"failedBuild": false,
		"interface": "-457203635",
		"implementationClass": "com.fns.grivet.controller.EntityController",
		"implementationMethod": "create",
		"entity": "type"
	},
	{
		"category": "JAVADOC_MISSING_PARAMETER_DOCUMENTATION",
		"description": "Missing parameter documentation",
		"failedBuild": false,
		"interface": "-457203635",
		"implementationClass": "com.fns.grivet.controller.EntityController",
		"implementationMethod": "create",
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_PARAMETER_DOCUMENTATION",
		"description": "Missing return type documentation",
		"failedBuild": false,
		"interface": "-457203635",
		"implementationClass": "com.fns.grivet.controller.EntityController",
		"implementationMethod": "create",
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_EXCEPTION_DOCUMENTATION",
		"description": "Exception thrown by method has no comment",
		"failedBuild": false,
		"interface": "-457203635",
		"implementationClass": "com.fns.grivet.controller.EntityController",
		"implementationMethod": "create",
		"entity": "java.io.IOException"
	},
	{
		"category": "REST_UNMAPPED_EXCEPTION",
		"description": "Exception is thrown by interface specification, but is not mapped in the MireDot configuration. As such, the return errorcode can not be documented properly.",
		"failedBuild": false,
		"interface": "-457203635",
		"implementationClass": "com.fns.grivet.controller.EntityController",
		"implementationMethod": "create",
		"entity": "java.io.IOException"
	},
	{
		"category": "JAVADOC_MISSING_SUMMARY",
		"description": "Missing summary tag",
		"failedBuild": false,
		"interface": "-1991031162",
		"implementationClass": "com.fns.grivet.controller.ClassRegistryController",
		"implementationMethod": "delete",
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_INTERFACEDOCUMENTATION",
		"description": "Missing interface documentation",
		"failedBuild": false,
		"interface": "-1991031162",
		"implementationClass": "com.fns.grivet.controller.ClassRegistryController",
		"implementationMethod": "delete",
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_PARAMETER_DOCUMENTATION",
		"description": "Missing parameter documentation",
		"failedBuild": false,
		"interface": "-1991031162",
		"implementationClass": "com.fns.grivet.controller.ClassRegistryController",
		"implementationMethod": "delete",
		"entity": "type"
	},
	{
		"category": "JAVADOC_MISSING_PARAMETER_DOCUMENTATION",
		"description": "Missing return type documentation",
		"failedBuild": false,
		"interface": "-1991031162",
		"implementationClass": "com.fns.grivet.controller.ClassRegistryController",
		"implementationMethod": "delete",
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_SUMMARY",
		"description": "Missing summary tag",
		"failedBuild": false,
		"interface": "2052062378",
		"implementationClass": "com.fns.grivet.controller.NamedQueryController",
		"implementationMethod": "create",
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_INTERFACEDOCUMENTATION",
		"description": "Missing interface documentation",
		"failedBuild": false,
		"interface": "2052062378",
		"implementationClass": "com.fns.grivet.controller.NamedQueryController",
		"implementationMethod": "create",
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_PARAMETER_DOCUMENTATION",
		"description": "Missing parameter documentation",
		"failedBuild": false,
		"interface": "2052062378",
		"implementationClass": "com.fns.grivet.controller.NamedQueryController",
		"implementationMethod": "create",
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_PARAMETER_DOCUMENTATION",
		"description": "Missing return type documentation",
		"failedBuild": false,
		"interface": "2052062378",
		"implementationClass": "com.fns.grivet.controller.NamedQueryController",
		"implementationMethod": "create",
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_SUMMARY",
		"description": "Missing summary tag",
		"failedBuild": false,
		"interface": "526124417",
		"implementationClass": "com.fns.grivet.controller.ClassRegistryController",
		"implementationMethod": "unlinkSchema",
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_INTERFACEDOCUMENTATION",
		"description": "Missing interface documentation",
		"failedBuild": false,
		"interface": "526124417",
		"implementationClass": "com.fns.grivet.controller.ClassRegistryController",
		"implementationMethod": "unlinkSchema",
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_PARAMETER_DOCUMENTATION",
		"description": "Missing parameter documentation",
		"failedBuild": false,
		"interface": "526124417",
		"implementationClass": "com.fns.grivet.controller.ClassRegistryController",
		"implementationMethod": "unlinkSchema",
		"entity": "type"
	},
	{
		"category": "JAVADOC_MISSING_PARAMETER_DOCUMENTATION",
		"description": "Missing parameter documentation",
		"failedBuild": false,
		"interface": "526124417",
		"implementationClass": "com.fns.grivet.controller.ClassRegistryController",
		"implementationMethod": "unlinkSchema",
		"entity": null
	},
	{
		"category": "JAVADOC_MISSING_PARAMETER_DOCUMENTATION",
		"description": "Missing return type documentation",
		"failedBuild": false,
		"interface": "526124417",
		"implementationClass": "com.fns.grivet.controller.ClassRegistryController",
		"implementationMethod": "unlinkSchema",
		"entity": null
	},
	{
		"category": "JAXRS_MISSING_CONSUMES",
		"description": "Interface specifies a JAXRS-BODY parameter, but does not specify a Consumes value.",
		"failedBuild": false,
		"interface": "526124417",
		"implementationClass": "com.fns.grivet.controller.ClassRegistryController",
		"implementationMethod": "unlinkSchema",
		"entity": null
	}];
com.qmino.miredot.processErrors  = [
];
