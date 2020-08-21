package infraestrutura;

import javax.crypto.Cipher;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Autoridade implements AutoridadeCertificadora{

    private CertificadoPrivado certificadoPrivado;
    private List<CertificadoPublico> certificadosRevogados = new ArrayList<CertificadoPublico>();


    public Autoridade(CertificadoPrivado certificadoPrivado) {
        this.certificadoPrivado = certificadoPrivado;
    }


    public void assinaCertificado(CertificadoPublico certificado) {
        //testa hierarquia
        if(this.getCertificado().getTipoCertificado()== CertificadoPublico.TipoCertificado.CA_RAIZ
                ||this.getCertificado().getTipoCertificado()== CertificadoPublico.TipoCertificado.CA
                && certificado.getTipoCertificado()== CertificadoPublico.TipoCertificado.USUARIO_FINAL){
            try {
                Util util = new Util();
                byte[] hash = util.getHash(certificado);
                Cipher cipher = Cipher.getInstance("RSA");
                cipher.init(Cipher.ENCRYPT_MODE, this.certificadoPrivado.getChavePrivada());
                byte[] chaveCript = cipher.doFinal(hash);
                certificado.setAssinatura(chaveCript);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            System.out.println("certificado não pode ser assinado - incoerencia de autoridade");
        }

    }


    public boolean verificaAssinaturaCertificado(CertificadoPublico certificado) {
        if(certificado.getAssinatura()!=null){
            try {
                Util util = new Util();
                byte[] hash = util.getHash(certificado);
                Cipher cipher = Cipher.getInstance("RSA");
                cipher.init(Cipher.DECRYPT_MODE, certificado.getCertificadoPor().getChavePublica());
                byte[] assinaturaDecript = cipher.doFinal(certificado.getAssinatura());
                boolean result = Arrays.equals(hash, assinaturaDecript);
                System.out.println("\n verifica assinatura certificado  "+result);
                return result;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("\nDOcumento não esta assinado");
        return false;
    }

    public boolean estaRevogado(CertificadoPublico certificado) {
        return this.certificadosRevogados.contains(certificado);
    }

    public void revogaCertificado(CertificadoPublico certificado) {

        this.certificadosRevogados.add(certificado);
    }


    public CertificadoPublico getCertificado() {

        return this.certificadoPrivado.getCertificado();
    }
}
