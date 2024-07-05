package org.tkit.onecx.tenant.test;

import java.util.List;

import org.tkit.quarkus.security.test.AbstractSecurityTest;
import org.tkit.quarkus.security.test.SecurityTestConfig;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class SecurityTest extends AbstractSecurityTest {
    @Override
    public SecurityTestConfig getConfig() {
        SecurityTestConfig config = new SecurityTestConfig();
        config.addConfig("read", "/internal/tenants/id", 404, List.of("ocx-tn:read"), "get");
        config.addConfig("write", "/internal/tenants", 400, List.of("ocx-tn:write"), "post");
        return config;
    }
}
