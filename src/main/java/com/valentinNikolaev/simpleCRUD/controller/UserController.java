package com.valentinNikolaev.simpleCRUD.controller;

import com.valentinNikolaev.simpleCRUD.models.Region;
import com.valentinNikolaev.simpleCRUD.models.Role;
import com.valentinNikolaev.simpleCRUD.models.User;
import com.valentinNikolaev.simpleCRUD.repository.RepositoryManager;
import com.valentinNikolaev.simpleCRUD.repository.UserRepository;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class UserController {

    static Logger log = Logger.getLogger(UserController.class.getName());

    private UserRepository   usersRepository;
    private RegionController regionController;

    public UserController() throws ClassNotFoundException {
        this.regionController = new RegionController();
        initUserRepository();
    }

    private void initUserRepository() throws ClassNotFoundException {
        log.debug("Starting initialisation of User repository");
        usersRepository = RepositoryManager.getRepositoryFactory().getUserRepository();
        log.debug("User repository implementation is: " + usersRepository.getClass().getName());
    }

    public User addUser(String firstName, String lastName, String regionName) {
        long   userId = getLastUserId() + 1;
        Region region = regionController.getRegionByName(regionName).get();
        User   user   = this.usersRepository.add(new User(userId, firstName, lastName, region));
        return user;
    }

    public User addUser(String firstName, String lastName, String roleName, String regionName) {
        long   userId = getLastUserId() + 1;
        Region region = regionController.getRegionByName(regionName).get();
        Role   role   = Role.valueOf(roleName);
        User   user   = this.usersRepository.add(
                new User(userId, firstName, lastName, region, role));
        return user;
    }

    public Optional<User> getUserById(String id) {
        long userId = Long.parseLong(id);
        Optional<User> user = this.usersRepository.isContains(userId) ? Optional.of(
                this.usersRepository.get(userId)) : Optional.empty();

        return user;
    }

    public List<User> getAllUsersList() {
        return this.usersRepository.getAll();
    }

    public List<User> getUsersWithFirstName(String firstName) {
        return this.usersRepository.getAll().stream().filter(
                user->user.getFirstName().equals(firstName)).collect(Collectors.toList());
    }

    public List<User> getUsersWithLastName(String lastName) {
        return this.usersRepository.getAll().stream().filter(
                user->user.getLastName().equals(lastName)).collect(Collectors.toList());
    }

    public List<User> getUsersWithRole(String roleName) {
        return this.usersRepository.getAll().stream().filter(
                user->user.getRole().toString().equals(roleName)).collect(Collectors.toList());
    }

    public List<User> getUsersFrom(String regionName) {
        return this.usersRepository.getAll().stream().filter(
                user->user.getRegion().getName().equals(regionName)).collect(Collectors.toList());
    }

    public boolean changeUserFirstName(String userId, String newUserFirstName) {
        long id = Long.parseLong(userId);
        if (this.usersRepository.isContains(id)) {
            User user = this.usersRepository.get(id);
            user.setFirstName(newUserFirstName);
            this.usersRepository.change(user);
        }
        return newUserFirstName.equals(this.usersRepository.get(id).getFirstName());
    }

    public boolean changeUserLastName(String userId, String newUserLastName) {
        long id = Long.parseLong(userId);
        if (this.usersRepository.isContains(id)) {
            User user = this.usersRepository.get(id);
            user.setLastName(newUserLastName);
            this.usersRepository.change(user);
        }
        return newUserLastName.equals(this.usersRepository.get(id).getLastName());
    }

    public boolean changeUserRole(String userId, String newUserRole) {
        long id = Long.parseLong(userId);
        if (this.usersRepository.isContains(id)) {
            User user = this.usersRepository.get(id);
            user.changeUserRole(newUserRole);
            this.usersRepository.change(user);
        }
        return newUserRole.equals(this.usersRepository.get(id).getRole().toString());
    }

    public boolean changeUserRegion(String userId, String regionName) {
        long   id     = Long.parseLong(userId);
        Region region = regionController.getRegionByName(regionName).get();
        if (this.usersRepository.isContains(id)) {
            User user = this.usersRepository.get(id);
            user.setRegion(region);
            this.usersRepository.change(user);
        }
        return regionName.equals(this.usersRepository.get(id).getRegion().getName());
    }

    public boolean removeUser(String userId) {
        return this.usersRepository.remove(Long.parseLong(userId));
    }

    public boolean removeAllUsers() {
        return this.usersRepository.removeAll();
    }

    private long getLastUserId() {
        Optional<Long> maxUserId  = getAllUsersList().stream().map(User::getId).max(
                Long::compareTo);
        return maxUserId.isPresent() ? maxUserId.get() : 0;
    }
}
