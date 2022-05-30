package bg.tuvarna.diploma_work.services;

import bg.tuvarna.diploma_work.enumerables.GroupType;
import bg.tuvarna.diploma_work.models.Group;
import bg.tuvarna.diploma_work.models.User;
import bg.tuvarna.diploma_work.models.UserGroup;
import bg.tuvarna.diploma_work.repositories.GroupRepository;
import bg.tuvarna.diploma_work.repositories.UserGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GroupService {

    private static final String ALL_REFUGEES_GROUP_MAIL     = "all.refugees@safe_shelter.com";
    private static final String ALL_EMPLOYEES_GROUP_MAIL    = "all.employees@safe_shelter.com";
    private static final String ALL_COMMON_GROUP_MAIL       = "all.common@safe_shelter.com";

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserGroupRepository userGroupRepository;

    public List<Group> getAllGroups(){
        return groupRepository.findAll();
    }

    public List<User> getGroupUsers(long id) { return userGroupRepository.getUsersByGroupId(id);
    }

    public Group getGroupByEmail( String email ) {
        return groupRepository.getByEmail( email );
    }

    public Group getAllRefugeesGroup( ){
        return getGroupByEmail(ALL_REFUGEES_GROUP_MAIL);
    }

    public Group getAllEmployeesGroup(){
        return getGroupByEmail(ALL_EMPLOYEES_GROUP_MAIL);
    }

    public Group getAllCommonGroup(){
        return getGroupByEmail(ALL_COMMON_GROUP_MAIL);
    }

    public boolean addEmployeeToGroups(User savedUser) {

        if( !addToCommonGroup(savedUser) )
            return false;

        Group employeeGroup = getAllEmployeesGroup();

        if( employeeGroup == null )
            return false;

        UserGroup userGroup = new UserGroup(savedUser, employeeGroup);
        if( userGroupRepository.save(userGroup) == null )
            return false;

        return true;
    }

    public boolean addRefugeeToGroups(User savedUser){

        if( !addToCommonGroup(savedUser) )
            return false;

        Group refugeeGroup = getAllRefugeesGroup();

        if( refugeeGroup == null )
            return false;

        UserGroup userGroup = new UserGroup(savedUser, refugeeGroup);
        if( userGroupRepository.save(userGroup) == null )
            return false;

        return true;
    }

    public boolean addToCommonGroup(User user){

        Group commonGroup = getAllCommonGroup();

        if( commonGroup == null )
            return false;

        UserGroup userGroup = new UserGroup(user, commonGroup);
        if( userGroupRepository.save(userGroup) == null )
            return false;

        return true;
    }

    public List<UserGroup> getUsersForAdding(Group group) {

        if( group.getGroupType() == GroupType.Common)
            return userGroupRepository.getAllUsersForAdding(group.getId());

        if( group.getGroupType() == GroupType.Employees)
            return userGroupRepository.getEmployeesForAdding(group.getId());

        return userGroupRepository.getRefugeesForAdding(group.getId());
    }

    public Group getGroup(Long id) {

        Optional<Group> optionalGroup =  groupRepository.findById(id);

        if( optionalGroup.isPresent() )
            return optionalGroup.get();

        return null;
    }

    public UserGroup addUserGroup(UserGroup userGroup) {

        return userGroupRepository.save(userGroup);
    }

    public void removeUserFromGroup(UserGroup userGroup) {

        userGroupRepository.removeUserFromGroup(userGroup.getGroup().getId(), userGroup.getUser().getId());
    }

    public Group createGroup(Group group) {

        return groupRepository.save(group);
    }

    public boolean validateGroupType(Group group) {

        if( group.getGroupType() == GroupType.Common)
            return true;

        long invalidUsersCount = 0;

        if( group.getGroupType() == GroupType.Employees)
            invalidUsersCount = userGroupRepository.getRefugeesCount(group.getId());

        if( group.getGroupType() == GroupType.Refugees)
            invalidUsersCount = userGroupRepository.getEmployeesCount(group.getId());

        if( invalidUsersCount > 0 )
            return false;

        return true;
    }

    public boolean deleteGroup(long groupId) {

        userGroupRepository.deleteGroup(groupId);
        groupRepository.deleteById(groupId);

        return true;
    }
}
