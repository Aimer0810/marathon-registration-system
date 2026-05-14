package com.marathon.mapper;

import com.marathon.entity.User;
import org.apache.ibatis.annotations.Mapper;        // 标记这是 MyBatis 的 Mapper 接口
import org.apache.ibatis.annotations.Param;        // 给参数起名，让 XML 能通过名字引用
import java.util.List;

/**
 * 用户数据访问接口（Mapper）
 *
 * 什么是 Mapper？
 *   MyBatis 框架中，Mapper 是专门负责和数据库交互的接口。
 *   你不用写实现类，MyBatis 会自动生成代理对象来执行对应的 SQL。
 *
 * 接口方法怎么和 SQL 对应？
 *   两种方式：
 *   1. XML 映射（本项目采用）—— 在 src/main/resources/mapper/UserMapper.xml 中
 *      写同名 SQL。比如接口叫 selectByUsername，XML 里就有一个 id="selectByUsername" 的 SQL。
 *   2. 注解方式 —— 直接在方法上写 @Select("SELECT * FROM user WHERE ...")
 *
 * XML 方式的优点是 SQL 和 Java 代码分开，复杂的 SQL 写得清楚。
 *
 * @Param 注解：
 *   方法的参数名在编译后默认会丢失（Java 的历史问题），
 *   加了 @Param("username") 后，XML 里就能用 #{username} 引用这个参数。
 *   单个参数时可以不写，MyBatis 会自动处理。
 */
@Mapper
public interface UserMapper {
    User selectByUsername(String username);                        // 根据用户名查用户（登录用）
    int insert(User user);                                         // 新增用户（注册用），返回受影响行数
    List<User> selectList(@Param("keyword") String keyword);        // 普通用户列表（支持搜索）
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);  // 启用/禁用账号
    int updatePassword(@Param("id") Long id, @Param("password") String password); // 改密码
    User selectById(Long id);                                     // 根据 ID 查用户
    int updateProfile(User user);                                  // 更新用户资料（姓名、电话、身份证）
}
