<?php

	require_once("secret.class.php");
	require_once("signal.class.php");
	
	class DataAccess
	{
		private static $ip = "localhost";
		private static function getConnection() {
			return new mysqli(DataAccess::$ip, Secret::$username, Secret::$password, "mpcp");
		}
		private static function hashSalt($pass) {
			return hash("sha256", $pass.Secret::$salt);
		} 

		public static function registerUser($uname, $pword) {
			$db = self::getConnection();
			$hash = self::hashSalt($pword);

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
			$stmt = $db->prepare('INSERT INTO users VALUES (null, ?, ?, null)');
			$stmt->bind_param('ss', $uname, $hash);
			if(!$stmt->execute()) {
				return Signal::$dbConnectionError;
			}
			return Signal::$success;
		}

		public static function logIn($uname, $pword) {
			$db = self::getConnection();
			
			$hash = self::hashSalt($pword);

			//Check if user exists
			$stmt = $db->prepare('SELECT userid FROM users WHERE username=? AND password=?');
			$stmt->bind_param('ss', $uname, $hash);
			if(!$stmt->execute()) {
				return Signal::$dbConnectionError;
			}
			$res = $stmt->get_result();
			$stmt->close();

			//Valid UN and pword
			if($res->num_rows == 1) {
				$row = $res->fetch_assoc();
				$userid = $row['userid'];

				//Check if user in table
				$res = $db->query("SELECT authcode FROM auth WHERE userid=$userid");

				//Generate a random authcode
				$random = openssl_random_pseudo_bytes(64);
				$authcode = self::hashSalt($random);

				if($res->num_rows >= 1) {
					$db->query("UPDATE auth SET expire DATE_ADD(NOW(), INTERVAL 1 MONTH) WHERE userid=$userid");
					$authcode = $res->fetch_assoc()['authcode'];
				} else {
					$db->query("INSERT INTO auth VALUES ( null, $userid, '$authcode', DATE_ADD(NOW(), INTERVAL 1 MONTH) )");
				}
				return new ISIGNAL($authcode, 1);
			}

			return Signal::$credentialError;
		}
		public static function logOut($authcode) {

		}
	}
?>