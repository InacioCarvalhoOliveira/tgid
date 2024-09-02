package com.br.tgid.tgid.entity;

import com.br.tgid.tgid.service.validation.CNPJValidator;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "companies")
public class Company extends User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String cnpj;

    private double balance;

    private double systemTax;

    public Company() {
    }

    public Company(Long id, String name, String cnpj, double balance, double systemTax) {
        super(id, name);
        if (!CNPJValidator.isvalidCnpj(cnpj)) {
            throw new IllegalArgumentException("CNPJ inválido.");
        }
        this.cnpj = cnpj;
        this.systemTax = systemTax;
        this.balance = balance;
    }

    public boolean checkCNPJ() {
        return CNPJValidator.isvalidCnpj(this.cnpj);
    }

    public void balanceUpdate(double value) {
        this.balance += value;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public double getSystemTax() {
        return systemTax;
    }

    public void setSystemTax(double systemTax) {
        this.systemTax = systemTax;
    }
}
