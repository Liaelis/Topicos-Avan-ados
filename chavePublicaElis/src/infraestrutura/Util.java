package infraestrutura;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Util implements Serializable {
    /**
     * Retorna o hash deste objeto, para ser usado na verificação de autenticidade
     * e geração de assinatura.
     *
     * @return byte array do hash deste certificado.
     */
    public synchronized byte[] getHash(Assinavel objeto) {
        byte[] certData;
        byte[] assinTemp = objeto.getAssinatura();
        CertificadoPublico certTemp = objeto.getCertificadoPor();
        objeto.setAssinatura(null);
        objeto.setCertificadoPor(null);
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oout = new ObjectOutputStream(bout);
            oout.writeObject(this);
            oout.close();
            bout.close();
            certData = bout.toByteArray();
            return MessageDigest.getInstance("SHA-256").digest(certData);
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        } finally {
            objeto.setAssinatura(assinTemp);
            objeto.setCertificadoPor(certTemp);
        }
    }
}
