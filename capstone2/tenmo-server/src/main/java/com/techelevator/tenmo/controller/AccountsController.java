package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountsDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Transfers;
import com.techelevator.tenmo.model.User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@PreAuthorize("isAuthenticated()")
public class AccountsController {

    private AccountsDao accountsDao;
    private UserDao userDao;

    public AccountsController(AccountsDao accountsDao, UserDao userDao) {
        this.accountsDao = accountsDao;
        this.userDao = userDao;
    }

    @RequestMapping(path = "accounts/{id}", method = RequestMethod.GET)
    public BigDecimal getBalance(@PathVariable int id) {
        return accountsDao.getBalance(id);
    }

    @RequestMapping(path = "/users", method = RequestMethod.GET)
    public List<User> list() {
        return userDao.findAll();
    }

    @RequestMapping(path = "/transfers", method = RequestMethod.POST)
    public void transfers(@RequestBody Transfers transfers) {
        accountsDao.transfer(transfers);
    }

    @RequestMapping(path = "accounts/username/{account_id}", method = RequestMethod.GET)
    public String getUsername(@PathVariable int account_id) {
        return accountsDao.getUsername(account_id);
    }

    @RequestMapping(path = "/transfers/{id}", method = RequestMethod.GET)
    public List<Transfers> listOfTransfer(@PathVariable int id) {
        return accountsDao.listOfTransfers(id);
    }

    @RequestMapping(path = "transfer/transfer_type/{transfer_type_id}", method = RequestMethod.GET)
    public String getTransferTypeString(@PathVariable int transfer_type_id) {
        return accountsDao.getTransferTypeString(transfer_type_id);
    }

    @RequestMapping(path = "transfer/transfer_status/{transfer_status_id}", method = RequestMethod.GET)
    public String getTransferStatusString(@PathVariable int transfer_status_id) {
        return accountsDao.getTransferStatusString(transfer_status_id);
    }

    @RequestMapping(path = "/transferDetails/{id}", method = RequestMethod.GET)
    public Transfers getTransferDetails(@PathVariable int id) {
        return accountsDao.getTransferDetails(id);
    }

}
