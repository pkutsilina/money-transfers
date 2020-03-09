package com.kutilina.transactions.service;

import com.kutilina.transactions.dao.AccountDAO;
import com.kutilina.transactions.dao.MoneyTransactionDAO;
import com.kutilina.transactions.exception.AccountException;
import com.kutilina.transactions.model.Account;
import com.kutilina.transactions.model.MoneyTransaction;

import java.util.List;

public class AccountService {
    private MoneyTransactionDAO transactionDAO;
    private AccountDAO accountDAO;

    private static AccountService instance;

    private AccountService() {
        transactionDAO = MoneyTransactionDAO.getInstance();
        accountDAO = AccountDAO.getInstance();
    }

    public static synchronized AccountService getInstance() {
        synchronized (AccountService.class) {
            if (instance == null) {
                instance = new AccountService();
            }
        }
        return instance;
    }


    public void transferAmount(Long sourceAccountID, Long targetAccountID, Double amount) {
        if (amount <= 0) {
            throw new AccountException(AccountException.ErrorCode.INVALID_AMOUNT);
        }
        if (sourceAccountID.equals(targetAccountID)) {
            throw new AccountException(sourceAccountID, AccountException.ErrorCode.SELF_TRANSFER);
        }
        Account sourceAccount = accountDAO.getAccountByID(sourceAccountID);
        if (sourceAccount == null) {
            throw new AccountException(sourceAccountID, AccountException.ErrorCode.ACCOUNT_NOT_FOUND);
        } else {
            Double sourceBalance = sourceAccount.getBalance();
            if (sourceBalance < amount) {
                throw new AccountException(sourceAccountID, AccountException.ErrorCode.INSUFFICIENT_MONEY);
            }
        }
        Account targetAccount = accountDAO.getAccountByID(targetAccountID);
        if (targetAccount == null) {
            throw new AccountException(targetAccountID, AccountException.ErrorCode.ACCOUNT_NOT_FOUND);
        }

        transactionDAO.transferAmount(sourceAccountID, targetAccountID, amount);
    }

    public Account getAccountById(Long id) {
        Account account = accountDAO.getAccountByID(id);
        if (account != null) {
            return account;
        } else {
            throw new AccountException(id, AccountException.ErrorCode.ACCOUNT_NOT_FOUND);
        }
    }

    public List<Account> getAllAccounts() {
        return accountDAO.getAllAccounts();
    }

    public List<MoneyTransaction> getTransactionsByAccountID(Long id) {
        Account account = accountDAO.getAccountByID(id);
        if (account != null) {
            return transactionDAO.getTransactionsByAccountID(id);
        } else {
            throw new AccountException(id, AccountException.ErrorCode.ACCOUNT_NOT_FOUND);
        }
    }
}
