package com.kutilina.transactions.resources;

import com.kutilina.transactions.exception.AccountException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class TransferExceptionMapper implements ExceptionMapper<AccountException> {
    @Override
    public Response toResponse(AccountException ex) {
        Response response = Response.serverError().build();
        switch (ex.getCode()) {
            case ACCOUNT_NOT_FOUND:
                response = Response.status(404)
                        .entity(new ErrorEntity(ex.getMessage()))
                        .type(MediaType.APPLICATION_JSON_TYPE)
                        .build();
                break;

            case INSUFFICIENT_MONEY:
            case INVALID_AMOUNT:
            case SELF_TRANSFER:
                response = Response.status(422)
                        .entity(new ErrorEntity(ex.getMessage()))
                        .type(MediaType.APPLICATION_JSON_TYPE)
                        .build();
                break;
            case UNEXPECTED:
                response = Response.status(500)
                        .entity(new ErrorEntity("Server error"))
                        .type(MediaType.APPLICATION_JSON_TYPE)
                        .build();
                break;
        }
        return response;
    }

    private class ErrorEntity {
        String message;

        public ErrorEntity() {
        }

        public ErrorEntity(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
