package fr.ans.psc.pscload.component;

import fr.ans.psc.pscload.component.utils.FilesUtils;
import fr.ans.psc.pscload.component.utils.SSLUtils;
import fr.ans.psc.pscload.mapper.Loader;
import fr.ans.psc.pscload.mapper.Serializer;
import fr.ans.psc.pscload.model.Professionnel;
import fr.ans.psc.pscload.model.Structure;
import fr.ans.psc.pscload.service.PscRestApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;

/**
 * The type Loader.
 */
@Component
public class Parser {

    @Autowired
    private PscRestApi pscRestApi;

    @Autowired
    private Serializer serializer;

    @Autowired
    private Loader loader;

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

    /**
     * Download and parse.
     *
     * @param downloadUrl the download url
     * @throws GeneralSecurityException the general security exception
     * @throws IOException              the io exception
     */
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

    public void diffOrLoad() throws IOException {
        Map<String, File> latestFiles = FilesUtils.getLatestExtAndSer(filesDirectory);

        File ogFile = latestFiles.get("ser");
        File newFile = latestFiles.get("txt");

        loader.loadFileToMap(newFile);

        Map<String, Professionnel> newPsMap = loader.getPsMap();
        Map<String, Structure> newStructureMap = loader.getStructureMap();

        // serialise latest extract
        serializer.serialiseMapsToFile(newPsMap, newStructureMap,
                filesDirectory + "/" + newFile.getName().replace(".txt", ".ser"));

        if (ogFile == null) {
            // first load
            pscRestApi.uploadPsMap(newPsMap);
            pscRestApi.uploadStructureMap(newStructureMap);
        } else {
            // perform diff
            serializer.deserialiseFileToMaps(ogFile);
            pscRestApi.diffPsMaps(serializer.getPsMap(), newPsMap);
            pscRestApi.diffStructureMaps(serializer.getStructureMap(), newStructureMap);
        }
    }

}
