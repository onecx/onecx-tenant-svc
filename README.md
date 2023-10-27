# OneCX-tenant-svc

In the OneCX Portal we want to support Multi-Tenancy. In order to do so this application was created:
TenantResolver Microservice To resolve Tenant - by user ID & Org-Party Id.

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


