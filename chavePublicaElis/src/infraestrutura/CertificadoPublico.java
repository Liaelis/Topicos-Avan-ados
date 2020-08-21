package infraestrutura;

import java.security.PublicKey;
import java.time.LocalDate;

public class CertificadoPublico implements Assinavel {
    public enum TipoCertificado {USUARIO_FINAL, CA, CA_RAIZ}

    private PublicKey chavePublica;
    private String nome;
    private String cpfCnpj;
    private String email;
    private String hostname;
    private LocalDate validade;
    private TipoCertificado tipoCertificado;
    private CertificadoPublico certificadoPor;
    private byte[] assinatura;
    private AutoridadeCertificadora autoridadeCertificadora;

    /**
     * Constrói um certificado a partir dos dados passados por parâmetro.
     *
     * @param chavePublica
     * @param nome
     * @param cpfCnpj
     * @param email
     * @param hostname
     * @param tipoCertificado
     */
    public CertificadoPublico(PublicKey chavePublica,
                              String nome,
                              String cpfCnpj,
                              String email,
                              String hostname,
                              TipoCertificado tipoCertificado) {
        this.chavePublica = chavePublica;
        this.nome = nome;
        this.cpfCnpj = cpfCnpj;
        this.email = email;
        this.hostname = hostname;
        this.tipoCertificado = tipoCertificado;
    }


    public boolean verifica() {
        if (this.getCertificadoPor() != null) {
            if (this.getValidade().isAfter(LocalDate.now())
                    && !autoridadeCertificadora.estaRevogado(this)
                    && autoridadeCertificadora.verificaAssinaturaCertificado(this)) {

                return this.getCertificadoPor().verifica();

            } else {
                System.out.println("Está Revogado");
                autoridadeCertificadora.revogaCertificado(this);
                return false;
            }
        } else {
            if (this.tipoCertificado == TipoCertificado.CA_RAIZ) {
                if (this.getValidade().isAfter(LocalDate.now())) {

                    return true;
                } else {
                    System.out.println("Certificado foi Revogado");
                    autoridadeCertificadora.revogaCertificado(this);
                    return false;
                }
            } else {
                System.out.println("erro de certificado");
                return false;
            }
        }

    }

    public PublicKey getChavePublica() {
        return chavePublica;
    }

    public void setChavePublica(PublicKey chavePublica) {
        this.chavePublica = chavePublica;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public LocalDate getValidade() {
        return validade;
    }

    public void setValidade(LocalDate validade) {
        this.validade = validade;
    }

    public TipoCertificado getTipoCertificado() {
        return tipoCertificado;
    }

    public void setTipoCertificado(TipoCertificado tipoCertificado) {
        this.tipoCertificado = tipoCertificado;
    }

    public CertificadoPublico getCertificadoPor() {
        return certificadoPor;
    }

    public void setCertificadoPor(CertificadoPublico certificadoPor) {
        this.certificadoPor = certificadoPor;
    }

    @Override
    public byte[] getAssinatura() {
        return assinatura;
    }

    @Override
    public void setAssinatura(byte[] assinatura) {
        this.assinatura = assinatura;
    }

    public AutoridadeCertificadora getAutoridadeCertificadora() {
        return autoridadeCertificadora;
    }

    public void setAutoridadeCertificadora(AutoridadeCertificadora autoridadeCertificadora) {
        this.autoridadeCertificadora = autoridadeCertificadora;
    }

}
