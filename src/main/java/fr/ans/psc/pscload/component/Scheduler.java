package fr.ans.psc.pscload.component;

import fr.ans.psc.pscload.component.utils.FilesUtils;
import fr.ans.psc.pscload.component.utils.SSLUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;

/**
 * The type Scheduler.
 */
@Component
public class Scheduler {

    @Value("${cert.path}")
    private String cert;

    @Value("${key.path}")
    private String key;

    @Value("${ca.path}")
    private String ca;

    @Value("${files.directory}")
    private String filesDirectory;

    /**
     * Download and parse.
     *
     */
    @Scheduled(fixedRate = 5000)
    public void downloadAndParse() throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException,
            KeyManagementException, InvalidKeySpecException, IOException, CertificateException {

        SSLUtils.initSSLContext(cert, key, ca);
        boolean goAhead = SSLUtils.downloadFile("https://service.annuaire.sante.fr/annuaire-sante-webservices/V300/services/extraction/Extraction_ProSanteConnect", filesDirectory);

        if (goAhead) {
            FilesUtils.cleanup(filesDirectory);
        }
    }

}
