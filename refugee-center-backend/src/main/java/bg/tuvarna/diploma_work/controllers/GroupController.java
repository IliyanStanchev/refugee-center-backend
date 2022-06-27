package bg.tuvarna.diploma_work.controllers;

import bg.tuvarna.diploma_work.exceptions.CustomResponseStatusException;
import bg.tuvarna.diploma_work.exceptions.InternalErrorResponseStatusException;
import bg.tuvarna.diploma_work.models.Group;
import bg.tuvarna.diploma_work.models.User;
import bg.tuvarna.diploma_work.models.UserGroup;
import bg.tuvarna.diploma_work.services.GroupService;
import bg.tuvarna.diploma_work.services.LogService;
import bg.tuvarna.diploma_work.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@CrossOrigin(origins = {"http://localhost:3000"})
@RestController
public class GroupController {

    @Autowired
    private GroupService groupService;

    @Autowired
    private UserService userService;

    @Autowired
    private LogService logService;

    @PostMapping("/create-group")
    public ResponseEntity<Void> createGroup(@RequestBody Group group) {

        if (group.getId() == null)
            group.setId(0L);

        if (group.getId() <= 0L)
            group.setCreationDate(LocalDate.now());

        Group databaseGroup = groupService.getGroupByEmail(group.getEmail());
        if (databaseGroup != null && databaseGroup.getId() != group.getId())
            throw new CustomResponseStatusException("Group with this email already exists");

        if (!groupService.validateGroupType(group))
            throw new CustomResponseStatusException("Users in this group do not match the group type.");

        if (groupService.createGroup(group) == null) {
            logService.logErrorMessage("GroupService::createGroup", String.valueOf(group.getEmail()));
            throw new InternalErrorResponseStatusException();
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/get-all-groups")
    public List<Group> getAllGroups() {
        return groupService.getAllGroups();
    }

    @GetMapping("/get-group-users/{id}")
    public List<User> getGroupUsers(@PathVariable long id) {
        return groupService.getGroupUsers(id);
    }

    @PostMapping("/get-users-for-adding")
    public List<UserGroup> getUsersForAdding(@RequestBody Group group) {

        return groupService.getUsersForAdding(group);
    }

    @PostMapping("/add-user-to-group")
    public ResponseEntity<Void> addUserToGroup(@RequestBody UserGroup userGroup) {

        User currentUser = userService.getUser(userGroup.getUser().getId());
        if (currentUser == null) {
            logService.logErrorMessage("UserService::getUser", String.valueOf(userGroup.getUser().getId()));
            throw new InternalErrorResponseStatusException();
        }

        Group currentGroup = groupService.getGroup(userGroup.getGroup().getId());
        if (currentGroup == null) {
            logService.logErrorMessage("GroupService::getGroup", String.valueOf(userGroup.getGroup().getId()));
            throw new InternalErrorResponseStatusException();
        }

        UserGroup newUserGroup = new UserGroup(currentUser, currentGroup);
        if (groupService.addUserGroup(newUserGroup) == null) {
            logService.logErrorMessage("GroupService::addUserGroup", String.valueOf(userGroup.getGroup().getId()));
            throw new InternalErrorResponseStatusException();
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/remove-user-from-group")
    @Transactional
    public ResponseEntity<Void> removeUserFromGroup(@RequestBody UserGroup userGroup) {

        groupService.removeUserFromGroup(userGroup);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/delete-group/{id}")
    @Transactional
    public ResponseEntity<Void> deleteGroup(@PathVariable long id) {

        if (!groupService.deleteGroup(id)) {
            logService.logErrorMessage("GroupService::deleteGroup", id);
            throw new InternalErrorResponseStatusException();
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }
}