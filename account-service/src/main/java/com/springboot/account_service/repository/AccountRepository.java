package com.springboot.account_service.repository;

import com.springboot.account_service.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface AccountRepository extends JpaRepository<Account,Long> {

   public void deleteByBankId(Long accountId);

    public List<Account> findByBankId(Long accountId);
}
