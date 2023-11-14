package com.example.aston.controller;

import com.example.aston.dto.AccountDTO;
import com.example.aston.dto.PaymentDTO;
import com.example.aston.exception.WrongPinException;
import com.example.aston.exception.NotEnoughFundsException;
import com.example.aston.exception.NotFoundException;
import com.example.aston.model.Account;
import com.example.aston.service.AccountService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/accounts")

public class AccountController {

    private final AccountService accountService;

    @PostMapping()
    public ResponseEntity<String> saveAccount(@RequestBody AccountDTO accountJson) {
        String pinCode = accountJson.getPinCode();
        String name = accountJson.getName();

        try {
            accountService.createAccount(name, pinCode);
        } catch (WrongPinException e) {
            return new ResponseEntity<>("Wrong PIN length.", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("New account created.", HttpStatus.OK);

    }

    @GetMapping
    public ResponseEntity<List<Account>> getAllAccounts() {
        List<Account> accounts = accountService.getAccounts();
        return new ResponseEntity<>(accounts, HttpStatus.OK);
    }

    @GetMapping(value = "/{accountNumber}")
    public ResponseEntity<Account> getAccount(@PathVariable @NotBlank String accountNumber) {
        Optional<Account> accountByAccountNumber = accountService.getAccountByAccountNumber(accountNumber);
        return accountByAccountNumber.map(account -> new ResponseEntity<>(account, HttpStatus.OK)).orElseGet(() ->
                new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PatchMapping(value = "/{accountNumber}/deposit")
    public ResponseEntity<String> deposit(@PathVariable @NotBlank String accountNumber,
                                          @RequestBody PaymentDTO paymentDto) {
        BigDecimal amount = paymentDto.getAmount();

        try {
            accountService.deposit(accountNumber, amount);
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
        return new ResponseEntity<>("Transfer completed.", HttpStatus.OK);
    }

    @PatchMapping(value = "/{fromAccountNumber}/transfer/{toAccountNumber}")
    public ResponseEntity<String> transfer(@PathVariable @NotBlank String fromAccountNumber,
                                           @PathVariable @NotBlank String toAccountNumber,
                                           @RequestBody PaymentDTO paymentDto) {

        BigDecimal amount = paymentDto.getAmount();
        String pinCode = paymentDto.getPinCode();

        try {
            accountService.transfer(fromAccountNumber, toAccountNumber, amount, pinCode);

        } catch (NotFoundException e) {
            return new ResponseEntity<>("Transfer error: Account not found.", HttpStatus.BAD_REQUEST);
        } catch (WrongPinException e) {
            return new ResponseEntity<>("Transfer error: Wrong PIN.", HttpStatus.BAD_REQUEST);
        } catch (NotEnoughFundsException e) {
            return new ResponseEntity<>("Transfer error: Insufficient funds.", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Transfer completed successfully.", HttpStatus.OK);

    }

    @PatchMapping(value = "/{accountNumber}/withdraw")
    public ResponseEntity<String> withdraw(@PathVariable @NotBlank String accountNumber,
                                           @RequestBody PaymentDTO paymentDto) {
        BigDecimal amount = paymentDto.getAmount();
        String pinCode = paymentDto.getPinCode();

        try {
            accountService.withdraw(accountNumber, amount, pinCode);

        } catch (NotFoundException e) {
            return new ResponseEntity<>("Withdrawal error: Account not found.", HttpStatus.BAD_REQUEST);
        } catch (NotEnoughFundsException e) {
            return new ResponseEntity<>("Withdrawal error: Insufficient funds.", HttpStatus.BAD_REQUEST);
        } catch (WrongPinException e) {
            return new ResponseEntity<>("Withdrawal error: Wrong PIN.", HttpStatus.BAD_REQUEST);

        }
        return new ResponseEntity<>("Withdrawal completed successfully.", HttpStatus.OK);
    }
}
