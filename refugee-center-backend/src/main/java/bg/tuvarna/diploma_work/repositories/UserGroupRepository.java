package bg.tuvarna.diploma_work.repositories;

import bg.tuvarna.diploma_work.models.User;
import bg.tuvarna.diploma_work.models.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserGroupRepository extends JpaRepository<UserGroup, Long> {


    @Query("SELECT u.user FROM UserGroup u WHERE u.group.id = ?1")
    List<User> getUsersByGroupId(long id);

    @Query(value = "SELECT DISTINCT ON (ug.user_id) ug.* FROM users_groups ug JOIN users u on ug.user_id = u.id JOIN roles r on u.role_id = r.id WHERE ug.user_id NOT IN ( SELECT user_id FROM users_groups WHERE group_id = ?1 )", nativeQuery = true)
    List<UserGroup> getAllUsersForAdding(Long id);

    @Query(value = "SELECT DISTINCT ON (ug.user_id) ug.* FROM users_groups ug JOIN users u on ug.user_id = u.id JOIN roles r on u.role_id = r.id WHERE ug.user_id NOT IN ( SELECT user_id FROM users_groups WHERE group_id = ?1 ) AND r.role_type = 2", nativeQuery = true)
    List<UserGroup> getRefugeesForAdding(Long id);

    @Query(value = "SELECT DISTINCT ON (ug.user_id) ug.* FROM users_groups ug JOIN users u on ug.user_id = u.id JOIN roles r on u.role_id = r.id WHERE ug.user_id NOT IN ( SELECT user_id FROM users_groups WHERE group_id = ?1 ) AND ( r.role_type = 0 OR r.role_type = 1 )", nativeQuery = true)
    List<UserGroup> getEmployeesForAdding(Long id);

    @Modifying
    @Query("DELETE FROM UserGroup WHERE group.id = ?1 AND user.id = ?2")
    void removeUserFromGroup(Long groupId, Long userId);

    @Query("SELECT COUNT(*) FROM UserGroup ug WHERE ug.group.id = ?1 AND ug.user.role.roleType = 2")
    long getRefugeesCount(Long groupId);

    @Query("SELECT COUNT(*) FROM UserGroup ug WHERE ug.group.id = ?1 AND ( ug.user.role.roleType = 0 OR ug.user.role.roleType = 1 )")
    long getEmployeesCount(Long id);

    @Modifying
    @Query("DELETE FROM UserGroup WHERE group.id = ?1")
    void deleteGroup(long groupId);
}
