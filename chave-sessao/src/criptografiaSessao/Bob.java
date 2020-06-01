package criptografiaSessao;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.*;

public class Bob {

    public static void main(String[] args) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, ClassNotFoundException, IOException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException {

       // Abrindo Socket
        ServerSocket ss = new ServerSocket(3333);
        System.out.println("1. Socket aberto");

       // Gera par chaves
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(1024);
        KeyPair kp = keyGen.generateKeyPair();
        PrivateKey privateKey = kp.getPrivate();
        PublicKey publicKey = kp.getPublic();
        System.out.println(" Par de chaves gerado.");

        while (true) {
            System.out.println("3. Aguardando conexões...");
            Socket s = ss.accept();
            System.out.println("    3.1 Cliente conectado.");

            // Enviando a chave publica
            ObjetoTroca obj = new ObjetoTroca();
            obj.setChavePublica(publicKey);
            ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
            out.writeObject(obj);
            out.flush();

            // arquivo recebido do socket
            ObjectInputStream in = new ObjectInputStream(s.getInputStream());
            ObjetoTroca objetoTroca = (ObjetoTroca) in.readObject();
            System.out.println("arquivo recebido -> "+objetoTroca.getArquivo());

            // Criar o desencriptador
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);

            //Desifrar chave da sessao usando chave privada
            byte[] chave_sessao= cipher.doFinal(objetoTroca.getChaveSessao());
            SecretKeySpec chaveSecretaAES = new SecretKeySpec(chave_sessao,"AES");

            //Criar o desencriptador
            Cipher cipherAES = Cipher.getInstance("AES");
            cipherAES.init(Cipher.DECRYPT_MODE,chaveSecretaAES);

            //Desifrar arquivo com chave de sessao
            byte[] b_arquivo = cipherAES.doFinal(objetoTroca.getArquivo());
            System.out.println(" MENSAGEM ----> "+ new String(b_arquivo));

            // Fechar conexão
            System.out.println("8. Conexão fechada.");
            s.close();

        }

    }
}
