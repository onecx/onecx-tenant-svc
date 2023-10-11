# OneCX-tenant-svc

In the OneCX Portal we need to support Multi-Tenancy. In order to do so this application was created:
TenantResolver Microservice To resolve Tenant - by user ID & Org-Party Id.

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
mvn compile quarkus:dev
```

## Develop

Before committing run command below to sort imports otherwise GitHub pipeline will fail.

```
mvn net.revelc.code.formatter:formatter-maven-plugin:2.23.0:format
```