package com.dopamines.backend.wallet.repository;

import com.dopamines.backend.wallet.dto.WalletDetailDto;
import com.dopamines.backend.wallet.dto.WalletDto;
import com.dopamines.backend.wallet.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
    List<WalletDetailDto> findAllByPlanBetween(LocalDate start, LocalDate end);
    List<Wallet> findAllByAccount_Email(String email);
}
