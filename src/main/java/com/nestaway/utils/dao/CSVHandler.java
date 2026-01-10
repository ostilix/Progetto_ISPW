package com.nestaway.utils.dao;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class CSVHandler{
    private final String filePath;
    private final String delimiter;

    public CSVHandler(String filePath, String delimiter) {
        this.filePath = filePath;
        this.delimiter = delimiter;
    }

    //scrittura in coda (append)
    public void writeAll(List<String[]> rs) throws IOException{
        //apro file in scrittura
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            for (String[] r : rs) {
                writer.write(String.join(delimiter, r));
                writer.newLine();
            }
            writer.flush();
        }
    }

    public List<String[]> readAll() throws IOException{
        List<String[]> rs = new ArrayList<>();
        //apro file in lettura
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            //leggo ogni riga
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue; //salto righe vuote
                }
                //divido la stringa in array
                rs.add(line.split(delimiter));
            }
        }
        return rs;
    }

    //sovrascrivo il file
    public void writeCleaned(List<String[]> rs) throws IOException{
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (String[] r : rs) {
                writer.write(String.join(delimiter, r));
                writer.newLine();
            }
            writer.flush();
        }
    }

    //leggo, filtro e ritono la lista
    public List<String[]> find(Predicate<String[]> predicate) throws IOException{
        return readAll().stream().filter(predicate).toList();
    }

    //elimino con un solo filtro
    public void remove(Predicate<String[]> predicate) throws IOException{
        List<String[]> filtered = readAll();
        filtered = filtered.stream().filter(predicate.negate()).toList();
        writeCleaned(filtered);
    }

    //elimino con più filtri(es notifiche)
    public void remove(List<Predicate<String[]>> predicates) throws IOException{
        List<String[]> filtered = readAll();
        for (Predicate<String[]> predicate : predicates) {
            filtered = filtered.stream().filter(predicate.negate()).toList();
        }
        writeCleaned(filtered);
    }
}

