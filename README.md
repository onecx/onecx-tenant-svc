# OneCX-tenant-svc

OneCX Tenant Service

In the OneCX Portal we want to support Multi-Tenancy. In order to do so this application was created:
TenantResolver Microservice To resolve Tenant - by user ID & Org-Party Id.

## Configuration

```properties
# header parameter
onecx.tenant.header.token=apm-principal-token
# enable or disable verified of the token
onecx.tenant.token.verified=true
# issuer suffix
onecx.tenant.token.issuer.public-key-location.suffix=/protocol/openid-connect/certs
# replace issuer location
onecx.tenant.token.issuer.public-key-location.enabled=false
# enable or disable default tenant
onecx.tenant.default.enabled=true
# default tenant value
onecx.tenant.default.tenant-id=default
# token organization claim
onecx.tenant.token.claim.org-id=orgId
```

## Data import

To enable data import for local development use these properties:

```properties
tkit.dataimport.enabled=true
tkit.dataimport.configurations.tenant.file=tenant-example-file.json
tkit.dataimport.configurations.tenant.metadata.operation=CLEAN_INSERT
tkit.dataimport.configurations.tenant.enabled=true
tkit.dataimport.configurations.tenant.stop-at-error=true
```

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
mvn compile quarkus:dev
```


