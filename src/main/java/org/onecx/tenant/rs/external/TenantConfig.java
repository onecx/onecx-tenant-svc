package org.onecx.tenant.rs.external;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithName;

@ConfigMapping(prefix = "onecx.tenant")
public interface TenantConfig {

    @WithName("header.token")
    String headerToken();

    @WithName("default.enabled")
    boolean defaultTenantEnabled();

    @WithName("default.tenant-id")
    String defaultTenantId();

    @WithName("token.claim.org-id")
    String tokenOrgClaim();

    @WithName("token.verified")
    boolean tokenVerified();

    @WithName("token.issuer.public-key-location.suffix")
    String tokenPublicKeyLocationSuffix();

    @WithName("token.issuer.public-key-location.enabled")
    boolean tokenPublicKeyEnabled();
}
