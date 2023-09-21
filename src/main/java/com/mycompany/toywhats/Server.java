/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.toywhats;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.ArrayList;
import java.util.Hashtable;

import org.apache.commons.codec.binary.Hex;

/**
 *
 * @author gabri
 */
public class Server {
    private List<User> users;
    private Hashtable<String, Hashtable<String, Chat>> chats;
    
    public Server(){
        try {
            FileInputStream fi = new FileInputStream(new File("users.txt"));
            ObjectInputStream oi = new ObjectInputStream(fi);
            users = (ArrayList) oi.readObject();
        }
        catch (IOException | ClassNotFoundException ex) {
            users = new ArrayList<>();
        }
        
        try {
            FileInputStream fi = new FileInputStream(new File("chats.txt"));
            ObjectInputStream oi = new ObjectInputStream(fi);
            chats = (Hashtable) oi.readObject();
        }
        catch (IOException | ClassNotFoundException ex) {
            chats = new Hashtable<>();
        }
    }
    
    public User register(String login, byte[] passwordScrypt, String phone, byte[] salt){
        if(getUser(login) != null){
            return null;
        }
        if(getUserByPhone(phone) != null){
            return null;
        }
        User user = new User(login, passwordScrypt, phone, salt);
        users.add(user);
        saveUsers();
        return user;
    }
    
    private User getUser(String login){
        for (User user : users) {
            if(user.getLogin().equals(login)){
                return user;
            }
        }
        return null;
    }
    
    public User getUserByPhone(String phone){
        for (User user : users) {
            if(user.getPhone().equals(phone)){
                return user;
            }
        }
        return null;
    }
    
    public byte[] getSalt(String login){
        for (User user : users) {
            if(user.getLogin().equals(login)){
                return user.getSalt();
            }
        }
        return null;
    }
    
    public User login(String login, byte[] passwrodScrypt){
        User user = getUser(login);
        if(user == null || ! Hex.encodeHexString(user.getPassword()).equals(Hex.encodeHexString(passwrodScrypt))){
            System.out.println("Senha ou usuário incorreto");
            return null;
        }
        if(Authenticator.authenticate(user.getPhone())){
            return user;
        }
        System.out.println("Autenticação falhou");
        return null;
    }
    
    public List<Message> getMessages(String senderPhone, String receiverPhone){
        String phone1;
        String phone2;
        
        if(senderPhone.compareTo(receiverPhone) < 0){
            phone1 = senderPhone;
            phone2 = receiverPhone;
        }
        else{
            phone1 = receiverPhone;
            phone2 = senderPhone;
        }
        
        if(chats.get(phone1) == null || chats.get(phone1).get(phone2) == null){
            return null;
        }
        
        return chats.get(phone1).get(phone2).getMessages();
    }
    
    public boolean sendMessage(String senderPhone, String receiverPhone, Message message){
        String phone1;
        String phone2;
        
        User receiver = getUserByPhone(receiverPhone);
        if(receiver == null){ 
            return false;
        }
        User sender = getUserByPhone(senderPhone);
        if(sender == null){ 
            return false;
        }
        
        if(senderPhone.compareTo(receiverPhone) < 0){
            phone1 = senderPhone;
            phone2 = receiverPhone;
        }
        else{
            phone1 = receiverPhone;
            phone2 = senderPhone;
        }
        if(chats.get(phone1) == null){
            chats.put(phone1, new Hashtable<>());
        }
        if(chats.get(phone1).get(phone2) == null){
            chats.get(phone1).put(phone2, new Chat(phone1, phone2));
        }
        
        chats.get(phone1).get(phone2).receiveMessage(message);
        saveChats();

        return true;
    }
    
    public User[] getContacts(String phone){
       User[] contacts = new User[users.size() - 1];
       int i = 0;
        for (User user : users) {
            if(!user.getPhone().equals(phone)){
                contacts[i] = user;
                i++;
            }
        }
        return contacts;
    }
    
    private void saveUsers(){
        try {
            FileOutputStream f = new FileOutputStream(new File("users.txt"));
            ObjectOutputStream o = new ObjectOutputStream(f);
            o.writeObject(users);
        }
        catch (IOException ex) {
            System.out.println(ex);
        }
    }
    
    private void saveChats(){
        try {
            FileOutputStream f = new FileOutputStream(new File("chats.txt"));
            ObjectOutputStream o = new ObjectOutputStream(f);
            o.writeObject(chats);
        }
        catch (IOException ex) {
            System.out.println(ex);
        }
    }
    
    private boolean authenticate(String phone){
        return true;
    }
}
