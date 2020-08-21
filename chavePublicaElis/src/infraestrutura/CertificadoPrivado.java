package infraestrutura;

import javax.crypto.Cipher;
import java.io.*;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;

public class CertificadoPrivado implements Serializable {

    private final PrivateKey chavePrivada;
    private final CertificadoPublico certificado;

    public CertificadoPrivado(String nome,
                              String cpfCnpj,
                              String email,
                              String hostname,
                              CertificadoPublico.TipoCertificado tipoCertificado) throws NoSuchAlgorithmException {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(1024);
        KeyPair kp = kpg.generateKeyPair();
        this.certificado = new CertificadoPublico(kp.getPublic(), nome, cpfCnpj, email, hostname, tipoCertificado);
        this.chavePrivada = kp.getPrivate();
    }

    /**
     * Cria um documento assinado com base nos dados recebidos.
     *
     * @param dados       dados do documento
     * @param nomeArquivo nome do arquivo deste documento
     * @param mimeType    tipificação do arquivo (RFC 2046)
     * @return o documento assinado
     */
    public DocumentoAssinado assinaDocumento(byte[] dados, String nomeArquivo, String mimeType) {

        if(this.certificado.getAssinatura()!= null){
            try {
                Util util = new Util();
                DocumentoAssinado documentoAssinado = new DocumentoAssinado();
                documentoAssinado.setDadosDocumento(dados);
                documentoAssinado.setMimeType(mimeType);
                documentoAssinado.setNomeDocumento(nomeArquivo);

                documentoAssinado.setCertificado(this.certificado);
                Cipher cipher = Cipher.getInstance("RSA");
                cipher.init(Cipher.ENCRYPT_MODE, this.chavePrivada);
                byte[] assCript = cipher.doFinal(util.getHash(documentoAssinado));

                documentoAssinado.setAssinatura(assCript);

                return documentoAssinado;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        System.out.println("Documento não foi assinado - Certificado tem assinatura invalida");
        return null;
    }
    public static Boolean saveCertificado(CertificadoPrivado certificadoPrivado) {
        try {
            FileOutputStream file = new FileOutputStream(certificadoPrivado.getCertificado().getNome()+".txt");
            ObjectOutputStream obou = new ObjectOutputStream(file);
            obou.writeObject(certificadoPrivado);
            obou.close();
            file.close();
            System.out.println("Certificado salvo com sucesso");
            return true;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    public static CertificadoPrivado findCertificado(String nomeArquivo) {
        try {
            FileInputStream fileInput = new FileInputStream(nomeArquivo + ".txt");
            ObjectInputStream oinput = new ObjectInputStream(fileInput);
            CertificadoPrivado certificado = (CertificadoPrivado) oinput.readObject();
            oinput.close();
            fileInput.close();
            return certificado;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public CertificadoPublico getCertificado() {
        return this.certificado;
    }

    public PrivateKey getChavePrivada() {

        return chavePrivada;
    }

}
