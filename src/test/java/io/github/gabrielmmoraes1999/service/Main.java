package io.github.gabrielmmoraes1999.service;

import io.github.gabrielmmoraes1999.apiservice.ApplicationServer;

public class Main {

    public static void main(String[] args) throws Exception {
        ApplicationServer.run(Main.class, 80);
    }

}
