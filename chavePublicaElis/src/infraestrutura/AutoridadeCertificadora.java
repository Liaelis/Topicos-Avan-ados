package infraestrutura;

import java.io.Serializable;

public interface AutoridadeCertificadora extends Serializable {
    /**
     * Assina o certificado recebido por parâmetro.
     *
     * @param certificado
     */
    void assinaCertificado(CertificadoPublico certificado);

    /**
     * Verifica se o certificado recebido por parâmetro está revogado.
     *
     * @param certificado
     * @return true se estiver revogado, false caso contrário.
     */
    boolean estaRevogado(CertificadoPublico certificado);

    /**
     * Revoga o certificado recebido por parâmetro.
     *
     * @param certificado
     */
    void revogaCertificado(CertificadoPublico certificado);

    /**
     * Permite obter o certificado desta autoridade.
     *
     * @return
     */
    CertificadoPublico getCertificado();

    boolean verificaAssinaturaCertificado(CertificadoPublico certificado);
}
