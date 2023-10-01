/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.toywhats;
import java.util.List;
import java.io.Serializable;
/**
 *
 * @author gabri
 */
public class User implements Serializable{
    private String login;
    private byte[] passwordScrypt;
    private String phone;
    private byte[] salt;
    private byte[] authenticatorSalt;
    
    public User(String newLogin, byte[] newPasswordScrypt, String newPhone, byte[] newSalt, byte[] newAuthenticatorSalt){
        login = newLogin;
        passwordScrypt = newPasswordScrypt;
        phone = newPhone;
        salt = newSalt;
        authenticatorSalt = newAuthenticatorSalt;
    }
    
    public String getLogin(){
        return login;
    }
    
    public byte[] getPassword(){
        return passwordScrypt;
    }
        
    public String getPhone(){
        return phone;
    }
            
    public byte[] getSalt(){
        return salt;
    }
    
    public byte[] getAuthenticatorSalt(){
        return authenticatorSalt;
    }
}
