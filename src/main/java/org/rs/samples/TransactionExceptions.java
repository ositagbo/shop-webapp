package org.rs.samples;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class TransactionExceptions implements ExceptionMapper<InvalidTransactionException> {

	public TransactionExceptions() {
	}

    @Override
    public Response toResponse(InvalidTransactionException exception) {
        Response r = Response.serverError()
                .status(Response.Status.BAD_REQUEST)
                .entity(exception.getMessage())
                .build();
        return r;
    }	
}
