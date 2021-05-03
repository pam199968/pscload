package fr.ans.psc.pscload.controller;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;

@RestController
class HelloController {

    /**
     * The logger.
     */
    private static final Logger log = LoggerFactory.getLogger(HelloController.class);

    @Value("${files.directory}")
    private String filesDirectory;

    @RequestMapping("/check")
    public String index() {
        return "health check";
    }

    @RequestMapping("/cleanup")
    public String cleanup() throws IOException {
        FileUtils.cleanDirectory(new File(filesDirectory));
        log.info("all files in {} were deleted", filesDirectory);
        return "cleaned";
    }

}
