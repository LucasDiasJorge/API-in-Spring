package com.project.core.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;


@RestController
@RequestMapping("/integration")
public class IntegrationController {


    @GetMapping("/event")
    public String event() throws IOException {

        BufferedReader objReader = null;
        StringBuilder ret = new StringBuilder();
        try {
            String strCurrentLine;

            objReader = new BufferedReader(new FileReader("loremipsum.txt"));

            while ((strCurrentLine = objReader.readLine()) != null) {

                ret.append(strCurrentLine);
            }

        } catch (IOException e) {

            e.printStackTrace();

        } finally {

            try {
                if (objReader != null)
                    objReader.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        return ret.toString();
    }

}
