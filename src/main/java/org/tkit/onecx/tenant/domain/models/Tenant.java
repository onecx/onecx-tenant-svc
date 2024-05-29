package org.tkit.onecx.tenant.domain.models;

import jakarta.persistence.*;

import org.tkit.quarkus.jpa.models.TraceableEntity;

import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "TENANT", uniqueConstraints = {
        @UniqueConstraint(name = "TENANT_ORG_ID", columnNames = { "ORG_ID" }),
        @UniqueConstraint(name = "TENANT_TENANT_ID", columnNames = { "TENANT_ID" })
})
@SuppressWarnings("java:S2160")
public class Tenant extends TraceableEntity {

    @Column(name = "ORG_ID")
    private String orgId;

    @Column(name = "TENANT_ID")
    private String tenantId;

    @Column(name = "DESCRIPTION")
    private String description;

    /**
     * Flag to identify created by an operator
     */
    @Column(name = "OPERATOR")
    private Boolean operator;
}
