-- Add OAuth2 tables
-- borrowed from https://github.com/spring-projects/spring-security-oauth/blob/master/spring-security-oauth2/src/test/resources/schema.sql

CREATE TABLE oauth_client_details (
  client_id VARCHAR(256) ${varcharQualifier},
  resource_ids VARCHAR(256) ${varcharQualifier},
  client_secret VARCHAR(256) ${varcharQualifier},
  scope VARCHAR(256) ${varcharQualifier},
  authorized_grant_types VARCHAR(256) ${varcharQualifier},
  web_server_redirect_uri VARCHAR(256) ${varcharQualifier},
  authorities VARCHAR(256) ${varcharQualifier},
  access_token_validity INT,
  refresh_token_validity INT,
  additional_information VARCHAR(4096) ${varcharQualifier},
  autoapprove VARCHAR(256) ${varcharQualifier},
  PRIMARY KEY (client_id)
) ${createTableSuffix};

CREATE TABLE oauth_client_token (
  token_id VARCHAR(256) ${varcharQualifier},
  token ${longVarBinary},
  authentication_id VARCHAR(256) ${varcharQualifier},
  user_name VARCHAR(256) ${varcharQualifier},
  client_id VARCHAR(256) ${varcharQualifier},
  PRIMARY KEY (authentication_id)
) ${createTableSuffix};

CREATE TABLE oauth_access_token (
  token_id VARCHAR(256) ${varcharQualifier},
  token ${longVarBinary},
  authentication_id VARCHAR(256) ${varcharQualifier},
  user_name VARCHAR(256) ${varcharQualifier},
  client_id VARCHAR(256) ${varcharQualifier},
  authentication ${longVarBinary},
  refresh_token VARCHAR(256) ${varcharQualifier},
  PRIMARY KEY (authentication_id)
) ${createTableSuffix};

CREATE TABLE oauth_refresh_token (
  token_id VARCHAR(256) ${varcharQualifier},
  token ${longVarBinary},
  authentication ${longVarBinary}
) ${createTableSuffix};

CREATE TABLE oauth_code (
  code VARCHAR(256) ${varcharQualifier},
  authentication ${longVarBinary}
) ${createTableSuffix};

CREATE TABLE oauth_approvals (
	userId VARCHAR(256) ${varcharQualifier},
	clientId VARCHAR(256) ${varcharQualifier},
	scope VARCHAR(256) ${varcharQualifier},
	status VARCHAR(10) ${varcharQualifier},
	expiresAt TIMESTAMP,
	lastModifiedAt TIMESTAMP
) ${createTableSuffix};