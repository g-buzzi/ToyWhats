/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.toywhats;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author gabri
 */
public class Message implements Serializable{
    private byte[] message;
    private String nonce;
    private String sender;
    
    public Message(byte[] newMessage, String newNonce, String newSender){
        message = newMessage;
        nonce = newNonce;
        sender = newSender;
    }
    
    public byte[] getMessage(){
        return message;
    }
    
    public String getNonce(){
        return nonce;
    }
    
    public String getSender(){
        return sender;
    }
    
}
