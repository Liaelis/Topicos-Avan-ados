package cripto;

import javax.crypto.*;
import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.security.*;

public class Alice {
    public static void main(String[] args) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, ClassNotFoundException {

        // Selecionar o arquivo
        JFileChooser chooserArquivo = new JFileChooser();
        int escolha = chooserArquivo.showOpenDialog(new JFrame());
        if (escolha != JFileChooser.APPROVE_OPTION) {
            return;
        }


        // Ler o arquivo
        File arquivo = new File(chooserArquivo.getSelectedFile().getAbsolutePath());
        FileInputStream fin = new FileInputStream(arquivo);
        byte[] barquivo = new byte[(int) fin.getChannel().size()];
        fin.read(barquivo);


        // Gera par chaves
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(1024);
        KeyPair kp = keyGen.generateKeyPair();
        PrivateKey privateKeyA = kp.getPrivate();
        PublicKey publicKeyA = kp.getPublic();

        // Criar hash do Arquivo
        MessageDigest md = null;
        md = MessageDigest.getInstance("SHA-1");
        byte[] arquivoHash = md.digest(barquivo);


        // Conectar à bob
        Socket s = new Socket("localhost", 3333);


        // Receber a chave pública Bob
        ObjectInputStream in = new ObjectInputStream(s.getInputStream());
        ObjetoTroca obj = (ObjetoTroca) in.readObject();


        //Criar o encriptador e criptografar o hash com chave privada
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, privateKeyA);
        byte[] assinatura_cripto = cipher.doFinal(arquivoHash);


        //Cria chave de sessão para arquivo
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        kgen.init(128);
        SecretKey aeskey = kgen.generateKey();
        byte[] chave_sessao = aeskey.getEncoded();


        // Criar o encriptador e criptografar arquivo com chave de sessão
        Cipher cipher_arquivo = Cipher.getInstance("AES");
        cipher_arquivo.init(Cipher.ENCRYPT_MODE, aeskey);
        byte[] arquivo_cripto = cipher_arquivo.doFinal(barquivo);


        // Criar o encriptador e criptografar ch sessao com ch publica bob
        Cipher cipher_sessao = Cipher.getInstance("RSA");
        cipher_sessao.init(Cipher.ENCRYPT_MODE, obj.getChavePublica());
        byte[] sessao_cripto = cipher_sessao.doFinal(aeskey.getEncoded());



        // Envia para Bob(Arquivo,ch sessão,hash(TodosCripto)e chave publica

        ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
        obj = new ObjetoTroca();
        obj.setChaveSessao(sessao_cripto);
        obj.setArquivo(arquivo_cripto);
        obj.setChavePublica(publicKeyA);
        obj.setMac(assinatura_cripto);
        obj.setNomeArquivo(chooserArquivo.getSelectedFile().getName());
        out.writeObject(obj);
        out.close();

        // Fecha Conexão
        s.close();
        System.out.println("Envio concluído, conexão fechada!");

    }

}
