package infraestrutura;

import java.io.Serializable;

public interface Assinavel extends Serializable {
    byte[] getAssinatura();

    void setAssinatura(byte[] assinatura);

    CertificadoPublico getCertificadoPor();

    void setCertificadoPor(CertificadoPublico certificado);
}
