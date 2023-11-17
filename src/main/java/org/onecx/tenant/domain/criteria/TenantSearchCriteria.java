package org.onecx.tenant.domain.criteria;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Getter;
import lombok.Setter;

@RegisterForReflection
@Getter
@Setter
public class TenantSearchCriteria {

    private String orgId;

    private Integer pageNumber;

    private Integer pageSize;
}
