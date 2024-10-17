package com.manager.class_activity.qnu.service;

import com.manager.class_activity.qnu.entity.Account;
import com.manager.class_activity.qnu.exception.BadException;
import com.manager.class_activity.qnu.exception.ErrorCode;
import com.manager.class_activity.qnu.repository.AccountRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AccountService {
    AccountRepository accountRepository;
    public void saveAccount(Account account) {
        if(accountRepository.existsByUsernameAndIsDeleted(account.getUsername(), false)) {
            throw new BadException(ErrorCode.USER_EXISTED);
        }
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        accountRepository.save(account);
    }

    public void deleteAccount(int id) {
        Account account = getAccount(id);
        account.setDeleted(true);
        accountRepository.save(account);
    }
    public Account getAccount(int id) {
        return accountRepository.findByIdAndIsDeleted(id,false)
                .orElseThrow(()->new BadException(ErrorCode.USER_NOT_EXISTED));
    }
}
