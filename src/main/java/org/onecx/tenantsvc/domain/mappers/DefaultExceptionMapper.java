package org.onecx.tenantsvc.domain.mappers;

import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;
import static org.onecx.tenantsvc.domain.mappers.DefaultExceptionMapper.Error.*;

import java.util.Locale;

import jakarta.annotation.Priority;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.*;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import org.eclipse.microprofile.config.ConfigProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tkit.quarkus.jpa.exceptions.DAOException;

import gen.io.github.onecx.tenantsvc.rs.internal.model.RestExceptionDTO;

/**
 * The default exception mapper with priority {@code PRIORITY}.
 */
@Provider
@Priority(DefaultExceptionMapper.PRIORITY)
public class DefaultExceptionMapper implements ExceptionMapper<Exception> {

    private static final Logger log = LoggerFactory.getLogger(DefaultExceptionMapper.class);

    /**
     * The exception mapper priority
     */
    public static final int PRIORITY = 10000;

    /**
     * The request headers.
     */
    @Context
    private HttpHeaders headers;

    /**
     * The request URI info.
     */
    @Context
    UriInfo uriInfo;

    /**
     * {@inheritDoc}
     */
    @Override
    public Response toResponse(Exception e) {

        var logException = ConfigProvider.getConfig().getOptionalValue("tkit.rs.mapper.log", Boolean.class);
        if (logException.isEmpty() || logException.get()) {
            log.error("REST exception URL:{},ERROR:{}", uriInfo.getRequestUri(), e.getMessage());
        }
        if (e instanceof WebApplicationException wae) {
            return createResponse(wae);
        }
        if (e instanceof DAOException de) {
            return createResponse(de);
        }
        return createResponse(e);
    }

    /**
     * Creates the response from the {@link WebApplicationException}
     *
     * @param webAppException the {@link WebApplicationException}
     * @return the corresponding response.
     */
    protected Response createResponse(WebApplicationException webAppException) {

        var dto = new RestExceptionDTO();
        dto.setErrorCode(WEB_APPLICATION_EXCEPTION.name());
        dto.setMessage(webAppException.getMessage());
        return Response.fromResponse(webAppException.getResponse()).entity(dto).type(mediaType()).build();
    }

    protected Response createResponse(DAOException daoException) {

        var dto = new RestExceptionDTO();
        dto.setErrorCode(DAO_EXCEPTION.name());
        dto.setMessage(daoException.getMessage());

        return Response.fromResponse(Response.status(BAD_REQUEST).build()).entity(dto).type(mediaType()).build();
    }

    /**
     * Creates the response from the {@link Exception}
     *
     * @param e the {@link Exception}
     * @return the corresponding response.
     */
    protected Response createResponse(Exception e) {

        var dto = new RestExceptionDTO();
        dto.setErrorCode(UNDEFINED_ERROR_CODE.name());
        dto.setMessage(e.getMessage());
        return Response.serverError().type(mediaType()).entity(dto).build();
    }

    /**
     * Gets the media type for the response.
     *
     * @return the media type for the response.
     */
    protected MediaType mediaType() {

        return MediaType.APPLICATION_JSON_TYPE;
    }

    /**
     * Gets the response locale.
     *
     * @return the response locale.
     */
    protected Locale getLocale() {

        var locales = headers.getAcceptableLanguages();
        if (locales != null && !locales.isEmpty()) {
            var tmp = locales.get(0);
            if (tmp != null && !"*".equals(tmp.getLanguage())) {
                return tmp;
            }
        }
        return null;
    }

    /**
     * The exception mapper error codes.
     */
    public enum Error {

        /**
         * The error code for the web application exception {@link WebApplicationException}
         */
        WEB_APPLICATION_EXCEPTION,

        DAO_EXCEPTION,

        /**
         * The error code for undefined exception.
         */
        UNDEFINED_ERROR_CODE;
    }
}
