package com.kutilina.transactions.dao;

import com.kutilina.transactions.AppProperties;
import com.kutilina.transactions.exception.AccountException;
import com.kutilina.transactions.model.MoneyTransaction;
import org.apache.log4j.Logger;

import javax.validation.constraints.NotNull;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MoneyTransactionDAO {
    private static final Logger LOGGER = Logger.getLogger(MoneyTransactionDAO.class);

    private static final String CONNECTION_URL = AppProperties.getProperty("db.url");
    private static final String PASSWORD = AppProperties.getProperty("db.password");
    private static final String USER = AppProperties.getProperty("db.user");
    private static final String INSERT_TRANSACTION = "INSERT INTO public.transactions (transfer_id, " +
            "transaction_date, account_id, amount) " +
            "VALUES (?, ?, ?, ?)";
    private static final String UPDATE_TRGT_ACCOUNT = "UPDATE public.accounts SET balance = balance + ? " +
            "WHERE id = ?";
    private static final String UPDATE_SRC_ACCOUNT = "UPDATE public.accounts SET balance = balance - ? " +
            "WHERE id = ?";
    private static final String SELECT_TRS_BY_ACC_ID = "SELECT * FROM public.transactions WHERE account_id = ? ORDER " +
            "BY transaction_date DESC;";

    private static MoneyTransactionDAO instance;

    public static synchronized MoneyTransactionDAO getInstance() {
        if (instance == null) {
            instance = new MoneyTransactionDAO();
        }
        return instance;
    }

    private MoneyTransactionDAO() { }

    public void transferAmount(Long sourceAccountID, Long targetAccountID, Double amount) {
        UUID transferID = UUID.randomUUID();
        Timestamp currentTime = getCurrentTime();

        try {
            doInTransaction((Connection connection) -> {
                PreparedStatement insrtTargetTransaction = connection.prepareStatement(INSERT_TRANSACTION);
                insrtTargetTransaction.setString(1, transferID.toString());
                insrtTargetTransaction.setTimestamp(2, currentTime);
                insrtTargetTransaction.setLong(3, targetAccountID);
                insrtTargetTransaction.setDouble(4, amount);
                insrtTargetTransaction.execute();

                PreparedStatement insrtSourceTransaction = connection.prepareStatement(INSERT_TRANSACTION);
                insrtSourceTransaction.setString(1, transferID.toString());
                insrtSourceTransaction.setTimestamp(2, currentTime);
                insrtSourceTransaction.setLong(3, sourceAccountID);
                insrtSourceTransaction.setDouble(4, -amount);
                insrtSourceTransaction.execute();

                PreparedStatement updSourceAcc = connection.prepareStatement(UPDATE_SRC_ACCOUNT);
                updSourceAcc.setDouble(1, amount);
                updSourceAcc.setLong(2, sourceAccountID);
                updSourceAcc.execute();

                PreparedStatement updTrgtAcc = connection.prepareStatement(UPDATE_TRGT_ACCOUNT);
                updTrgtAcc.setDouble(1, amount);
                updTrgtAcc.setLong(2, targetAccountID);
                updTrgtAcc.execute();
            });
        } catch (SQLException e) {
            LOGGER.error("Error while transfer amount: ", e);
        }
    }

    public List<MoneyTransaction> getTransactionsByAccountID(@NotNull Long accountID) {
        List<MoneyTransaction> transactions = new ArrayList<>();
        try {
            try (Connection connection = DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD)) {
                try (PreparedStatement stmt = connection.prepareStatement(SELECT_TRS_BY_ACC_ID)) {
                    stmt.setLong(1, accountID);
                    ResultSet rs = stmt.executeQuery();
                    while (rs.next()) {
                        MoneyTransaction transaction = new MoneyTransaction();
                        transaction.setId(rs.getLong("id"));
                        transaction.setTransferID(UUID.fromString(rs.getString("transfer_id")));
                        transaction.setAmount((rs.getDouble("amount")));
                        transaction.setDate((rs.getTimestamp("transaction_date")));
                        transactions.add(transaction);
                    }
                    rs.close();
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error while retrieving transactions: ", e);
            throw new AccountException(AccountException.ErrorCode.UNEXPECTED);
        }
        return transactions;
    }

    private Timestamp getCurrentTime() {
        ZonedDateTime gmt = ZonedDateTime.now().withZoneSameInstant(ZoneId.of("UTC"));
        return Timestamp.valueOf(gmt.toLocalDateTime());
    }

    private static void doInTransaction(sqlConsumer<Connection> f) throws SQLException {
        Connection connection = null;
        boolean initialAutocommit = false;
        try {
            connection = DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD);
            initialAutocommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            f.consume(connection);
            connection.commit();
        } catch (SQLException ex) {
            if (connection != null) {
                connection.rollback();
            }
            throw ex;
        } finally {
            if (connection != null) {
                try {
                    if (initialAutocommit) {
                        connection.setAutoCommit(true);
                    }
                    connection.close();
                } catch (SQLException ex) {
                    System.err.println("Could not close connection " + ex.toString());
                }
            }
        }
    }

    @FunctionalInterface
    private interface sqlConsumer<C> {
        void consume(C c) throws SQLException;
    }
}

