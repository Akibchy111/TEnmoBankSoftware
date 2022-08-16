package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfers;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcAccountsDao implements AccountsDao{

    private JdbcTemplate jdbcTemplate;

    public JdbcAccountsDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int getAccountId(int userId) {
        String sql = "SELECT account_id FROM accounts WHERE user_id = ?";
        int accountId = jdbcTemplate.queryForObject(sql, Integer.class, userId);
        return accountId;
    }

    @Override
    public BigDecimal getBalance(int userId) {
        int account_id = getAccountId(userId);
        String sql = "SELECT balance FROM accounts WHERE account_id = ?;";
        BigDecimal balance = jdbcTemplate.queryForObject(sql, BigDecimal.class, account_id);

       return balance;
    }

    @Override
    public void transfer(Transfers transfer) {
        int fromAccount = getAccountId(transfer.getAccountFrom());
        int toAccount = getAccountId(transfer.getAccountTo());
        int transferStatusId = transfer.getTransferStatusId();
        int transferTypeId = transfer.getTransferTypeId();
        BigDecimal amount = transfer.getAmount();
        BigDecimal fromAccountBalance = getBalance(transfer.getAccountFrom());
        BigDecimal toAccountBalance = getBalance(transfer.getAccountTo());
        BigDecimal zero = new BigDecimal(0);
        if((fromAccountBalance.subtract(amount).compareTo(zero) >= 0)) {
            String sql = "UPDATE accounts SET balance = ? WHERE account_id = ?";
            toAccountBalance = toAccountBalance.add(amount);
            fromAccountBalance = fromAccountBalance.subtract(amount);
            jdbcTemplate.update(sql, toAccountBalance, toAccount);
            jdbcTemplate.update(sql, fromAccountBalance, fromAccount);
            String sql1 = "INSERT INTO transfers VALUES (DEFAULT, ?, ?, ?, ?, ?)";
            jdbcTemplate.update(sql1, transferTypeId, transferStatusId, fromAccount, toAccount, amount);
        } else {
            transfer.setTransferStatusId(3);
            String sql2 = "INSERT INTO transfers VALUES (DEFAULT, ?, ?, ?, ?, ?)";
            jdbcTemplate.update(sql2, transferTypeId, transfer.getTransferStatusId(), fromAccount, toAccount, amount);

        }
    }

    @Override
    public String getUsername(int accountId) {
        String sql = "SELECT username FROM users \n" +
                "JOIN accounts ON users.user_id = accounts.user_id\n" +
                "WHERE account_id = ?";
        String username = jdbcTemplate.queryForObject(sql, String.class, accountId);
        return username;
    }

    @Override
    public List<Transfers> listOfTransfers(int user_id) {
        int account_id = getAccountId(user_id);
        List<Transfers> transfersList = new ArrayList<>();
        String sql = "SELECT * FROM transfers WHERE account_from = ?";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, account_id);
        while(result.next()) {
            transfersList.add(mapRowToTransfer(result));
        }
        return transfersList;
    }

    @Override
    public String getTransferTypeString(int transferTypeId) {
        String sql = "SELECT transfer_type_desc FROM transfer_types WHERE transfer_type_id = ?";
        String transferTypeString = jdbcTemplate.queryForObject(sql, String.class, transferTypeId);
        return transferTypeString;
    }

    @Override
    public String getTransferStatusString(int transferStatusId) {
        String sql = "SELECT transfer_status_desc FROM transfer_statuses WHERE transfer_status_id = ?";
        String transferStatusString = jdbcTemplate.queryForObject(sql, String.class, transferStatusId);
        return transferStatusString;
    }

    @Override
    public Transfers getTransferDetails(int transfer_id) {
        String sql = "SELECT * FROM transfers WHERE transfer_id = ?";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, transfer_id);
        Transfers transfer = null;
        while (result.next()) {
            transfer = mapRowToTransfer(result);
        }

        return transfer;
    }

    private Transfers mapRowToTransfer(SqlRowSet rs) {
        Transfers transfers = new Transfers();
        transfers.setTransferId(rs.getInt("transfer_id"));
        transfers.setTransferIdType(rs.getInt("transfer_type_id"));
        transfers.setTransferStatusId(rs.getInt("transfer_status_id"));
        transfers.setAccountFrom(rs.getInt("account_from"));
        transfers.setAccountTo(rs.getInt("account_to"));
        transfers.setAmount(rs.getBigDecimal("amount"));
        return transfers;
    }
}
