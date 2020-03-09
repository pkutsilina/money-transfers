package com.kutilina.transactions.dao;

import com.kutilina.transactions.AppProperties;
import com.kutilina.transactions.exception.AccountException;
import com.kutilina.transactions.model.Account;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AccountDAO {
    private static final Logger LOGGER = Logger.getLogger(AccountDAO.class);

    private static final String CONNECTION_URL = AppProperties.getProperty("db.url");
    private static final String PASSWORD = AppProperties.getProperty("db.password");
    private static final String USER = AppProperties.getProperty("db.user");
    private static final String SELECT_ALL_STMT = "SELECT * FROM public.accounts;";
    private static final String SELECT_BY_ID_STMT = "SELECT * FROM public.accounts WHERE id = ?;";

    private static AccountDAO instance;

    public static synchronized AccountDAO getInstance() {
        if (instance == null) {
            instance = new AccountDAO();
        }
        return instance;
    }

    private AccountDAO() { }

    public Account getAccountByID(Long accountID) {
        Account account = null;
        try {
            try (Connection connection = DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD)) {
                try (PreparedStatement stmt = connection.prepareStatement(SELECT_BY_ID_STMT)) {
                    stmt.setLong(1, accountID);
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        account = new Account();
                        account.setId(rs.getLong("id"));
                        account.setBalance((rs.getDouble("balance")));
                    }
                    rs.close();
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error while retrieving accounts: ", e);
            throw new AccountException(AccountException.ErrorCode.UNEXPECTED);
        }
        return account;
    }

    public List<Account> getAllAccounts() {
        List<Account> accounts = new ArrayList<>();
        try {
            try (Connection connection = DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD)) {
                try (PreparedStatement stmt = connection.prepareStatement(SELECT_ALL_STMT)) {
                    ResultSet rs = stmt.executeQuery();
                    while (rs.next()) {
                        Account account = new Account();
                        account.setId(rs.getLong("id"));
                        account.setBalance((rs.getDouble("balance")));
                        accounts.add(account);
                    }

                    rs.close();
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error while retrieving accounts: ", e);
            throw new AccountException(AccountException.ErrorCode.UNEXPECTED);
        }
        return accounts;
    }

}
