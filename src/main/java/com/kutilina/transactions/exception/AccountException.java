package com.kutilina.transactions.exception;

public class AccountException extends RuntimeException {
    private Long accountID;
    private ErrorCode code;

    public AccountException(Long accountID, ErrorCode code) {
        super(String.format("%s : %s", code.getMessage(), accountID));
        this.accountID = accountID;
        this.code = code;
    }

    public AccountException(ErrorCode code) {
        super(code.getMessage());
        this.code = code;
    }

    public Long getAccountID() {
        return accountID;
    }

    public ErrorCode getCode() {
        return code;
    }

    public enum ErrorCode {
        INSUFFICIENT_MONEY("Not enough money on account"),
        INVALID_AMOUNT("Only positive amount to transfer is allowed"),
        ACCOUNT_NOT_FOUND("Account not found"),
        SELF_TRANSFER("Invalid target account id"),
        UNEXPECTED("Something went wrong");

        private String message;

        ErrorCode(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }


}
