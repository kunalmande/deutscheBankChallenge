package com.dws.challenge;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.dws.challenge.domain.Account;
import com.dws.challenge.service.AccountsService;

@SpringBootTest
class ChallengeApplicationTests {
	
	  @Autowired
	  private AccountsService accountsService;

	@Test
	void transferMoneyTest() {
		
		Account accountFrom = new Account("Id-123");
		accountFrom.setBalance(new BigDecimal(1000));
	    this.accountsService.createAccount(accountFrom);
	    
	    Account accountTo = new Account("Id-213");
	    accountTo.setBalance(new BigDecimal(1000));
	    this.accountsService.createAccount(accountTo);
		
		this.accountsService.transferMoney(accountFrom.getAccountId(),accountTo.getAccountId(),500);
		
		
	}
	
	
	

}
