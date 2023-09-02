create table users(username varchar(50) not null primary key,password varchar(500) not null,enabled boolean not null);
create table authorities (username varchar(50) not null,authority varchar(50) not null,constraint fk_authorities_users foreign key(username) references users(username));
create unique index ix_auth_username on authorities (username,authority);

-- used in tests that use HSQL
create table oauth_client_details (
                                      client_id VARCHAR(256) PRIMARY KEY,
                                      resource_ids VARCHAR(256),
                                      client_secret VARCHAR(256),
                                      scope VARCHAR(256),
                                      authorized_grant_types VARCHAR(256),
                                      web_server_redirect_uri VARCHAR(256),
                                      authorities VARCHAR(256),
                                      access_token_validity INTEGER,
                                      refresh_token_validity INTEGER,
                                      additional_information VARCHAR(4096),
                                      autoapprove VARCHAR(256)
);

create table oauth_client_token (
                                    token_id VARCHAR(256),
                                    token binary,
                                    authentication_id VARCHAR(256) PRIMARY KEY,
                                    user_name VARCHAR(256),
                                    client_id VARCHAR(256)
);

create table oauth_access_token (
                                    token_id VARCHAR(256),
                                    token binary,
                                    authentication_id VARCHAR(256) PRIMARY KEY,
                                    user_name VARCHAR(256),
                                    client_id VARCHAR(256),
                                    authentication varbinary(4000),
                                    refresh_token VARCHAR(256)
);

create table oauth_refresh_token (
                                     token_id VARCHAR(256),
                                     token varbinary(4000),
                                     authentication  varbinary(4000)
);

create table oauth_code (
                            code VARCHAR(256), authentication  varbinary(4000)
);

create table oauth_approvals (
                                 userId VARCHAR(256),
                                 clientId VARCHAR(256),
                                 scope VARCHAR(256),
                                 status VARCHAR(10),
                                 expiresAt TIMESTAMP,
                                 lastModifiedAt TIMESTAMP
);


-- customized oauth_client_details table
create table ClientDetails (
                               appId VARCHAR(256) PRIMARY KEY,
                               resourceIds VARCHAR(256),
                               appSecret VARCHAR(256),
                               scope VARCHAR(256),
                               grantTypes VARCHAR(256),
                               redirectUrl VARCHAR(256),
                               authorities VARCHAR(256),
                               access_token_validity INTEGER,
                               refresh_token_validity INTEGER,
                               additionalInformation VARCHAR(4096),
                               autoApproveScopes VARCHAR(256)
);