package fr.ans.psc.pscload.controller;

import fr.ans.psc.pscload.component.Loader;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;

@RestController
class HelloController {

    /**
     * The logger.
     */
    private static final Logger log = LoggerFactory.getLogger(HelloController.class);

    @Autowired
    private final Loader loader;

    @Value("${files.directory}")
    private String filesDirectory;

    @Value("${test.download.url}")
    private String testDownloadUrl;

    @Value("${extract.download.url}")
    private String extractDownloadUrl;

    HelloController(Loader loader) {
        this.loader = loader;
    }

    @GetMapping(value = "/check", produces = MediaType.APPLICATION_JSON_VALUE)
    public String index() {
        return "health check OK";
    }

    @GetMapping(value = "/cleanup", produces = MediaType.APPLICATION_JSON_VALUE)
    public String cleanup() throws IOException {
        FileUtils.cleanDirectory(new File(filesDirectory));
        log.info("all files in {} were deleted", filesDirectory);
        return "cleaned";
    }

    @GetMapping(value = "/load", produces = MediaType.APPLICATION_JSON_VALUE)
    public String load() throws IOException, GeneralSecurityException {
        log.info("loading from {}", extractDownloadUrl);
        loader.downloadAndParse(extractDownloadUrl);
        log.info("loading complete!");
        return "loading complete";
    }

    @GetMapping(value = "/loadTest", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String loadTest(@RequestParam String fileName) throws GeneralSecurityException, IOException {
        String downloadUrl = testDownloadUrl + fileName + ".zip";
        log.info("loading from {}", downloadUrl);
        loader.downloadAndParse(downloadUrl);
        return "loading complete";
    }

}
