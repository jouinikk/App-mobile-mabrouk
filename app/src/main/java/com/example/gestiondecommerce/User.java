package com.example.gestiondecommerce;

import java.io.Serializable;

public class User implements Serializable {

    private String Id;
    private  String email ;
    private  String password ;
    private  String name ;
    private  String role ;
    private int tel ;
    private String commercialAffectee;
    private String clientAffectee;



    public User(String email, String name, String role, int tel) {
        this.email = email;
        this.name = name;
        this.role = role;
        this.tel = tel;
    }

    public User(String nom, String email, int tel, String password, String selectedRole, String commercialAffecte, String clientAffecte) {
        this.email = email;
        this.password = password;
        this.name = nom;
        this.role = selectedRole;
        this.tel = tel;
        this.commercialAffectee = commercialAffecte;
        this.clientAffectee=clientAffecte;
    }

    public String getCommercialAffectee() {
        return this.commercialAffectee;
    }

    public void setCommercialAffectee(String commercialAffectee) {
        this.commercialAffectee = commercialAffectee;
    }

    public String getClientAffectee() {
        return this.clientAffectee;
    }


    public void setClientAffectee(String clientAffectee) {
        this.clientAffectee = clientAffectee;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }
    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }
    public User(String id, String email, String password, String name, String role, int tel) {
        Id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.role = role;
        this.tel = tel;
    }
    public User() {}

    public String getEmail() {
        return email;
    }

    public void setTel(int tel) {
        this.tel = tel;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getTel(){
        return String.valueOf(this.tel);
    }

    @Override
    public String toString(){
        return name;
    }
}