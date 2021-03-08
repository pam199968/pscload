package fr.ans.psc.pscload.component.utils;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.springframework.beans.factory.annotation.Value;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.*;
import java.net.URL;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SSLUtils {

    private static final char[] PSC_LOAD_PASS = "pscloadpass".toCharArray();

    SSLUtils() {}

    public static void initSSLContext(String certFile, String keyFile, String caCertFile) throws NoSuchAlgorithmException, UnrecoverableKeyException, KeyStoreException,
            KeyManagementException, IOException, InvalidKeySpecException, CertificateException {
        KeyStore keyStore = keyStoreFromPEM(certFile, keyFile);
        KeyManagerFactory keyManagerFactory =
                KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, PSC_LOAD_PASS);

        KeyStore trustStore = trustStoreFromPEM(caCertFile);
        TrustManagerFactory trustManagerFactory =
                TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(trustStore);

        final SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
        sslContext.init(keyManagerFactory.getKeyManagers(),
                trustManagerFactory.getTrustManagers(),
                new SecureRandom());
        SSLContext.setDefault(sslContext);
    }


    private static KeyStore keyStoreFromPEM(String certFile, String keyFile) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, KeyStoreException, CertificateException {
        String alias="psc";

        // Private Key
        PemReader keyReader = new PemReader(new FileReader(keyFile));
        PemObject keyObject = keyReader.readPemObject();

        PrivateKeyInfo pkInfo = PrivateKeyInfo.getInstance(keyObject.getContent());
        PKCS8EncodedKeySpec pkSpec = new PKCS8EncodedKeySpec(pkInfo.getEncoded());
        PrivateKey key = KeyFactory.getInstance("RSA").generatePrivate(pkSpec);
        keyReader.close();

        // Certificate
        PemReader certReader = new PemReader(new FileReader(certFile));
        PemObject certObject = certReader.readPemObject();

        List<X509Certificate> certs = new ArrayList<>();
        X509CertificateHolder certHolder = new X509CertificateHolder(certObject.getContent());
        certs.add(new JcaX509CertificateConverter().setProvider(new BouncyCastleProvider()).getCertificate(certHolder));
        certReader.close();

        // Keystore
        KeyStore ks = KeyStore.getInstance("PKCS12");
        ks.load(null);

        for (int i = 0; i < certs.size(); i++) {
            ks.setCertificateEntry(alias + "_" + i, certs.get(i));
        }

        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(null);
        keyStore.setKeyEntry(alias, key, PSC_LOAD_PASS, certs.toArray(new X509Certificate[certs.size()]));

        return keyStore;
    }

    private static KeyStore trustStoreFromPEM(String caCertFile) throws IOException, CertificateException, NoSuchAlgorithmException, KeyStoreException {

        PemReader caReader = new PemReader(new FileReader(caCertFile));
        PEMParser caParser = new PEMParser(caReader);

        KeyStore trustStore = KeyStore.getInstance("JKS");
        trustStore.load(null);

        int index = 1;
        Object pemCert;

        while ((pemCert = caParser.readObject()) != null) {
            X509Certificate caCert = new JcaX509CertificateConverter()
                    .setProvider(new BouncyCastleProvider())
                    .getCertificate((X509CertificateHolder) pemCert);
            trustStore.setCertificateEntry("ca-" + index, caCert);
            index++;
        }

        return trustStore;
    }

    /**
     * Downloads a file from a URL
     * @param fileURL HTTP URL of the file to be downloaded
     * @throws IOException IO Exception
     */
    public static boolean downloadFile(String fileURL, String saveDirectory) throws IOException {
        URL url = new URL(fileURL);
        HttpsURLConnection httpConn = (HttpsURLConnection) url.openConnection();
        int responseCode = httpConn.getResponseCode();

        // always check HTTP response code first
        if (responseCode == HttpsURLConnection.HTTP_OK) {
            String fileName = "";
            String disposition = httpConn.getHeaderField("Content-Disposition");
            String contentType = httpConn.getContentType();
            int contentLength = httpConn.getContentLength();

            if (disposition != null) {
                // extracts file name from header field
                int index = disposition.indexOf("filename=");
                if (index > 0) {
                    fileName = disposition.substring(disposition.lastIndexOf("=") + 1);
                }
            } else {
                // extracts file name from URL
                fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1);
            }

            System.out.println("Content-Type = " + contentType);
            System.out.println("Content-Disposition = " + disposition);
            System.out.println("Content-Length = " + contentLength);
            System.out.println("fileName = " + fileName);

            // Check if zip already exists before download
            File[] existingFiles = new File(saveDirectory).listFiles();
            String finalFileName = fileName;
            if (existingFiles != null && Arrays.stream(existingFiles).anyMatch(f -> finalFileName.equals(f.getName()))) {
                System.out.println("File already downloaded");
                httpConn.disconnect();
                return false;
            }

            // opens input stream from the HTTP connection
            InputStream inputStream = httpConn.getInputStream();
            String zipFile = saveDirectory + File.separator + fileName;

            // opens an output stream to save into file
            FileOutputStream outputStream = new FileOutputStream(zipFile);

            int bytesRead;
            byte[] buffer = new byte[4096];
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            System.out.println("File downloaded");

            outputStream.close();
            inputStream.close();

            if (FilesUtils.unzip(zipFile)) {
                httpConn.disconnect();
                return true;
            }
        }
        System.out.println("No files to download. Server replied with HTTP code: " + responseCode);
        httpConn.disconnect();
        return false;
    }
}
