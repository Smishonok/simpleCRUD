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

    public Region addRegion(String name) {
        log.debug("The operation of adding a new region with name: "+name+" is started.");
        Region region = regionRepository.add(new Region(getLastRegionId() + 1, name));
        log.debug("The operation was ended, region with id: "+region.getId()+" was added " +
                          "into repository.");
        return region;
    }

    public Optional<Region> getRegionById(String regionId) {
        long id = Long.parseLong(regionId);
        Optional<Region> region = this.regionRepository.isContains(id) ? Optional.of(
                this.regionRepository.get(id)) : Optional.empty();

        return region;
    }

    public Optional<Region> getRegionByName(String regionName) {
        List<Region> regionsList = this.regionRepository.getAll();

        int indexOfRequestedRegion = - 1;
        for (int i = 0; i < regionsList.size(); i++) {
            if (regionsList.get(i).getName().equals(regionName)) {
                indexOfRequestedRegion = i;
            }
        }

        Optional<Region> requestedRegion = indexOfRequestedRegion != - 1 ? Optional.of(
                regionsList.get(indexOfRequestedRegion)) : Optional.empty();
        return requestedRegion;
    }

    public boolean changeRegionName(String regionId, String newRegionName) {
        long id = Long.parseLong(regionId);
        if (this.regionRepository.isContains(id)) {
            Region region = this.regionRepository.get(id);
            region.setName(newRegionName);
            this.regionRepository.change(region);
        }
        return this.regionRepository.get(id).getName().equals(newRegionName);
    }

    public boolean removeRegionWithId(String regionId) {
        long id = Long.parseLong(regionId);
        boolean isRegionRemoved = regionRepository.remove(id);
        return isRegionRemoved;
    }

    public boolean removeAllRegions() {
        log.debug("Removing all regions from repository.");
        boolean isAllRegionsRemoved = regionRepository.removeAll();
        return isAllRegionsRemoved;
    }

    public List<Region> getAllRegions() {
        log.debug("Getting list of all regions from repository.");
        List<Region> regionList = regionRepository.getAll();
        return regionList;
    }

    private void initRegionRepository() throws ClassNotFoundException {
        log.debug("Starting initialisation of Region repository");
        regionRepository = RepositoryManager.getRepositoryFactory().getRegionRepository();
        log.debug("Region repository implementation is: " + regionRepository.getClass().getName());
    }

    private long getLastRegionId() {
        Optional<Long> lastRegionId = this.regionRepository.getAll().stream().map(Region::getId)
                                                           .max(Long::compareTo);
        return lastRegionId.isPresent() ? lastRegionId.get() : 0;
    }
}
