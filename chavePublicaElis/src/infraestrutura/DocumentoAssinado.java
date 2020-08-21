package infraestrutura;

import javax.crypto.Cipher;
import java.io.*;
import java.util.Arrays;

public class DocumentoAssinado implements Assinavel {
    private byte[] dadosDocumento;
    private String mimeType;
    private String nomeDocumento;
    private byte[] assinatura;
    private CertificadoPublico certificado;

    public boolean verificaAutenticidade() {
        if(this.assinatura != null){
            System.out.println("\nveficando autenticidade documento " + this.nomeDocumento);
            if (verificaAssinaturaDocumento(this) && certificado.verifica()) {
                System.out.println("certificado  e doocumento valido");
                return true;
            } else {
                System.out.println("certificado do documento invalido");
                return false;
            }
        }else{
            System.out.println("Documento não esta assinado");
            return false;
        }
    }

    public boolean verificaAssinaturaDocumento(DocumentoAssinado documentoAssinado) {
        try {
            Util util = new Util();
            byte[] hash = util.getHash(documentoAssinado);
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, documentoAssinado.certificado.getChavePublica());
            byte[] assinaturaDescriptograda = cipher.doFinal(documentoAssinado.getAssinatura());
            return Arrays.equals(hash, assinaturaDescriptograda);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static DocumentoAssinado findDocumento(String nomeArquivo) {
        try {

            FileInputStream fileInputStream = new FileInputStream(nomeArquivo + ".txt");
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            DocumentoAssinado doc = (DocumentoAssinado) objectInputStream.readObject();
            fileInputStream.close();
            objectInputStream.close();
            return doc;

        } catch (FileNotFoundException e) {

            return null;


        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean saveDocumento(DocumentoAssinado documentoAssinado) {
        try {
            FileOutputStream file = new FileOutputStream(documentoAssinado.getNomeDocumento() + ".txt");
            ObjectOutputStream obou = new ObjectOutputStream(file);
            obou.writeObject(documentoAssinado);
            obou.close();
            file.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public byte[] getDadosDocumento() {
        return dadosDocumento;
    }

    public void setDadosDocumento(byte[] dadosDocumento) {
        this.dadosDocumento = dadosDocumento;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getNomeDocumento() {
        return nomeDocumento;
    }

    public void setNomeDocumento(String nomeDocumento) {
        this.nomeDocumento = nomeDocumento;
    }

    @Override
    public byte[] getAssinatura() {
        return assinatura;
    }

    @Override
    public void setAssinatura(byte[] assinatura) {
        this.assinatura = assinatura;
    }

    public CertificadoPublico getCertificado() {
        return certificado;
    }

    public void setCertificado(CertificadoPublico certificado) {
        this.certificado = certificado;
    }

    @Override
    public CertificadoPublico getCertificadoPor() {
        return this.certificado.getCertificadoPor();
    }

    @Override
    public void setCertificadoPor(CertificadoPublico certificado) {
        this.certificado.setCertificadoPor(certificado);
    }
}
