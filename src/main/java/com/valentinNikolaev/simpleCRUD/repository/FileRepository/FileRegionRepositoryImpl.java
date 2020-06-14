package com.valentinNikolaev.simpleCRUD.repository.FileRepository;

import com.valentinNikolaev.simpleCRUD.models.Region;
import com.valentinNikolaev.simpleCRUD.repository.RegionRepository;
import org.apache.log4j.Logger;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

public class FileRegionRepositoryImpl implements RegionRepository {

    static Logger log = Logger.getLogger(FileRegionRepositoryImpl.class);

    private Path regionRepositoryPath;

    //Region`s fields names for parsing
    private final String REGION_ID   = "Post`s id:";
    private final String REGION_NAME = "User`s id:";

    public FileRegionRepositoryImpl(Path repositoryRootPath) {
        this.regionRepositoryPath = repositoryRootPath.resolve("regionRepository.txt");
        createRegionRepository();
    }

    private void createRegionRepository() {
        log.debug("Checking is repository file with region data exists.");
        if (! Files.exists(this.regionRepositoryPath)) {
            log.debug(
                    "Region repository does not exist, started the creation of a repository file.");
            try {
                Files.createFile(this.regionRepositoryPath);
            } catch (IOException e) {
                log.debug("File \"regionRepository.txt\" can`t be created: " + e.getMessage());
            }
            log.debug("The repository file with regions data created successfully");
        } else {
            log.debug("The repository file with regions data exists.");
        }
    }

    @Override
    public void add(Region region) {
        try {
            BufferedWriter writer = Files.newBufferedWriter(regionRepositoryPath,
                                                            Charset.forName("UTF-8"),
                                                            StandardOpenOption.WRITE,
                                                            StandardOpenOption.APPEND);
            writer.write(this.prepareDataForSerialisation(region));
        } catch (IOException e) {
            log.error("Can`t write the region`s data into repository file: " + e.getMessage());
        }

    }

    @Override
    public Region get(Long regionId) {
        Optional<Region> region = Optional.empty();

        try {
            region = Files.lines(regionRepositoryPath).filter(
                    regionData->this.parseRegionId(regionData) == regionId).map(this::parseRegion)
                          .findFirst();
        } catch (IOException e) {
            log.error("Users`s repository file can`t be opened and read: " + e.getMessage());
        }

        if (! region.isEmpty()) {
            return region.get();
        } else {
            throw new IllegalArgumentException(
                    "Region with id " + regionId + "' does not contain in " + "database.");
        }
    }

    @Override
    public void change(Region region) {
        List<Region> regionsList = getAll();

        int indexOfRegionInRegionsList = - 1;
        for (int i = 0; i < regionsList.size(); i++) {
            if (regionsList.get(i).getId() == region.getId()) {
                indexOfRegionInRegionsList = i;
            }
        }

        if (indexOfRegionInRegionsList == - 1) {
            throw new IllegalArgumentException(
                    "Region with ID: " + region.getId() + " is not contains in repository.");
        }

        regionsList.set(indexOfRegionInRegionsList, region);

        rewriteInRepository(regionsList.stream().map(this::prepareDataForSerialisation)
                                       .collect(Collectors.toList()));
    }

    @Override
    public void remove(Long regionId) {
        List<String> regionsList = getRegionsListExcludeRegionWith(regionId);
        rewriteInRepository(regionsList);
    }

    @Override
    public List<Region> getAll() {
        return getRegionsListExcludeRegionWith(0).stream().map(this::parseRegion).collect(
                Collectors.toList());
    }

    @Override
    public void removeAll() {
        if (Files.exists(regionRepositoryPath)) {
            try {
                Files.delete(regionRepositoryPath);
            } catch (IOException e) {
                log.error("The repository file can`t be deleted: " + e.getMessage());
            }
        }

        try {
            Files.createFile(regionRepositoryPath);
        } catch (IOException e) {
            log.error("The repository file can`t be created: " + e.getMessage());
        }
    }

    @Override
    public boolean contains(Long regionId) {
        boolean isExists = false;

        try {
            isExists = Files.lines(regionRepositoryPath).anyMatch(
                    regionData->this.parseRegionId(regionData) == regionId);
        } catch (IOException e) {
            log.error("Can`t read repository file with region data: " + e.getMessage());
        }
        return isExists;
    }

    private List<String> getRegionsListExcludeRegionWith(long regionId) {
        List<String> regionsList = new ArrayList<>();
        try {
            regionsList = Files.lines(regionRepositoryPath).filter(
                    regionData->this.parseRegionId(regionData) != regionId).collect(Collectors.toList());
        } catch (IOException e) {
            log.error("Can`t read repository file with regions data: " + e.getMessage());
        }
        return regionsList;
    }

    private void rewriteInRepository(List<String> regionsList) {
        try (BufferedWriter writer = Files.newBufferedWriter(regionRepositoryPath,
                                                             Charset.forName("UTF-8"),
                                                             StandardOpenOption.WRITE)) {
            for (String region : regionsList) {
                writer.write(region);
            }
        } catch (IOException e) {
            log.error("Can`t write in repository file with regions data: " + e.getMessage());
        }
    }

    private String prepareDataForSerialisation(Region region) {
        return REGION_ID + region.getId() + ";" + REGION_NAME + region.getName() + ";\n";
    }

    private long parseRegionId(String regionData) {
        Scanner scanner = getScanner(regionData);

        scanner.findInLine(REGION_ID);
        if (scanner.hasNextLong()) {
            return scanner.nextLong();
        } else {
            throw new IllegalArgumentException(
                    "Invalid data. The string does not contain the user Id.");
        }
    }

    private String parseRegionName(String regionData) {
        Scanner scanner = getScanner(regionData);

        scanner.findInLine(REGION_NAME);
        if (scanner.hasNext()) {
            return scanner.next();
        } else {
            throw new IllegalArgumentException(
                    "Invalid data. The string does not contain the user Id.");
        }
    }

    private Region parseRegion(String regionData) {
        if (regionData.isBlank() || regionData.isEmpty()) {
            throw new IllegalArgumentException(
                    "String with region`s data for parsing can`t be " + "empty");
        }

        long   id   = parseRegionId(regionData);
        String name = parseRegionName(regionData);

        return new Region(id, name);
    }

    private Scanner getScanner(String recourse) {
        Scanner scanner = new Scanner(recourse);
        scanner.useDelimiter(";");
        return scanner;
    }
}
