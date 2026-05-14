package com.marathon.service;

import com.marathon.entity.User;
import com.marathon.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;  // 自动注入依赖
import org.springframework.stereotype.Service;               // 标记为 Service 层的 Bean
import java.util.List;

/**
 * 用户业务服务 —— 负责用户注册、登录、资料管理等核心逻辑
 *
 * 什么是 @Service？
 *   告诉 Spring："这是一个业务层的组件，请帮我创建并管理它"。
 *   有了这个注解，其他类才能通过 @Autowired 注入这个 Service。
 *
 * 什么是 @Autowired？
 *   自动注入——Spring 会自动找到对应的 Bean，然后赋值给这个字段。
 *   不需要你手动写 userMapper = new UserMapper()。
 *   这种模式叫"依赖注入"（DI），是 Spring 框架的核心思想。
 *
 * 分层架构中的 Service 层：
 *   Controller → Service → Mapper → 数据库
 *   Controller 只负责接收请求、校验登录、返回响应
 *   Service 负责真正"做事"——业务逻辑判断、数据校验、调 Mapper
 *   Mapper 只负责执行 SQL
 */
@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;  // Spring 自动注入 UserMapper 的代理对象

    /**
     * 登录验证
     * @return 验证通过返回用户对象，失败返回 null
     *
     * 三个验证条件缺一不可：
     *   1. 用户名存在
     *   2. 密码匹配（注意：这里是明文比较，生产环境应该用 BCrypt）
     *   3. 账号是正常状态（status=1，0 表示被管理员封禁了）
     */
    public User login(String username, String password) {
        User user = userMapper.selectByUsername(username);
        if (user != null && user.getPassword().equals(password) && user.getStatus() == 1) {
            return user;
        }
        return null;
    }

    /**
     * 用户注册
     * @return true=注册成功，false=用户名已存在
     *
     * 先查用户名是否被占用，未被占用才插入。
     * insert 返回值 > 0 表示插入了一条记录。
     */
    public boolean register(User user) {
        User exist = userMapper.selectByUsername(user.getUsername());
        if (exist != null) return false;  // 用户名已存在
        return userMapper.insert(user) > 0;
    }

    /** 获取普通用户列表（管理员功能），支持按姓名或用户名搜索 */
    public List<User> getUserList(String keyword) {
        return userMapper.selectList(keyword);
    }

    /** 启用/禁用用户（管理员功能） */
    public boolean updateUserStatus(Long id, Integer status) {
        return userMapper.updateStatus(id, status) > 0;
    }

    /** 管理员重置用户密码 */
    public boolean resetPassword(Long id, String newPassword) {
        return userMapper.updatePassword(id, newPassword) > 0;
    }

    /** 按 ID 查询用户 */
    public User getUserById(Long id) {
        return userMapper.selectById(id);
    }

    /** 用户自己修改资料（真实姓名、电话、身份证号） */
    public boolean updateProfile(User user) {
        return userMapper.updateProfile(user) > 0;
    }

    /**
     * 用户自己修改密码
     * @return true=修改成功，false=旧密码不正确
     *
     * 先验证旧密码是否正确，正确才允许修改。
     * 防止别人登录后改密码——如果你的 token 被偷了，对方还需要知道旧密码才能改。
     */
    public boolean changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userMapper.selectById(userId);
        if (user != null && user.getPassword().equals(oldPassword)) {
            return userMapper.updatePassword(userId, newPassword) > 0;
        }
        return false;
    }
}
