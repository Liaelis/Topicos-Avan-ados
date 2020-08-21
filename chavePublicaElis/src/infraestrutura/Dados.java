package infraestrutura;

import java.time.LocalDate;

// Dados pré existentes
public class Dados {
    public static void main(String[] args) {

        try {
            LocalDate today = LocalDate.now();
//Certificado CA_RAIZ
            CertificadoPrivado certificado1 = new CertificadoPrivado(
                    "CA Raiz",
                    "236521536",
                    "ca-raiz@gmail",
                    "127.23.01",
                    CertificadoPublico.TipoCertificado.CA_RAIZ
            );
            certificado1.getCertificado().setValidade(today.plusYears(1)); //

            Autoridade caRaiz = new Autoridade(certificado1);
            certificado1.getCertificado().setAutoridadeCertificadora(caRaiz);
            Boolean resultRAIZ = CertificadoPrivado.saveCertificado(certificado1);
            //System.out.println("resultado salvar Certificado Raiz  " + resultRAIZ);
//==============================================================================================================
            //Certificado CA Valido
            CertificadoPrivado certificado2 = new CertificadoPrivado(
                    "CA",
                    "897123125",
                    "ca@email",
                    "127.166.002",
                    CertificadoPublico.TipoCertificado.CA
            );
            certificado2.getCertificado().setAutoridadeCertificadora(caRaiz);
            certificado2.getCertificado().setCertificadoPor(caRaiz.getCertificado());
            certificado2.getCertificado().setValidade(today.plusYears(1));
            caRaiz.assinaCertificado(certificado2.getCertificado());
            Autoridade ca = new Autoridade(certificado2);
            Boolean resultCA = CertificadoPrivado.saveCertificado(certificado2);
            System.out.println("resultado save CA  " + resultCA);
            System.out.print("Cpf/Cnpj do certificado recuperado:" + CertificadoPrivado.findCertificado("CA").getCertificado().getCpfCnpj());
//============================================================================================================
            //certificado CA data de validade vencida
            CertificadoPrivado certificado3 = new CertificadoPrivado(
                    "CA2",
                    "435566756",
                    "ca2@gmail.com",
                    "234.321.213.32",
                    CertificadoPublico.TipoCertificado.CA
            );
            certificado3.getCertificado().setAutoridadeCertificadora(caRaiz);
            certificado3.getCertificado().setCertificadoPor(caRaiz.getCertificado());
            certificado3.getCertificado().setValidade(today.minusYears(1));
            caRaiz.assinaCertificado(certificado3.getCertificado());
            Autoridade ca2= new Autoridade(certificado3);
            Boolean resul = CertificadoPrivado.saveCertificado(certificado3);
            //=======================================================================================================
//Certificado usuario final
            CertificadoPrivado certificado4 = new CertificadoPrivado(
                    "Elis",
                    "123435435",
                    "elis@gmail.com",
                    "123.212.12.3",
                    CertificadoPublico.TipoCertificado.USUARIO_FINAL

            );
            certificado4.getCertificado().setAutoridadeCertificadora(ca2);
            certificado4.getCertificado().setCertificadoPor(ca2.getCertificado());
            certificado4.getCertificado().setValidade(today.plusMonths(3));
            ca2.assinaCertificado(certificado4.getCertificado());
            Boolean resultado = CertificadoPrivado.saveCertificado(certificado4);
//Certificado usuario final
            CertificadoPrivado certificado8 = new CertificadoPrivado(
                    "Raul",
                    "123435435",
                    "elis@gmail.com",
                    "123.212.12.3",
                    CertificadoPublico.TipoCertificado.USUARIO_FINAL

            );
            certificado8.getCertificado().setAutoridadeCertificadora(ca);
            certificado8.getCertificado().setCertificadoPor(ca.getCertificado());
            certificado8.getCertificado().setValidade(today.plusMonths(3));
            ca.assinaCertificado(certificado8.getCertificado());
            Boolean res = CertificadoPrivado.saveCertificado(certificado8);
            //==================================================================================
            //CA_RAIZ com validade vencida
            CertificadoPrivado certificado5 = new CertificadoPrivado(
                    "CA_Raiz2",
                    "236521536",
                    "ca-raiz@gmail",
                    "127.23.01",
                    CertificadoPublico.TipoCertificado.CA_RAIZ
            );
            certificado5.getCertificado().setValidade(today.minusYears(1));

            Autoridade caRaiz2 = new Autoridade(certificado5);
            certificado5.getCertificado().setAutoridadeCertificadora(caRaiz2);
            boolean bol = CertificadoPrivado.saveCertificado(certificado5);

            //==========================================================================================

      //Criação de documentos
            DocumentoAssinado documento1 = certificado2.assinaDocumento("teste1".getBytes(), "documento1", "text/plain");
            DocumentoAssinado.saveDocumento(documento1);
            DocumentoAssinado documento2 = certificado4.assinaDocumento("teste2".getBytes(),"documento 2","text/plain");
            DocumentoAssinado.saveDocumento(documento2);
            DocumentoAssinado documento3 = certificado8.assinaDocumento("teste32".getBytes(), "documento3", "text/plain");
            DocumentoAssinado.saveDocumento(documento3);
            System.out.println("\ndocumento 1 "+ documento1.verificaAutenticidade());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
