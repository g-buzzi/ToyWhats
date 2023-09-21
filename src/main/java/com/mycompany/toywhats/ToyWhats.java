/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.toywhats;
import java.util.List;
import java.util.Scanner;
import javax.crypto.SecretKey;
/**
 *
 * @author gabri
 */
public class ToyWhats {
    User currentUser;
    Scanner entrada;
    Server server;

    public static void main(String[] args) {
        ToyWhats app = new ToyWhats();
        app.start();
    }
    
    public ToyWhats() {
        server = new Server();
        entrada = new Scanner(System.in);
    }
    
    public void start(){
       boolean loop = true;
       while(loop){
           System.out.println("================= ToyWhats ===============\n");
           System.out.println("Escolha uma opção:\n\n0: Login\n1: Registrar\n2: Voltar");
           String opcao = entrada.nextLine();
           switch(opcao){
               case "0":
                   login();
                   break;
               case "1":
                   register();
                   break;
               case "2":
                   loop = false;
                   break;
               default:
                   System.out.println("Comando não reconhecido");
           }
       }
    }
    
    private void login(){
        boolean loop = true;
        while(loop){
            System.out.println("================= Login ===============\n");
            System.out.println("Digite o nome do usuário: ");
            String login = entrada.nextLine();
            if(login.equals("")){
                break;
            }
            System.out.println("Digite a senha: ");
            String password = entrada.nextLine();
            if(password.equals("")){
                break;
            }
            
            byte[] salt = server.getSalt(login);
            if(salt == null){
                System.out.println("Senha ou usuário incorreto");
            }
            else{
                // Fazer Scrypt da senha
                byte[] passwordScrypt = Crypto.computeSCRYPT(password, salt);

                currentUser = server.login(login, passwordScrypt);
                if(currentUser != null){
                    loop = false;
                    mainMenu();
                }
            }
        }
    }
    
    private void register(){
        while(true){
            System.out.println("================= Register ===============\n");
            System.out.println("Digite o nome do usuário: ");
            String login = entrada.nextLine();
            if(login.equals("")){
                break;
            }
            System.out.println("Digite a senha: ");
            String password = entrada.nextLine();
            if(password.equals("")){
                break;
            }
            
            byte[] salt = Crypto.generateSalt();
            byte[] passwordScrypt = Crypto.computeSCRYPT(password, salt);
            
            // Fazer Scrypt da senha
            
            System.out.println("Digite o número do telefone: ");
            String phone = entrada.nextLine();
            
            if(phone.equals("")){
                break;
            }
            
            currentUser = server.register(login, passwordScrypt, phone, salt);
            if(currentUser == null){
                System.out.println("\nLogin ou número de telefone já cadastrados!");
            }
            else{
                mainMenu();
                break;
            }
        }
    }
    
    private void mainMenu(){
       while(true){
           System.out.println("================= Menu ===============\n");
           User[] contacts = server.getContacts(currentUser.getPhone());
            for(int i = 0; i < contacts.length; i++){
                System.out.println(String.format("%s: %s",contacts[i].getPhone(),contacts[i].getLogin()));  
            }
           
           System.out.println("Digite Enter para sair ou entre um número de contato para ver suas mensagens.");
           String opcao = entrada.nextLine();
           if(opcao.equals("")){
               break;
           }
           contactMenu(opcao);
       }
    }
    
    private void contactMenu(String phone){
        while(true){
            User sender = server.getUserByPhone(phone);
            if(sender == null){
                System.out.println("Usuário não encontrado.");
                return;
            }
            System.out.println(String.format("================= %s ===============\n", sender.getLogin()));
            List<Message> messages = server.getMessages(phone, currentUser.getPhone());

            SecretKey receiverKey = Crypto.generateKey(currentUser.getPhone(), currentUser.getSalt());
            SecretKey senderKey = Crypto.generateKey(phone, sender.getSalt());

            for (int i = 0; messages != null && i < messages.size(); i ++) {
                if(messages.get(i).getSender().equals(phone)){
                    System.out.println(String.format("================= %s", sender.getLogin()));
                    System.out.println(Utils.toString(messages.get(i).getMessage()));
                    System.out.println(Crypto.decryptMessage(receiverKey, messages.get(i)));
                }
                else{                
                    System.out.println(String.format("%s =================", currentUser.getLogin()));
                    System.out.println(Utils.toString(messages.get(i).getMessage()));
                    System.out.println(Crypto.decryptMessage(senderKey, messages.get(i)));
                }
                System.out.println();
            }
            
            System.out.println("\nEscolha uma opção:\n\n0: Enviar mensagem\n1: Voltar");
            String opcao = entrada.nextLine();
            
            if(opcao.equals("0")){
                sendMessage(phone);
            }
            else{
                break;
            }
        }
    }
    
    private void sendMessage(String phone){
        System.out.println("Digite sua mensagem: ");
        String text = entrada.nextLine();
        if(!text.equals("")){
            User user = server.getUserByPhone(phone);

            SecretKey key = Crypto.generateKey(user.getPhone(), user.getSalt());
            String iv = Crypto.generateNonce();
            byte[] encryptedText = Crypto.encryptMessage(key, text, iv);
            Message message = new Message(encryptedText, iv, currentUser.getPhone());

            server.sendMessage(currentUser.getPhone(), phone, message);
            System.out.println("\nMensagem enviada com sucesso!");
        }
    }
}