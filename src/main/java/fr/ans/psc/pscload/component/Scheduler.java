package fr.ans.psc.pscload.component;

import fr.ans.psc.pscload.component.utils.FilesUtils;
import fr.ans.psc.pscload.component.utils.SSLUtils;
import fr.ans.psc.pscload.model.mapper.ProfessionnelMapper;
import fr.ans.psc.pscload.service.PscRestApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;

/**
 * The type Scheduler.
 */
@Component
public class Scheduler {

    @Autowired
    private final PscRestApi pscRestApi;

    @Value("${cert.path}")
    private String cert;

    @Value("${key.path}")
    private String key;

    @Value("${ca.path}")
    private String ca;

    @Value("${files.directory}")
    private String filesDirectory;

    public Scheduler(PscRestApi pscRestApi) {
        this.pscRestApi = pscRestApi;
    }

    /**
     * Download and parse.
     */
    @Scheduled(fixedRate = 3600000)
    public void downloadAndParse() throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException,
            KeyManagementException, InvalidKeySpecException, IOException, CertificateException {

        SSLUtils.initSSLContext(cert, key, ca);
        String zipFile = SSLUtils.downloadFile("https://service.annuaire.sante.fr/annuaire-sante-webservices/V300/services/extraction/Extraction_ProSanteConnect", filesDirectory);

        if (zipFile != null && FilesUtils.unzip(zipFile)) {
            diffOrLoad();
            FilesUtils.cleanup(filesDirectory);
        }
    }

    private void diffOrLoad() throws IOException {
        Map<String, File> latestFiles = FilesUtils.getLatestExtAndSer(filesDirectory);

        File ogFile = latestFiles.get("ser");
        File newFile = latestFiles.get("ext");

        if (ogFile == null && newFile != null) {
            pscRestApi.uploadPsMap(ProfessionnelMapper.getPsMapFromFile(newFile));
        } else if (ogFile != null && newFile != null) {
            pscRestApi.diffPsMaps(ProfessionnelMapper.getPsMapFromFile(ogFile),
                    ProfessionnelMapper.getPsMapFromFile(newFile));
        }
    }

}
