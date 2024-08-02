package org.tkit.onecx.tenant.domain.services;

import io.quarkus.runtime.annotations.ConfigDocFilename;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;

/**
 * Tenant service configuration
 */
@ConfigDocFilename("onecx-tenant-svc.adoc")
@ConfigRoot(phase = ConfigPhase.RUN_TIME)
@ConfigMapping(prefix = "onecx.tenant")
public interface TenantConfig {

    /**
     * Enable or disable default no claim tenant
     */
    @WithName("default.no-claim.enabled")
    @WithDefault("true")
    boolean defaultNoClaimTenantEnabled();

    /**
     * Default no claim tenant ID
     */
    @WithName("default.no-claim.tenant-id")
    @WithDefault("default")
    String defaultNoClaimTenantId();

    /**
     * Enable or disable default tenant
     */
    @WithName("default.enabled")
    @WithDefault("true")
    boolean defaultTenantEnabled();

    /**
     * Default tenant ID
     */
    @WithName("default.tenant-id")
    @WithDefault("default")
    String defaultTenantId();

    /**
     * Token claim ID of the organization ID
     */
    @WithName("token.claim.org-id")
    @WithDefault("orgId")
    String tokenOrgClaim();

    /**
     * Tenant resolver configuration
     */
    @WithName("resolver")
    TenantResolverConfig resolver();

    /**
     * Tenant resolver configuration
     */
    interface TenantResolverConfig {

        /**
         * Enable or disable default tenant
         */
        @WithName("default.enabled")
        @WithDefault("true")
        boolean defaultTenantEnabled();

        /**
         * Default tenant ID
         */
        @WithName("default.tenant-id")
        @WithDefault("default")
        String defaultTenantId();

    }
}
