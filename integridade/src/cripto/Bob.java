package cripto;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.*;
import java.util.Arrays;

public class Bob {
    public static void main(String[] args) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, ClassNotFoundException {

        // Abrindo Socket
        ServerSocket ss = new ServerSocket(3333);

        // Gera par de chaves
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(1024);
        KeyPair kp = keyGen.generateKeyPair();
        PrivateKey privateKeyB = kp.getPrivate();
        PublicKey publicKeyB = kp.getPublic();

        // Aguarda Conexão
        while (true) {
            System.out.println("3. Aguardando conexões...");
            Socket s = ss.accept();
            System.out.println(" Cliente conectado.");

            // Enviar chave pública
            ObjetoTroca obj = new ObjetoTroca();
            obj.setChavePublica(publicKeyB);
            ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
            out.writeObject(obj);
            out.flush();
            System.out.println("Chave publica enviada.");

            //Recebe dados de Alice
            ObjectInputStream in = new ObjectInputStream(s.getInputStream());
            ObjetoTroca objetoTroca = (ObjetoTroca) in.readObject();

            // Descriptografar hash com chave publica
            Cipher cipherHASH = Cipher.getInstance("RSA");
            cipherHASH.init(Cipher.DECRYPT_MODE,objetoTroca.getChavePublica());
            byte[] arquivoHashAlice = cipherHASH.doFinal(objetoTroca.getMac());


            //Desifrar chave da sessao usando chave privada
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKeyB);
            byte[] c_sessao= cipher.doFinal(objetoTroca.getChaveSessao());
            SecretKeySpec ks = new SecretKeySpec(c_sessao,"AES");

            // Descriptografar arquivo com chave de sessão do Alice
            Cipher cipher_arquivo = Cipher.getInstance("AES");
            cipher_arquivo.init(Cipher.DECRYPT_MODE,ks);
            byte[] b_arquivo = cipher_arquivo.doFinal(objetoTroca.getArquivo());


            // Criar hash do Arquivo recebido de alic
            MessageDigest md = null;
            md = MessageDigest.getInstance("SHA-1");
            byte[] arquivoHash = md.digest(b_arquivo);

            //Compara os hash verificar integridade
            System.out.println("Comparando Hashs.");
            if(Arrays.equals(arquivoHash,arquivoHashAlice))
            {
                System.out.println(" Hashs Iguais.");
            }
            else {
                System.out.println("ERRO! Hashs Invalida.");
            }
            //Fechar conexão
            s.close();


        }
    }
}