package infraestrutura.teste;

import infraestrutura.Autoridade;
import infraestrutura.CertificadoPrivado;
import infraestrutura.CertificadoPublico;
import infraestrutura.DocumentoAssinado;

import javax.sound.midi.Soundbank;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;

/*Implementar:
        - a assinatura de documentos (classe DocumentoAssinado);
       - a autenticidade de documentos (classe DocumentoAssinado);
        - a autenticação de Certificados (classe Certificado);
        - a autenticidade de Certificados (classe Certificado);
        - a persistência e recuperação de Certificados Completos (classe CertificadoPrivado);
      - os serviços da interface AutoridadeCertificadora;
       - casos de teste para todos os requisitos anteriores;*/
public class Teste {
    //Arquivos préviamente criados pela Classe Dados
    /*
        CA - certificado tipo CA valido
        CA Raiz - certificado tipo raiz valido
        CA2 - certificado tipo CA invalido
        CA_Raiz2 - certificado raiz invalido
        Elis - certificado usuario_final invalido
        Raul - certificado usuario_final valido
        documento1-valido
        documento2-invalido
        documento3- valido
     */

    public static void main(String[] args) throws NoSuchAlgorithmException {
        try {


            LocalDate today = LocalDate.now();

//===========Recuperação de Certificados ===========

            CertificadoPrivado certP = CertificadoPrivado.findCertificado("CA");
            System.out.println("\n ChavePrivada " + certP.getChavePrivada());
            System.out.println("\n nome " + certP.getCertificado().getNome());
            System.out.println("\n cpfcnpj " + certP.getCertificado().getCpfCnpj());
            System.out.println("\n Email " + certP.getCertificado().getEmail());
            System.out.println("\n hostname " + certP.getCertificado().getHostname());
            System.out.println("\n Validade " + certP.getCertificado().getValidade());
            System.out.println("\n Tipo " + certP.getCertificado().getTipoCertificado());
            if(certP.getCertificado().getTipoCertificado() != CertificadoPublico.TipoCertificado.CA_RAIZ){
                System.out.println("\n CertificadoPor " + certP.getCertificado().getCertificadoPor().getNome());
                System.out.println("\n Assinatura certificado "+certP.getCertificado().getAssinatura());
            }



            //=========Assinar Certificado===============

            CertificadoPrivado certificadoTeste = new CertificadoPrivado(
                    "Lilith",
                    "12345678",
                    "Lilith@gmail.com",
                    "123.212.12.3",
                    CertificadoPublico.TipoCertificado.USUARIO_FINAL
            );
            Autoridade auto = new Autoridade(certP);
            certificadoTeste.getCertificado().setAutoridadeCertificadora(auto);
            certificadoTeste.getCertificado().setCertificadoPor(auto.getCertificado());
            certificadoTeste.getCertificado().setValidade(today.plusMonths(3));
            auto.assinaCertificado(certificadoTeste.getCertificado());
            boolean status = auto.verificaAssinaturaCertificado(certificadoTeste.getCertificado());
            System.out.println("\ncertificado está assinado: " + status);
            // Caso a não siga a hierarquia, não será realizada a assinatura
            boolean retornoSave = CertificadoPrivado.saveCertificado(certificadoTeste);
            System.out.println(retornoSave);

//================Verifica Autenticidade Certificado==============

            boolean result = auto.getCertificado().verifica(); // metodo checa assinatura e hieraquia
            System.out.println("\n Resultado Autenticidade Certificado Recuperado " + result);

            //======================Assinatura Documento e Verifica=======================
            System.out.println("\n Assinar documento");
            DocumentoAssinado documentoTeste = certificadoTeste.assinaDocumento("Teste Teste Teste".getBytes(), "documentoTeste", "text/plain");
            if (documentoTeste != null) {
                System.out.println("documento Assinado");
                boolean resultado = documentoTeste.verificaAutenticidade();
                System.out.println("\n Resultado Documento autentico " + resultado);
            } else {
                System.out.println("Documento não foi assinado");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
