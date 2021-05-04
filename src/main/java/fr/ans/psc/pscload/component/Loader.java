package fr.ans.psc.pscload.component;

import fr.ans.psc.pscload.component.utils.FilesUtils;
import fr.ans.psc.pscload.component.utils.SSLUtils;
import fr.ans.psc.pscload.model.mapper.ProfessionnelMapper;
import fr.ans.psc.pscload.model.object.Professionnel;
import fr.ans.psc.pscload.service.PscRestApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;

@Component
public class Loader {

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

    @Value("${use.ssl}")
    private boolean useSSL;

    public Loader(PscRestApi pscRestApi) {
        this.pscRestApi = pscRestApi;
    }

    public void downloadAndParse(String downloadUrl) throws GeneralSecurityException, IOException {
        if (useSSL) {
            SSLUtils.initSSLContext(cert, key, ca);
        }
        String zipFile = SSLUtils.downloadFile(downloadUrl, filesDirectory);

        if (zipFile != null && FilesUtils.unzip(zipFile)) {
            diffOrLoad();
            FilesUtils.cleanup(filesDirectory);
        }
    }

    private void diffOrLoad() throws IOException {
        Map<String, File> latestFiles = FilesUtils.getLatestExtAndSer(filesDirectory);

        File ogFile = latestFiles.get("ser");
        File newFile = latestFiles.get("ext");

        Map<String, Professionnel> newPsMap = ProfessionnelMapper.getPsMapFromFile(newFile);

        if (ogFile == null) {
            pscRestApi.uploadPsMap(newPsMap);
        } else {
            // perform diff
            pscRestApi.diffPsMaps(ProfessionnelMapper.deserialiseFileToPsMap(ogFile), newPsMap);
        }
        // serialise latest extract
        ProfessionnelMapper.serialisePsMapToFile(newPsMap, filesDirectory + "/" + newFile.getName().replace(".txt", ".ser"));
    }

}
