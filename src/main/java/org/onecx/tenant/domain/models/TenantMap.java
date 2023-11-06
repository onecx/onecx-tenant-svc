package org.onecx.tenant.domain.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import org.tkit.quarkus.jpa.models.TraceableEntity;

import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "tenant_map")
@SuppressWarnings("java:S2160")
public class TenantMap extends TraceableEntity {

    @Column(name = "org_id", unique = true)
    private String orgId;

    @Column(name = "tenant_id", unique = true)
    private String tenantId;
}
