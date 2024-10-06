package com.dws.challenge.service;

import static java.util.Objects.isNull;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dws.challenge.domain.Account;
import com.dws.challenge.exception.MoneyTransferException;
import com.dws.challenge.repository.AccountsRepository;

import lombok.Getter;

@Service
public class AccountsService {

  @Getter
  private final AccountsRepository accountsRepository;

  @Autowired
  public AccountsService(AccountsRepository accountsRepository) {
    this.accountsRepository = accountsRepository;
  }
  
  @Autowired
  private NotificationService notificationService;

  public void createAccount(Account account) {
    this.accountsRepository.createAccount(account);
  }

  public Account getAccount(String accountId) {
    return this.accountsRepository.getAccount(accountId);
  }

  //This will make the method thread-safe
  public synchronized void transferMoney(String accountFromId, String accountToId, double amount) 
		  throws MoneyTransferException {
	  this.moneyTransferValidator(accountFromId, accountToId, amount);
	  
	  //update both the accounts  
	  Account accountFrom = this.getAccount(accountFromId);
	  Account accountTo = this.getAccount(accountToId);
	  
	  Double amountDeducted = accountFrom.getBalance().doubleValue() - amount;
	  Double amountAdded = accountTo.getBalance().doubleValue() + amount;
	  
	  accountFrom.setBalance(BigDecimal.valueOf(amountDeducted));
	  accountTo.setBalance(BigDecimal.valueOf(amountAdded));
	  
	  this.accountsRepository.updateAccount(accountFrom);
	  this.accountsRepository.updateAccount(accountTo);
	  
	  notificationService.notifyAboutTransfer(accountFrom, ""+amount
			  +" has been transferred to account "+accountTo.getAccountId());
	  notificationService.notifyAboutTransfer(accountTo, ""+amount
			  + " has been credited from account "+accountFrom.getAccountId());
	
  }
  
  public void moneyTransferValidator(String accountFromId, String accountToId, double amount) 
		  throws MoneyTransferException{
	  
	  if(isNull(accountFromId) || isNull(accountToId) || isNull(amount)) {
		  throw new MoneyTransferException("Please enter all the fields.");
	  }
	  if(amount <= 0) {
		  throw new MoneyTransferException("Transfer amount must be positive.");
      }
	  if(accountFromId.equals(accountToId)) {
		  throw new MoneyTransferException("Account numbers cannot be same for money transfer.");
	  }
	  
	  Account accountFrom = this.getAccount(accountFromId);
	  Account accountTo = this.getAccount(accountToId);
	  
	  if(isNull(accountFrom) || isNull(accountFrom.getAccountId())) {
		  throw new MoneyTransferException("Please Enter Valid Account Number to Transfer From.");
	  }
	  
	  if(isNull(accountTo) || isNull(accountTo.getAccountId())) {
		  throw new MoneyTransferException("Please Enter Valid Account Number to Transfer To.");
	  }
	  
	  //check the balance should not be zero 
	  if(accountFrom.getBalance().doubleValue() - amount < 0) {
		  throw new MoneyTransferException("Overdraft not Allowed.");
	  }
	  
  }
  
  
}
