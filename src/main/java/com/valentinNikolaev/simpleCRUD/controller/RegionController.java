package com.valentinNikolaev.simpleCRUD.controller;

import com.valentinNikolaev.simpleCRUD.models.Region;
import com.valentinNikolaev.simpleCRUD.repository.RegionRepository;
import com.valentinNikolaev.simpleCRUD.repository.RepositoryManager;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Optional;

public class RegionController {

    static Logger log = Logger.getLogger(RegionController.class.getName());

    private RegionRepository regionRepository;

    public RegionController() throws ClassNotFoundException {
        initRegionRepository();
    }

    private void initRegionRepository() throws ClassNotFoundException {
        log.debug("Starting initialisation of Region repository");
        regionRepository = RepositoryManager.getRepositoryFactory().getRegionRepository();
        log.debug("Region repository implementation is: " + regionRepository.getClass().getName());
    }

    public void addRegion(String name) {
        log.debug("Adding new region into repository.");
        Region region = new Region(name);
        regionRepository.add(region);
        log.debug("Adding the new region into the repository ended successfully.");
    }

    public Optional<Region> getRegionById(String regionId) {
        long id = Long.parseLong(regionId);
        Optional<Region> region = this.regionRepository.contains(id) ? Optional.of(
                this.regionRepository.get(id)) : Optional.empty();

        return region;
    }

    public Optional<Region> getRegionByName(String regionName) {
        List<Region>     regionsList     = this.regionRepository.getAll();

        int indexOfRequestedRegion = - 1;
        for (int i = 0; i < regionsList.size(); i++) {
            if (regionsList.get(i).getName().equals(regionName)) {
                indexOfRequestedRegion = i;
            }
        }

        Optional<Region> requestedRegion = indexOfRequestedRegion != 1 ? Optional.of(
                regionsList.get(indexOfRequestedRegion)) : Optional.empty();
        return requestedRegion;
    }

    public void changeRegionName(String regionId, String newRegionName) {
        long id = Long.parseLong(regionId);
        if (this.regionRepository.contains(id)) {
            Region region = this.regionRepository.get(id);
            region.setName(newRegionName);
            this.regionRepository.change(region);
        }
    }

    public void removeRegionWithId(String regionId) {
        long id = Long.parseLong(regionId);
        log.debug("Removing the region with name '" + id + "' from repository.");
        regionRepository.remove(id);
        log.debug("Removing operation is ended.");
    }

    public void removeAllRegions() {
        log.debug("Removing all regions from repository.");
        regionRepository.removeAll();
    }

    public List<Region> getAllRegions() {
        log.debug("Getting list of all regions from repository.");
        List<Region> regionList = regionRepository.getAll();
        return regionList;
    }
}
