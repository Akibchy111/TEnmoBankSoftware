package com.techelevator.tenmo.dao;


import com.techelevator.tenmo.model.Transfers;

import java.math.BigDecimal;
import java.util.List;

public interface AccountsDao {

    BigDecimal getBalance(int user_id);

    void transfer(Transfers transfer);

    List<Transfers> listOfTransfers(int user_id);

    String getUsername(int accountId);

    String getTransferTypeString(int transferTypeId);

    String getTransferStatusString(int transferStatusId);

    Transfers getTransferDetails(int transfer_id);
}
