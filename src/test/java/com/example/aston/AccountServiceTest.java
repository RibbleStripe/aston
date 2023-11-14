package com.example.aston;

import com.example.aston.exception.WrongPinException;
import com.example.aston.exception.NotEnoughFundsException;
import com.example.aston.exception.NotFoundException;
import com.example.aston.model.Account;
import com.example.aston.model.Operation;
import com.example.aston.model.Transaction;
import com.example.aston.repo.AccountRepository;
import com.example.aston.repo.TransactionRepository;
import com.example.aston.service.AccountServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountServiceImpl accountService;

    @Test
    public void createAccount_WithValidValues_returnsCreatedAccount() {
        String name = "name1";
        String pinCode = "1111";

        Account account = new Account(name, pinCode);
        Mockito.doReturn(account).when(accountRepository).save(any(Account.class));

        Optional<Account> createdAccount = accountService.createAccount(name, pinCode);

        assertTrue(createdAccount.isPresent());
        assertEquals(createdAccount.get().getName(), name);
        assertEquals(createdAccount.get().getPinCode(), pinCode);

    }

    @Test
    public void createAccount_WithInvalidValues_throwsInvalidPinException() {
        assertThrows(WrongPinException.class, () -> accountService.createAccount(null, "111"));
        assertThrows(WrongPinException.class, () -> accountService.createAccount("Name", "11"));
    }

    @Test
    void getAllAccounts_ReturnsAllAccountsFromRepository() {
        Account account1 = new Account("name1", "1111");
        Account account2 = new Account("name2", "2222");

        List<Account> accountList = Arrays.asList(account1, account2);

        when(accountRepository.findAll()).thenReturn(accountList);

        List<Account> accounts = accountService.getAccounts();

        assertEquals(accountList, accounts);
    }

    @Test
    void getAccountByAccountNumber_WithValidAccountNumber_ReturnsAccount() {
        String accountNumber = "f47ac10b-58cc-4372-a567-0e02b2c3d479";
        Account account = new Account(accountNumber, "Name", "1111", BigDecimal.ZERO);

        when(accountRepository.findById(accountNumber)).thenReturn(Optional.of(account));

        Optional<Account> accountByAccountNumber = accountService.getAccountByAccountNumber("f47ac10b-58cc-4372-a567-0e02b2c3d479");

        assertTrue(accountByAccountNumber.isPresent());
        assertEquals(accountByAccountNumber.get(), account);
    }

    @Test
    public void deposit_WithValidAccountNumber_DepositsAmountAndSavesTransaction() {
        String accountNumber = "f47ac10b-58cc-4372-a567-0e02b2c3d479";
        BigDecimal amountToDeposit = new BigDecimal("1000.00");
        Account existingAccount = new Account(accountNumber, "Name123", "3333", new BigDecimal("222.22"));
        Transaction transaction = new Transaction(accountNumber, accountNumber, LocalTime.now(), amountToDeposit, Operation.DEPOSIT);

        when(accountRepository.findById(accountNumber)).thenReturn(Optional.of(existingAccount));
        when(transactionRepository.save(any())).thenReturn(transaction);
        accountService.deposit(accountNumber, amountToDeposit);

        verify(accountRepository).save(existingAccount);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
        assertEquals(new BigDecimal("1222.22"), existingAccount.getAmount());
    }

    @Test
    void deposit_ThrowsNotFoundException_WhenAccountNotFound() {
        String accountNumber = "f47ac10b-58cc-4372-a567-0e02b2c3d479";

        when(accountRepository.findById(accountNumber)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> accountService.deposit(accountNumber, BigDecimal.TEN));

        verify(accountRepository, times(1)).findById(accountNumber);
    }

    @Test
    void deposit_IncreasesAccountBalance() {
        String accountNumber = "f47ac10b-58cc-4372-a567-0e02b2c3d479";
        Account account = new Account(accountNumber, "Name Surname", "7777", BigDecimal.ONE);


        when(accountRepository.findById(accountNumber)).thenReturn(Optional.of(account));

        BigDecimal amount = new BigDecimal(222);
        accountService.deposit(accountNumber, amount);

        BigDecimal expectedBalance = BigDecimal.valueOf(223);
        assertEquals(expectedBalance, account.getAmount());

        verify(accountRepository, times(1)).save(account);
    }

    @Test
    void deposit_CreatesTransaction() {
        String accountNumber = "f47ac10b-58cc-4372-a567-0e02b2c3d479";
        Account account = new Account(accountNumber, "Name Surname", "7777", BigDecimal.TEN);

        when(accountRepository.findById(accountNumber)).thenReturn(Optional.of(account));

        BigDecimal amount = new BigDecimal(100);
        accountService.deposit(accountNumber, amount);

        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    public void transferSuccess() {
        String fromAccountNumber = "f47ac10b-58cc-4372-a567-0e02b2c3d479";
        Account fromAccount = new Account(fromAccountNumber, "Name Surname1", "7777", BigDecimal.TEN);
        String toAccountNumber = "f47ac10b-58cc-4372-a567-0e02b2c3d478";
        Account toAccount = new Account(toAccountNumber, "Name Surname2", "8888", BigDecimal.ZERO);

        when(accountRepository.findById(fromAccountNumber)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findById(toAccountNumber)).thenReturn(Optional.of(toAccount));

        accountService.transfer(fromAccountNumber, toAccountNumber, BigDecimal.TEN, "7777");

        assertEquals(BigDecimal.ZERO, fromAccount.getAmount());
        assertEquals(BigDecimal.TEN, toAccount.getAmount());
    }

    @Test
    void transfer_CreatesTransaction() {
        String fromAccountNumber = "f47ac10b-58cc-4372-a567-0e02b2c3d479";
        Account fromAccount = new Account(fromAccountNumber, "Name Surname1", "7777", BigDecimal.TEN);
        String toAccountNumber = "f47ac10b-58cc-4372-a567-0e02b2c3d478";
        Account toAccount = new Account(toAccountNumber, "Name Surname2", "8888", BigDecimal.ZERO);

        when(accountRepository.findById(fromAccountNumber)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findById(toAccountNumber)).thenReturn(Optional.of(toAccount));

        accountService.transfer(fromAccountNumber, toAccountNumber, BigDecimal.TEN, "7777");

        verify(transactionRepository, times(1)).save(any(Transaction.class));

    }

    @Test()
    public void transferNotEnoughFunds() {
        String fromAccountNumber = "f47ac10b-58cc-4372-a567-0e02b2c3d479";
        Account fromAccount = new Account(fromAccountNumber, "Name Surname1", "7777", BigDecimal.TEN);
        String toAccountNumber = "f47ac10b-58cc-4372-a567-0e02b2c3d478";
        Account toAccount = new Account(toAccountNumber, "Name Surname2", "8888", BigDecimal.ZERO);


        when(accountRepository.findById(fromAccountNumber)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findById(toAccountNumber)).thenReturn(Optional.of(toAccount));

        assertThrows(NotEnoughFundsException.class, () -> accountService.transfer(fromAccountNumber, toAccountNumber, BigDecimal.valueOf(20), "7777"));
    }

    @Test()
    public void transferInvalidPin() {
        String fromAccountNumber = "f47ac10b-58cc-4372-a567-0e02b2c3d479";
        Account fromAccount = new Account(fromAccountNumber, "Name Surname1", "7777", BigDecimal.TEN);
        String toAccountNumber = "f47ac10b-58cc-4372-a567-0e02b2c3d478";
        Account toAccount = new Account(toAccountNumber, "Name Surname2", "8888", BigDecimal.ZERO);

        when(accountRepository.findById(fromAccountNumber)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findById(toAccountNumber)).thenReturn(Optional.of(toAccount));

        assertThrows(NotEnoughFundsException.class, () -> accountService.transfer(fromAccountNumber, toAccountNumber, BigDecimal.valueOf(20), "7777"));
    }


    @Test
    public void transferAccountNotFound() {
        String fromAccountNumber = "f47ac10b-58cc-4372-a567-0e02b2c3d479";
        Account fromAccount = new Account(fromAccountNumber, "Name Surname1", "7777", BigDecimal.TEN);
        String toAccountNumber = "f47ac10b-58cc-4372-a567-0e02b2c3d478";

        when(accountRepository.findById(fromAccountNumber)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findById(toAccountNumber)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                accountService.transfer(fromAccountNumber, toAccountNumber, BigDecimal.TEN, "7777"));
    }

    @Test
    public void testWithdrawSuccess() {
        String fromAccountNumber = "f47ac10b-58cc-4372-a567-0e02b2c3d479";
        Account fromAccount = new Account(fromAccountNumber, "Name Surname1", "7777", BigDecimal.TEN);

        when(accountRepository.findById(fromAccountNumber)).thenReturn(Optional.of(fromAccount));

        accountService.withdraw(fromAccountNumber, BigDecimal.TEN, fromAccount.getPinCode());

        assertEquals(BigDecimal.ZERO, fromAccount.getAmount());
    }

    @Test
    public void testWithdrawNotFoundException() {
        String accountNumber = "66666";
        BigDecimal amount = new BigDecimal(100);
        String pinCode = "1111";

        assertThrows(NotFoundException.class, () ->
                accountService.withdraw(accountNumber, amount, pinCode)
        );
    }

    @Test
    public void testWithdrawNotEnoughFundsException() {
        String fromAccountNumber = "f47ac10b-58cc-4372-a567-0e02b2c3d479";
        Account fromAccount = new Account(fromAccountNumber, "Name Surname1", "7777", BigDecimal.TEN);

        when(accountRepository.findById(fromAccountNumber)).thenReturn(Optional.of(fromAccount));

        assertThrows(NotEnoughFundsException.class, () ->
                accountService.withdraw(fromAccountNumber, BigDecimal.valueOf(25), fromAccount.getPinCode())
        );
    }

    @Test
    public void testWithdrawInvalidPinException() {
        String fromAccountNumber = "f47ac10b-58cc-4372-a567-0e02b2c3d479";
        Account fromAccount = new Account(fromAccountNumber, "Name Surname1", "7777", BigDecimal.TEN);

        when(accountRepository.findById(fromAccountNumber)).thenReturn(Optional.of(fromAccount));

        assertThrows(WrongPinException.class, () ->
                accountService.withdraw(fromAccountNumber, BigDecimal.TEN, "111")
        );
    }
}
