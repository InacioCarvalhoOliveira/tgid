package com.br.tgid.tgid.service;

import com.br.tgid.tgid.entity.Company;
import com.br.tgid.tgid.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CompanyService {

    @Autowired
    private CompanyRepository companyRepository;

    public Company createCompany(Company company) {
        if (!company.checkCNPJ()) {
            throw new IllegalArgumentException("CNPJ inválido.");
        }

        Optional<Company> existingCompany = companyRepository.findByCnpj(company.getCnpj());
        if (existingCompany.isPresent()) {
            throw new IllegalArgumentException("Já existe uma empresa com este CNPJ.");
        }

        return companyRepository.save(company);
    }

    public Company updateCompany(Long id, Company company) {
        if (!company.checkCNPJ()) {
            throw new IllegalArgumentException("CNPJ inválido.");
        }
        company.setId(id);
        return companyRepository.save(company);
    }

    public Company save(Company company) {
        return companyRepository.save(company);
    }

    public Optional<Company> findById(Long id) {
        return companyRepository.findById(id);
    }

    public void deleteById(Long id) {
        companyRepository.deleteById(id);
    }

    public boolean existsById(Long id) {
        return companyRepository.existsById(id);
    }
}
