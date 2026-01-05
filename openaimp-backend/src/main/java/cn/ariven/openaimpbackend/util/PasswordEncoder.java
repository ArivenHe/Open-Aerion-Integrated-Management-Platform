package cn.ariven.openaimpbackend.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * 密码加密以及密码比较器
 *
 * @author 1228
 * @version 0.1-SNAPSHOT
 */
public class PasswordEncoder {

	public static String encode(String password) {
		return BCrypt.hashpw(password, BCrypt.gensalt(12));
	}

	public static boolean compare(String password, String encodedPassword) {
		return BCrypt.checkpw(password, encodedPassword);
	}

}
