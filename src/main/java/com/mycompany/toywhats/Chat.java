/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.toywhats;

import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

/**
 *
 * @author gabri
 */
public class Chat implements Serializable{
    private String phone1;
    private String phone2;
    private List<Message> messages = new ArrayList<>();
    
    public Chat(String newPhone1, String newPhone2){
        phone1 = newPhone1;
        phone2 = newPhone2;
    }
    
    public List<Message> getMessages(){
        return messages;
    }
    
    public void receiveMessage(Message message){
        messages.add(message);
    }
}
