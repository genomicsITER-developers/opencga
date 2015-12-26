/*
 * Copyright 2015 OpenCB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opencb.opencga.storage.app.service;

import org.opencb.opencga.core.common.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Created by jacobo on 23/10/14.
 */
public class OpenCGAStorageServiceMain {


    private static Properties properties;
    private static String opencgaHome;

    private static Logger logger = LoggerFactory.getLogger(OpenCGAStorageServiceMain.class);

    public static void main(String[] args) throws IOException {
        OptionParser optionParser = new OptionParser();

        String command = optionParser.parse(args);

        OptionParser.GeneralOptions opts = optionParser.getGeneralOptions();

        if (opts.help) {
            System.out.println(optionParser.usage());
            return;
        }

        //Get properties
        String propertyAppHome = System.getProperty("app.home");
        if (propertyAppHome != null) {
            opencgaHome = System.getProperty("app.home");
        } else {
            String envAppHome = System.getenv("OPENCGA_HOME");
            if (envAppHome != null) {
                opencgaHome = envAppHome;
            } else {
                opencgaHome = Paths.get("opencga-storage-app", "build").toString(); //If it has not been run from the shell script (debug)
            }
        }
        Config.setOpenCGAHome(opencgaHome);
        File configFile;
        if (opts.conf.isEmpty()) {
            configFile = Paths.get(opencgaHome, "conf", "storage-service.properties").toFile();
        } else {
            configFile = Paths.get(opts.conf).toFile();
        }
        if (!configFile.exists()) {
            throw new FileNotFoundException("File " + configFile.toString() + " not found");
        }
        properties = new Properties();
        properties.load(new FileInputStream(configFile));

        if (opts.port != 0) {
            properties.setProperty(OpenCGAStorageService.PORT, Integer.toString(opts.port));
        }

        int status;
        try {
            OpenCGAStorageService storageService = OpenCGAStorageService.newInstance(properties);
            storageService.start();
            status = storageService.join();
        } catch (Exception e) {
            e.printStackTrace();
            status = 3;
        }
        System.exit(status);
    }
}

