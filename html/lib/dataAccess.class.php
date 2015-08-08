<?php
	require_once("secret.class.php");
	require_once("signal.class.php");
	class DataAccess
	{
		private static $ip = "localhost";
		private static function getConnection() {
			return new mysqli(DataAccess::$ip, Secret::$username, Secret::$password, "mpcp");
		}
		private static function hashPwd($pass) {
			return hash("sha256", $pass.Secret::$salt);
		} 

		public static function registerUser($uname, $pword) {
			$db = self::getConnection();
			$hash = self::hashPwd($pword);

			//Check if user exists
			$stmt = $db->prepare('SELECT userid FROM users WHERE username=?');
			$stmt->bind_param('s', $uname);
			if(!$stmt->execute()) {
				return Signal::$dbConnectionError;
			}
			$res = $stmt->get_result();
			if($res->num_rows > 0)
				return Signal::$usernameTakenError;
			$stmt->close();

			//Insert user into database
			$stmt = $db->prepare('INSERT INTO users VALUES (null, ?, ?)');
			$stmt->bind_param('ss', $uname, $hash);
			if(!$stmt->execute()) {
				return Signal::$dbConnectionError;
			}
			return Signal::$success;
		}
	}
?>